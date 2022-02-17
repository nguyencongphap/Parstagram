package com.example.parstagram.fragments

import android.util.Log
import com.example.parstagram.Post
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser

class ProfileFragment: FeedFragment() {

    // override queryPosts of class FeedFragment
    // because we want to ProfileFragment behaves the same as FeedFragment
    // except for queryPosts
    override fun queryPosts() {
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        // when we make this query for all the posts, we're telling Parse to also give us back the
        // User that's associated with each post so that we can use to do whatever we want inside
        // our app.
        query.include(Post.KEY_USER)

        // Only return to me the posts where the user of the post is
        // the user that currently signed in. Comparing object, not just usernames.
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser())

        // Hey, I want all the posts come back in descinding order based on when
        // they were created
        query.addDescendingOrder("createdAt")

        // Only return the most recent 20 posts
        query.setLimit(20)

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

                        // Add all the posts grabbed from Parse server to our list
                        allPosts.addAll(posts)
                        // Notify adapter that we have filled in the data for it to work
                        // with in allPosts
                        adapter.notifyDataSetChanged()
                    }
                }
            }

        })
    }

}