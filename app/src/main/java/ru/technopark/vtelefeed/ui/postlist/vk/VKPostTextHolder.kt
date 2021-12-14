package ru.technopark.vtelefeed.ui.postlist.vk

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.data.VKPost
import ru.technopark.vtelefeed.databinding.VkTextPostItemBinding
import ru.technopark.vtelefeed.ui.postlist.tg.TgPostPhotoHolder
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.ContextCompat.startActivity




class VKPostTextHolder(view: View) : VKPostHolder(view) {

    private val binding: VkTextPostItemBinding = VkTextPostItemBinding.bind(itemView)

    override fun bind(post: VKPost) {
        with(binding) {
            textPost.text = post.text
            val url = "https://vk.com/wall-${post.sourceID}_${post.postID}"
            vkOrTgImageView.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                itemView.context.startActivity(browserIntent)
            }
            icoReposts.setOnClickListener {
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.putExtra(Intent.EXTRA_TEXT, url)
                sendIntent.type = "text/plain"
                itemView.context.startActivity(sendIntent)
            }
            Glide.with(itemView.context)
                .load(post.groupPhoto)
                .into(chatPhoto)

            chatTitle.text = post.groupName
            binding.likes.text = post.likes.toString()
            binding.reposts.text = post.reposts.toString()
            binding.comments.text = post.comments.toString()
            binding.views.text = post.views.toString()
            vkOrTgImageView.setImageResource(R.drawable.vk)
            val date = Date(post.date * TgPostPhotoHolder.MILLIS_IN_SECOND)
            val dateText = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).format(date)
            datePost.text = dateText
        }
    }
}
