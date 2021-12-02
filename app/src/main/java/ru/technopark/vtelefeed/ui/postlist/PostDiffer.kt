package ru.technopark.vtelefeed.ui.postlist

import androidx.recyclerview.widget.DiffUtil
import ru.technopark.vtelefeed.data.Post

class PostDiffer : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem.tgPost.message.id == newItem.tgPost.message.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
}
