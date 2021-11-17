package ru.technopark.vtelefeed

import androidx.recyclerview.widget.DiffUtil

class PostDiffer : DiffUtil.ItemCallback<Post>() {

    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean =
        oldItem.tgPost.id == newItem.tgPost.id

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
}
