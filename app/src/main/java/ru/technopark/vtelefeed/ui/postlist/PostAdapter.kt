package ru.technopark.vtelefeed.ui.postlist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import org.drinkless.td.libcore.telegram.TdApi
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.R

class PostAdapter(diffUtilCallback: DiffUtil.ItemCallback<Post>) :
    PagedListAdapter<Post, PostHolder>(diffUtilCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        return when (viewType) {
            ViewType.PHOTO.ordinal -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.photo_post_item, parent, false)
                PostPhotoHolder(view)
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
        return when (getItem(position)!!.tgPost.message.content) {
            is TdApi.MessagePhoto -> ViewType.PHOTO.ordinal
            else -> ViewType.TEXT.ordinal
        }
    }

    private enum class ViewType {
        PHOTO, TEXT
    }
}