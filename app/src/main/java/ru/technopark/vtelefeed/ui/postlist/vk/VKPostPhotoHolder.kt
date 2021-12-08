package ru.technopark.vtelefeed.ui.postlist.vk

import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.data.VKPost
import ru.technopark.vtelefeed.databinding.VkPhotoPostItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class VKPostPhotoHolder(view: View) : VKPostHolder(view) {

    private val binding = VkPhotoPostItemBinding.bind(itemView)

    override fun bind(post: VKPost) {

        with(binding) {
            textPost.text = post.text
            photos.removeAllViews()
            videos.removeAllViews()

            Glide.with(itemView.context)
                .load(post.groupPhoto)
                .into(chatPhoto)

            chatTitle.text = post.groupName
            binding.likes.text = post.likes.toString()
            binding.reposts.text = post.reposts.toString()
            binding.comments.text = post.comments.toString()
            vkOrTgImageView.setImageResource(R.drawable.vk)
            vkOrTgImageView.setOnClickListener {
                val url = "https://vk.com/wall-${post.sourceID}_${post.postID}"
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                itemView.context.startActivity(browserIntent)
            }
            if (post.photos != null) {
                post.photos.forEach {
                    val imageView = ImageView(itemView.context)

                    Glide.with(itemView.context)
                        .load(it.url)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView)
                    photos.addView(imageView)
                }
            }
            if (post.videos != null) {
                post.videos.forEach {
                    val imageButton = ImageButton(itemView.context)
                    //imageButton.setImageBitmap()
                    val url = "https://vk.com/video?z=video${it.ownerId}_${it.id}"
                    imageButton.setOnClickListener {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        itemView.context.startActivity(browserIntent)
                    }
                    Glide.with(itemView.context)
                        .load(it.image)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageButton)

                    photos.addView(imageButton)
                }
            }
            val date = Date(post.date * MILLIS_IN_SECOND)
            val dateText = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).format(date)
            datePost.text = dateText
        }
    }

    companion object {
        const val MILLIS_IN_SECOND = 1000L
    }
}
