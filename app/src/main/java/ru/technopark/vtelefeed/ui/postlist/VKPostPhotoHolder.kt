package ru.technopark.vtelefeed.ui.postlist

import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.data.TgPost
import ru.technopark.vtelefeed.data.VKPost
import ru.technopark.vtelefeed.databinding.VkPhotoPostItemBinding
import java.text.SimpleDateFormat
import java.util.Date
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.size
import java.util.Locale
import com.google.android.flexbox.FlexboxLayout
import com.google.android.flexbox.FlexboxLayoutManager


class VKPostPhotoHolder(view: View) : PostHolder(view) {

    private val binding = VkPhotoPostItemBinding.bind(itemView)

    override fun bind(post: VKPost) {

        with(binding) {
            textPost.text = post.text
            photos.removeAllViews()

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
            Glide.with(itemView.context)
                .load(post.groupPhoto)
                .into(chatPhoto)

            chatTitle.text = post.groupName

            vkOrTgImageView.setImageResource(R.drawable.vk)
            val date = Date(post.date * MILLIS_IN_SECOND)
            val dateText = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()).format(date)
            datePost.text = dateText
        }
    }

    companion object {
        const val MILLIS_IN_SECOND = 1000L
    }
}
