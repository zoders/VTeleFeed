package ru.technopark.vtelefeed.ui.postlist

import androidx.recyclerview.widget.DiffUtil
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.data.VKPost

class PostDiffer : DiffUtil.ItemCallback<VKPost>() {

    override fun areItemsTheSame(oldItem: VKPost, newItem: VKPost): Boolean =
        oldItem.postID == newItem.postID && oldItem.sourceID == newItem.sourceID

    override fun areContentsTheSame(oldItem: VKPost, newItem: VKPost): Boolean = oldItem == newItem
}
