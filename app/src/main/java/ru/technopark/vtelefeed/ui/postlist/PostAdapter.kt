package ru.technopark.vtelefeed.ui.postlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.data.TgPost
import ru.technopark.vtelefeed.data.VKPost

class PostAdapter(diffUtilCallback: DiffUtil.ItemCallback<VKPost>) :
    PagedListAdapter<VKPost, PostHolder>(diffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        return when (viewType) {
            ViewType.PHOTO.ordinal -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.photo_post_item, parent, false)
                PostPhotoHolder(view)
            }
            ViewType.VKPHOTO.ordinal -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.vk_photo_post_item, parent, false)
                VKPostPhotoHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.text_post_item, parent, false)
                PostTextHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun getItemViewType(position: Int): Int {
        /*return if (getItem(position)!!.innerPost is TgPost) {
            if ((getItem(position)!!.innerPost as TgPost).photo != null) {
                ViewType.PHOTO.ordinal
            } else {
                ViewType.TEXT.ordinal
            }
        } else{
            ViewType.TEXT.ordinal
        }*/
        return if (getItem(position)!!.photos!!.isNotEmpty()) {
            ViewType.VKPHOTO.ordinal
        } else {
            ViewType.TEXT.ordinal
        }
    }

    private enum class ViewType {
        PHOTO, TEXT, VKPHOTO
    }
}
