package ru.technopark.vtelefeed.ui.postlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import ru.technopark.vtelefeed.R
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.ui.postlist.tg.TgPostTextHolder

class TgPostAdapter(diffUtilCallback: DiffUtil.ItemCallback<Post>) :
    PagedListAdapter<Post, TgPostHolder>(diffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TgPostHolder {
        return when (viewType) {
            ViewType.PHOTO.ordinal -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.photo_post_item, parent, false)
                TgPostPhotoHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.text_post_item, parent, false)
                TgPostTextHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: TgPostHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position)!!.innerPost.photo != null) {
            ViewType.PHOTO.ordinal
        } else {
            ViewType.TEXT.ordinal
        }
    }

    private enum class ViewType {
        PHOTO, TEXT
    }
}
