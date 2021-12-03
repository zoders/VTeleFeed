package ru.technopark.vtelefeed.ui.postlist

import android.view.View
import com.bumptech.glide.Glide
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.databinding.TextPostItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostTextHolder(view: View) : PostHolder(view) {

    private val binding: TextPostItemBinding = TextPostItemBinding.bind(itemView)

    override fun bind(post: Post) {
        with(binding) {
            textPost.text = post.tgPost.text

            Glide.with(itemView.context)
                .load(post.tgPost.chatPhoto)
                .into(chatPhoto)

            chatTitle.text = post.tgPost.chatTitle

            vkOrTgImageView.setImageResource(R.drawable.tg)
            val date = Date(post.date * PostPhotoHolder.MILLIS_IN_SECOND)
            val dateText = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()).format(date)
            datePost.text = dateText
        }
    }
}
