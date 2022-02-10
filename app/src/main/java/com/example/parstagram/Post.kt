package com.example.parstagram

import com.parse.ParseClassName
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser

// The way we reference this Post class to what we created in our Parse server is by adding a
// ParseClassName with the name of the table we created. We're saying that this Kotlin object
// class is to be associated with the table we created in the Parse server
@ParseClassName("Post")
class Post : ParseObject() {

    // Every Post object has 3 things:
    // Description : String
    // Image : File
    // User : User

    // The way we get each field in the table is using the key defined in our table
    // The key for each field we want to get is the name of each column
    fun getDescription(): String? {
        // getString is a special method provided by the ParseObject.java
        // This lets us access a string value
        // Because each description stored as a string in our string
        return getString(KEY_DESCRIPTION)
    }


    fun setDescription(description: String) {
        // Add a key-value pair to this object.
        put(KEY_DESCRIPTION, description)
    }


    fun getImage(): ParseFile? {
        return getParseFile(KEY_IMAGE)
    }


    fun setImage(parsefile: ParseFile) {
        put(KEY_IMAGE, parsefile)
    }


    fun getUser(): ParseUser? {
        return getParseUser(KEY_USER)
    }


    fun setUser(user : ParseUser) {
        put(KEY_USER, user)
    }


    companion object {
        const val KEY_DESCRIPTION = "description"
        const val KEY_IMAGE = "image"
        const val KEY_USER = "user"
    }
}