package com.example.parstagram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.parse.ParseUser


class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Before setting up and showing the LoginActivity
        // check if there's a user logged in
        // If there is, take them to MainActivity
        if (ParseUser.getCurrentUser() != null) {
            goToMainActivity()
        }

        findViewById<Button>(R.id.bLogin).setOnClickListener {
            val username = findViewById<EditText>(R.id.etUsername).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()
            loginUser(username, password)
        }

        findViewById<Button>(R.id.signupBtn).setOnClickListener {
            val username = findViewById<EditText>(R.id.etUsername).text.toString()
            val password = findViewById<EditText>(R.id.etPassword).text.toString()
            signUpUser(username, password)
        }

    }


    // sign the user up into our Parse server using the username and password the user put in
    // make a callback e to see if we successfully signed the user up
    private fun signUpUser(username: String, password: String) {
        // Create the ParseUser
        val user = ParseUser()

        // Set fields for the user to be created
        user.setUsername(username)
        user.setPassword(password)

        user.signUpInBackground { e ->
            if (e == null) {
                // User has successfully created a new account

                // Navigate user to the MainActivity because we don't want to keep them on
                // this LoginActivity anymore.
                goToMainActivity()

                // Show a toast to indicate that user successfully signed up for an account
                Toast.makeText(this, "Successfully signed up", Toast.LENGTH_SHORT).show()

            } else {
                // Show a toast to tell user sign up was not successful
                Toast.makeText(this, "Error signing up", Toast.LENGTH_SHORT).show()

                // Sign up didn't succeed. Look at the ParseException
                // to figure out what went wrong
                e.printStackTrace()
            }
        }
    }


    // Everytime we make a call to our Parse server, we're basically making a network call
    // which takes an undetermined amount of time. So, if a network call takes a really long time,
    // we don't want to freeze the UI of our Android app. So, instead of making the call on the
    // main UI thread, we want to make the call on the background thread so that users can still
    // interact with our app as we're making the call to log the user in.
    private fun loginUser(username: String, password: String) {
        // our callback that lets us know whether the user successfully logged in involves 2
        // e is the exception that comes back if the user fails to log in.
        ParseUser.logInInBackground(username, password, ({ user, e ->
            if (user != null) {
                Log.i(TAG, "Successfully logged in user")

                // Navigate user to the MainActivity because we don't want to keep them on
                // this LoginActivity anymore.
                goToMainActivity()

            } else {
                // Signup failed.  Look at the ParseException to see what happened.
                // Useful to log the exception
                e.printStackTrace()
                // Useful to show Toast too
                Toast.makeText(this, "Error logging in", Toast.LENGTH_SHORT).show()
            }})
        )
    }
    
    
    // Helper method that navigates the user to MainActivity using Intent
    // and closes out the LoginActivity
    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        // Hitting the back button from the MainActivity will exit the app instead of navigating
        // the user back to the LoginActivity
        finish()
    }
    

    companion object {
        const val TAG = "LoginActivity"
    }
}