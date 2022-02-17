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
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.parstagram.fragments.ComposeFragment
import com.example.parstagram.fragments.FeedFragment
import com.example.parstagram.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.parse.*
import java.io.File

/**
 * Let user create a post by taking a photo with their camera
 */
class MainActivity : AppCompatActivity() {

    lateinit var bottom_navigation : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val fragmentManager: FragmentManager = supportFragmentManager

        findViewById<Button>(R.id.btnLogOut).setOnClickListener {
            Log.i(TAG, "attempting to logOut")
            ParseUser.logOut()
            // Navigate user to the MainActivity because we don't want to keep them on
            // this LoginActivity anymore.
            goToLoginActivity()
        }

        bottom_navigation = findViewById(R.id.bottom_navigation)
        bottom_navigation.setOnItemSelectedListener {
            // item -> meaning: Instead of calling the generic item called "it" that's passed into
            // this function, call that generic item "item" to make our code more readable
            item ->

            var fragmentToShow: Fragment? = null
            when (item.itemId) {

                R.id.action_home -> {
                    fragmentToShow = FeedFragment()
                }
                R.id.action_compose -> {
                    fragmentToShow = ComposeFragment()
                }
                R.id.id_profile -> {
                    fragmentToShow = ProfileFragment()
                }
            }

            if (fragmentToShow != null) {
                // call commit so that things happen
                // replace the container in the layout file with the fragment that we want to show
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragmentToShow).commit()
            }

            // Return true to say that we've handled this user interaction on the item
            true
        }

        // Set default fragment to show if none of the button on bottom navigation bar is clicked
        bottom_navigation.selectedItemId = R.id.action_home
    }


    private fun goToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

        // Hitting the back button from the LoginActivity will minimize the app instead of navigating
        // the user back to the MainActivity
        finish()
    }


    companion object {
        const val TAG = "MainActivity"
    }
}