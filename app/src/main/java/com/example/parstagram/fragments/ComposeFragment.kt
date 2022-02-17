package com.example.parstagram.fragments

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.parstagram.MainActivity
import com.example.parstagram.Post
import com.example.parstagram.R
import com.parse.*
import java.io.File

class ComposeFragment : Fragment() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    lateinit var  ivPreview: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false)
    }

    // Fragment is different from Activity in that:
    // instead of setting both which layout file to use and setting onClickListeners in
    // the same method, we have to set which layout file to use in onCreateView
    // and set onClickListeners in onViewCreated

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Set onClickListeners and setup logic

        ivPreview = view.findViewById(R.id.imageView)

        view.findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            // send post to server without an image
            // Get the description that they have inputted
            val description = view.findViewById<EditText>(R.id.etDescription).text.toString()
            val user = ParseUser.getCurrentUser()
            if (photoFile != null) {
                // !! means "hey, we're guaranteed that it's not null here
                submitPost(description, user, photoFile!!)
            } else {
                Log.e(MainActivity.TAG, "photoFile is null")
                Toast.makeText(requireContext(), "photoFile is null", Toast.LENGTH_SHORT).show()
            }
        }

        view.findViewById<Button>(R.id.btnTakePicture).setOnClickListener {
            // Launch camera to let user take picture
            onLaunchCamera()
        }

    }


    // Send a Post object to our Parse server
    fun submitPost(description: String, user: ParseUser, file: File) {
        // Create the Post object to send to the server
        val post = Post()
        post.setDescription(description)
        post.setUser(user)
        post.setImage(ParseFile(file))
        // saveInBackground says: hey, take this parse object that we've created and we set fields
        // for and send it to our Parse server.
        // saveInBackground returns an exception if sth went wrong
        // Since what we're doing is a network request, we
        // want to do it in the background to make sure we're not making the UI freeze for the user.
        post.saveInBackground { exception ->
            if (exception != null) {
                // Something has went wrong
                Log.e(MainActivity.TAG, "Error while saving post")
                exception.printStackTrace()
                Toast.makeText(requireContext(), "Error while saving post", Toast.LENGTH_SHORT).show()
            } else {
                // Post is saved successessfully
                Log.i(MainActivity.TAG, "Successfully saved post")
                // TODO: Reseting the EditText field to be empty so that we can fill in our data
                // TODO: Reset the ImageView to be empty so that we can fill in our image
            }
        }
    }


    // Launch an camera app using Intent
    fun onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName)

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        if (photoFile != null) {
            val fileProvider: Uri =
                // Instead of using "this" as in an activity,
                // requireContext() is what we use to get reference to the Context in a fragment.
                FileProvider.getUriForFile(requireContext(), "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(requireContext().packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
        }
    }


    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), MainActivity.TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(MainActivity.TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
    }





    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == AppCompatActivity.RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPreview.setImageBitmap(takenImage)
            } else { // Result was a failure
                Toast.makeText(requireContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}