package ru.technopark.vtelefeed.ui.postlist.tg

import androidx.recyclerview.widget.DiffUtil
import ru.technopark.vtelefeed.data.Post

class TgPostDiffer : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
}
