package ru.technopark.vtelefeed

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil

class PostAdapter(diffUtilCallback: DiffUtil.ItemCallback<Post>) :
    PagedListAdapter<Post, PostHolder>(diffUtilCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post, parent, false)
        return PostHolder(view)
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }
}
