package ru.technopark.vtelefeed.ui.postlist

import android.view.View
import com.bumptech.glide.Glide
import org.drinkless.td.libcore.telegram.TdApi
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.databinding.TextPostItemBinding
import java.text.SimpleDateFormat
import java.util.*

class PostTextHolder(view: View) : PostHolder(view) {

    private val binding: TextPostItemBinding = TextPostItemBinding.bind(itemView)

    override fun bind(post: Post) {
        with(binding) {
            textPost.text = when (val content = post.tgPost.message.content) {
                is TdApi.MessageText -> content.text.text
                is TdApi.MessageVideo -> content.caption.text
                else -> "---no-text---"
            }

            Glide.with(itemView.context)
                .load(post.tgPost.chat.photo?.small?.local?.path)
                .into(chatPhoto)

            chatTitle.text = post.tgPost.chat.title

            vkOrTgImageView.setImageResource(R.drawable.tg)
            val date = Date(post.tgPost.message.date * PostPhotoHolder.MILLIS_IN_SECOND)
            val dateText = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()).format(date)
            datePost.text = dateText
        }
    }

}
