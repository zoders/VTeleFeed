package ru.technopark.vtelefeed.ui.postlist.tg

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import ru.technopark.vtelefeed.data.Post

open class TgPostHolder(view: View) : RecyclerView.ViewHolder(view) {
    open fun bind(post: Post) = Unit
}
