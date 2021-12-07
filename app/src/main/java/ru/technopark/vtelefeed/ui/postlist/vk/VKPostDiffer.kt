package ru.technopark.vtelefeed.ui.postlist.vk

import androidx.recyclerview.widget.DiffUtil
import ru.technopark.vtelefeed.data.BasePost
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.data.VKPost

class VKPostDiffer : DiffUtil.ItemCallback<VKPost>() {

    override fun areItemsTheSame(oldItem: VKPost, newItem: VKPost): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: VKPost, newItem: VKPost): Boolean =
        newItem == oldItem
}
