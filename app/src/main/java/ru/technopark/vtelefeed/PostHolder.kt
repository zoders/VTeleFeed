package ru.technopark.vtelefeed

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.drinkless.td.libcore.telegram.TdApi
import java.util.*

class PostHolder(view: View) : RecyclerView.ViewHolder(view) {
    private lateinit var post: Post
    private val vkOrTgImageView: ImageView = itemView.findViewById(R.id.vk_or_tg_image_view)
    private val textPost: TextView = itemView.findViewById(R.id.text_post)
    private val datePost: TextView = itemView.findViewById(R.id.date_post)

    fun bind(post: Post) {
        this.post = post

        val content = post.tgPost.content

        textPost.text = when (content) {
            is TdApi.MessageText -> content.text.text
            is TdApi.MessagePhoto -> content.caption.text
            is TdApi.MessageVideo -> content.caption.text
            else -> "-- no text --"
        }

        vkOrTgImageView.setImageResource(R.drawable.tg)
        datePost.text = Date(post.tgPost.date.toLong()).toString()
    }
}
