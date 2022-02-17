package com.example.parstagram

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


// we need to pass in some information to our adapters in order to be able to
// display each item. So, when we create an adapter there's 2 things we need
// to pass into it: context and list of Posts
class PostAdapter(val context: Context, val posts: List<Post>)
    : RecyclerView.Adapter<PostAdapter.ViewHolder>() {

    // in charge of saying: "Hey, which layout file should we use to create ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostAdapter.ViewHolder {
        // Specify the layout file to use for this item: R.layout.item_post
        val view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false)
        // return the ViewHolder based on this view that we have just inflated.
        return ViewHolder(view)
    }

    // take a ViewHolder created, and fill information into that ViewHolder
    override fun onBindViewHolder(holder: PostAdapter.ViewHolder, position: Int) {
        // get the specific post we care about
        // It's the post at the position (specified by param) we're trying to inflate
        val post = posts.get(position)
        holder.bind(post)
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    // Specifies what the ViewHolder contains and how to information is filled into the ViewHolder
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvUsername: TextView
        val ivImage: ImageView
        val tvDescription: TextView

        init {
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        // bind the data (Post) to our layout
        // this method will be used later
        fun bind(post: Post) {
            tvUsername.text = post.getUser()?.username
            tvDescription.text = post.getDescription()

            // Populate the ImageView:
            // ImageViews are a little bit harder and different thatn regular
            // text views. We use 3rd party library Glide to populate images
            // into ImageViews more easily.
            // We need a context to display things with so we need Glide.with
            Glide.with(itemView.context).load(post.getImage()?.url).into(ivImage)
        }
    }
}