package com.example.parstagram.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.parstagram.MainActivity
import com.example.parstagram.Post
import com.example.parstagram.PostAdapter
import com.example.parstagram.R
import com.parse.FindCallback
import com.parse.ParseException
import com.parse.ParseQuery


open class FeedFragment : Fragment() {

    lateinit var postsRecyclerView: RecyclerView

    lateinit var adapter: PostAdapter

    // Create list that contains Posts. Adapter will use this list
    // Mutable because we want to grab things from our Parse server
    // and then add to this list later. So, I want to make sure I
    // still have the ability to change what's inside this post.
    var allPosts: MutableList<Post> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_feed, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Unlike Activities, for Fragments, this is where we set up our views
        // and click listeners
        postsRecyclerView = view.findViewById(R.id.postRecyclerView)

        // Steps to populate RecyclerView
        // 1. Create layout for each row in list (item_post.xml)
        // 2. Create data source for each row (this is the Post class)
        // 3. Create adapter that will bridge data and row layout (PostAdapter)
        // 4. Set adapter on RecyclerView
        // We're giving adapter an empty list, we'll notify it when we fill data into that list
        // for it to work
        adapter = PostAdapter(requireContext(), allPosts)
        postsRecyclerView.adapter = adapter

        // 5. Set layout manager on RecyclerView
        postsRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        queryPosts()
    }


    // Query for all posts in our sever
    open fun queryPosts() {
        // Specify which class to query
        val query: ParseQuery<Post> = ParseQuery.getQuery(Post::class.java)

        // when we make this query for all the posts, we're telling Parse to also give us back the
        // User that's associated with each post so that we can use to do whatever we want inside
        // our app.
        query.include(Post.KEY_USER)
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

    companion object {
        const val TAG = "FeedFragment"
    }
}