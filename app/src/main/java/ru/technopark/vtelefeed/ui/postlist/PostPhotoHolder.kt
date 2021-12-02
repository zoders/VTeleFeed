package ru.technopark.vtelefeed.ui.postlist

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import org.drinkless.td.libcore.telegram.TdApi
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.databinding.PhotoPostItemBinding
import java.text.SimpleDateFormat
import java.util.*

class PostPhotoHolder(view: View) : PostHolder(view) {

    private val binding = PhotoPostItemBinding.bind(itemView)

    override fun bind(post: Post) {
        val content = post.tgPost.message.content as TdApi.MessagePhoto

        with(binding) {
            textPost.text = content.caption.text

            Glide.with(itemView.context)
                .load(content.photo.sizes.last().photo.local.path)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(image)

            Glide.with(itemView.context)
                .load(post.tgPost.chat.photo?.small?.local?.path)
                .into(chatPhoto)

            chatTitle.text = post.tgPost.chat.title

            vkOrTgImageView.setImageResource(R.drawable.tg)
            val date = Date(post.tgPost.message.date * MILLIS_IN_SECOND)
            val dateText = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()).format(date)
            datePost.text = dateText
        }
    }

    companion object {
        const val MILLIS_IN_SECOND = 1000L
    }
}
