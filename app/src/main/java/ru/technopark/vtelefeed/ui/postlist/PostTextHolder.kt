package ru.technopark.vtelefeed.ui.postlist

import android.view.View
import com.bumptech.glide.Glide
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.data.TgPost
import ru.technopark.vtelefeed.data.VKPost
import ru.technopark.vtelefeed.databinding.TextPostItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostTextHolder(view: View) : PostHolder(view) {

    private val binding: TextPostItemBinding = TextPostItemBinding.bind(itemView)

    override fun bind(post: VKPost) {
        with(binding) {
            textPost.text = post.text

            Glide.with(itemView.context)
                .load(post.groupPhoto)
                .into(chatPhoto)

            chatTitle.text = post.groupName

            vkOrTgImageView.setImageResource(R.drawable.vk)
            val date = Date(post.date * PostPhotoHolder.MILLIS_IN_SECOND)
            val dateText = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()).format(date)
            datePost.text = dateText
        }
    }
}
