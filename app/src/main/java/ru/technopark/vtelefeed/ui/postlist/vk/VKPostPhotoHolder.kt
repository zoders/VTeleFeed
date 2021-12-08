package ru.technopark.vtelefeed.ui.postlist.vk

import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.data.VKPost
import ru.technopark.vtelefeed.data.vk.VKPhoto
import ru.technopark.vtelefeed.data.vk.VKVideo
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
            setPhotos(post.photos, binding)
            setVideos(post.videos, binding)
            val date = Date(post.date * MILLIS_IN_SECOND)
            val dateText = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.getDefault()).format(date)
            datePost.text = dateText
        }
    }

    private fun setVideos(vkVideos: List<VKVideo>?, binding: VkPhotoPostItemBinding) {
        if (vkVideos != null) {
            with(binding) {
                vkVideos.forEach {
                    val view = LayoutInflater.from(itemView.context)
                        .inflate(R.layout.video_item, videos, false)
                    val preview = view.findViewById<ImageView>(R.id.preview)
                    val videoTitle = view.findViewById<TextView>(R.id.video_title)
                    videoTitle.text = it.title
                    val url = "https://vk.com/video?z=video${it.ownerId}_${it.id}"
                    preview.setOnClickListener {
                        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        itemView.context.startActivity(browserIntent)
                    }
                    Glide.with(itemView.context)
                        .load(it.image)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(preview)

                    videos.addView(view)
                }
            }
        }
    }

    private fun setPhotos(vkPhotos: List<VKPhoto>?, binding: VkPhotoPostItemBinding) {
        if (vkPhotos != null) {
            with(binding) {
                vkPhotos.forEach {
                    //val imageView = ImageView(itemView.context)
                    val view = LayoutInflater.from(itemView.context)
                        .inflate(R.layout.photo_item, photos, false)
                    val imageView = view.findViewById<ImageView>(R.id.post_photo)
                    Glide.with(itemView.context)
                        .load(it.url)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(imageView)
                    photos.addView(view)
                }
            }
        }
    }

    companion object {
        const val MILLIS_IN_SECOND = 1000L
    }
}

