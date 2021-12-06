package ru.technopark.vtelefeed.ui.postlist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.data.VKPost

open class PostHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(post: VKPost) = Unit
}
