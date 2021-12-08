package ru.technopark.vtelefeed.ui.postlist.vk

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.data.VKPost

class VKPostAdapter(diffUtilCallback: DiffUtil.ItemCallback<VKPost>) :
    PagedListAdapter<VKPost, VKPostHolder>(diffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VKPostHolder {
        return when (viewType) {
            ViewType.PHOTO.ordinal -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.vk_photo_post_item, parent, false)
                VKPostPhotoHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.vk_text_post_item, parent, false)
                VKPostTextHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: VKPostHolder, position: Int) {
        getItem(position)?.let { holder.bind(it as VKPost) }
    }

    override fun getItemViewType(position: Int): Int {
        return if (
            getItem(position)!!.photos!!.isNotEmpty()
            || getItem(position)!!.videos!!.isNotEmpty()
            || getItem(position)!!.audios!!.isNotEmpty()
            || getItem(position)!!.docs!!.isNotEmpty()) {
            ViewType.PHOTO.ordinal
        } else {
            ViewType.TEXT.ordinal
        }
    }

    private enum class ViewType {
        PHOTO, TEXT
    }
}
