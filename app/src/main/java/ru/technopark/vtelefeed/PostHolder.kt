package ru.technopark.vtelefeed

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PostHolder(view: View) : RecyclerView.ViewHolder(view) {
    private lateinit var post: Post
    private val vkOrTgImageView: ImageView = itemView.findViewById(R.id.vk_or_tg_image_view)
    private val textPost: TextView = itemView.findViewById(R.id.text_post)
    private val datePost: TextView = itemView.findViewById(R.id.date_post)

    fun bind(post: Post) {
        this.post = post
        vkOrTgImageView.setImageResource(post.fromVkOrTg)
        textPost.text = post.text
        datePost.text = post.date.toString()
    }
}