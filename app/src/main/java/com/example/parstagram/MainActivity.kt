package com.example.parstagram

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.parse.*
import java.io.File

/**
 * Let user create a post by taking a photo with their camera
 */
class MainActivity : AppCompatActivity() {

    val CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034
    val photoFileName = "photo.jpg"
    var photoFile: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Setting the description of the post
        // 2. A button to launch the camera to take a picture
        // 3. An ImageView to show the picture the user has taken
        // 4. A button to save and send the post to our Parse server

        findViewById<Button>(R.id.btnSubmit).setOnClickListener {
            // send post to server without an image
            // Get the description that they have inputted
            val description = findViewById<EditText>(R.id.etDescription).text.toString()
            val user = ParseUser.getCurrentUser()
            if (photoFile != null) {
                // !! means "hey, we're guaranteed that it's not null here
                submitPost(description, user, photoFile!!)
            } else {
                Log.e(TAG, "photoFile is null")
                Toast.makeText(this, "photoFile is null", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.btnTakePicture).setOnClickListener {
            // Launch camera to let user take picture
            onLaunchCamera()
        }

        findViewById<Button>(R.id.btnLogOut).setOnClickListener {
            Log.i(TAG, "attempting to logOut")
            ParseUser.logOut()
            // Navigate user to the MainActivity because we don't want to keep them on
            // this LoginActivity anymore.
            goToLoginActivity()
        }
    }


    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

        // Hitting the back button from the MainActivity will exit the app instead of navigating
        // the user back to the LoginActivity
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                val takenImage = BitmapFactory.decodeFile(photoFile!!.absolutePath)
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                val ivPreview: ImageView = findViewById(R.id.imageView)
                ivPreview.setImageBitmap(takenImage)
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Returns the File for a photo stored on disk given the fileName
    fun getPhotoFileUri(fileName: String): File {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        val mediaStorageDir =
            File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG)

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(TAG, "failed to create directory")
        }

        // Return the file target for the photo based on filename
        return File(mediaStorageDir.path + File.separator + fileName)
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
                FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile!!)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(packageManager) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE)
            }
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
                Log.e(TAG, "Error while saving post")
                exception.printStackTrace()
                Toast.makeText(this, "Error while saving post", Toast.LENGTH_SHORT).show()
            } else {
                // Post is saved successessfully
                Log.i(TAG, "Successfully saved post")
                // TODO: Reseting the EditText field to be empty so that we can fill in our data
                // TODO: Reset the ImageView to be empty so that we can fill in our image
            }
        }
    }


    // Query for all posts in our sever
    fun queryPosts() {

        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        // when we make this query for all the posts, we're telling Parse to also give us back the
        // User that's associated with each post so that we can use to do whatever we want inside
        // our app.
        query.include(Post.KEY_USER)
        // findInBackground takes in a callback that tells us what to do once
        // this network request has completed
        // We're telling Parse to find all the Post objects in our server and return those to us.
        // Once that network call is completed, and we have a result, what we actually want to
        // do can be done in the "done" method of the callback.
        query.findInBackground(object : FindCallback<Post> {
            override fun done(posts: MutableList<Post>?, e: ParseException?) {
                if (e != null) {
                    // Something has went wrong
                    Log.e(TAG, "Error fetching posts")
                } else {
                    if (posts != null) {
                        for (post in posts) {
                            Log.i(TAG, "Post: " + post.getDescription() + " , username: " +
                            post.getUser()?.username)
                        }
                    }
                }
            }

        })
    }


    companion object {
        const val TAG = "MainActivity"
    }
}