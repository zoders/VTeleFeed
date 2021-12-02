package ru.technopark.vtelefeed

import android.view.View
import androidx.recyclerview.widget.RecyclerView

open class PostHolder(view: View) : RecyclerView.ViewHolder(view) {

    open fun bind(post: Post) = Unit

}
