package ru.technopark.vtelefeed.ui.postlist.tg

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.databinding.PhotoPostItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TgPostPhotoHolder(view: View) : TgPostHolder(view) {

    private val binding = PhotoPostItemBinding.bind(itemView)

    override fun bind(post: Post) {

        with(binding) {
            textPost.text = post.innerPost.text

            Glide.with(itemView.context)
                .load(post.innerPost.photo)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(image)

            Glide.with(itemView.context)
                .load(post.innerPost.chatPhoto)
                .into(chatPhoto)

            chatTitle.text = post.innerPost.chatTitle

            vkOrTgImageView.setImageResource(R.drawable.tg)
            val date = Date(post.date * MILLIS_IN_SECOND)
            val dateText = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).format(date)
            datePost.text = dateText
        }
    }

    companion object {
        const val MILLIS_IN_SECOND = 1000L
    }
}
