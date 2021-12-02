package ru.technopark.vtelefeed.ui.postlist

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.technopark.vtelefeed.data.Post

open class PostHolder(view: View) : RecyclerView.ViewHolder(view) {

    open fun bind(post: Post) = Unit

}
