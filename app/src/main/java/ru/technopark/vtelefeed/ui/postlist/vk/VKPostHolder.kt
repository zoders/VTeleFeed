package ru.technopark.vtelefeed.ui.postlist.vk

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.data.VKPost

open class VKPostHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(post: VKPost) = Unit
}
