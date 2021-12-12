package ru.technopark.vtelefeed.ui.postlist.tg

import android.view.View
import com.bumptech.glide.Glide
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.databinding.TextPostItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TgPostTextHolder(view: View) : TgPostHolder(view) {

    private val binding: TextPostItemBinding = TextPostItemBinding.bind(itemView)

    override fun bind(post: Post) {
        with(binding) {
            textPost.text = post.innerPost.text

            Glide.with(itemView.context)
                .load(post.innerPost.chatPhoto)
                .into(chatPhoto)

            chatTitle.text = post.innerPost.chatTitle

            vkOrTgImageView.setImageResource(R.drawable.tg)
            val date = Date(post.date * TgPostPhotoHolder.MILLIS_IN_SECOND)
            val dateText = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()).format(date)
            datePost.text = dateText
        }
    }
}
