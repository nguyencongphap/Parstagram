package com.example.parstagram

import android.app.Application
import com.parse.Parse
import com.parse.ParseObject

// Application() class that we extend is a class that gets called whenever our app is launched
// for the first time. So, we extend Application() and override its onCreate() to add in
// the initialization of Parse so that our android app can start talking to our Parse server
// right at whenever our app is launched for the first time
class ParstagramApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Register our parse models BEFORE we call Parse.initialize
        // This sets us up for actually using the Post class
        ParseObject.registerSubclass(Post::class.java)

        Parse.initialize(
            Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build());
    }
}