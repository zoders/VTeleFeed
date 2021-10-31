package com.android.vtelefeed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.lang.NullPointerException
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private var adapter: PostAdapter? = null
    private val postStorage: PostStorage by lazy {
        ViewModelProvider(this).get(PostStorage::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
        adapter = PostAdapter(postStorage.posts, PostDiffer())
        val factory = PostSourceFactory(postStorage)
        val config = PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(2).build()
        val pagedListLiveData = LivePagedListBuilder(factory, config).setFetchExecutor(Executors.newSingleThreadExecutor()).build()
        pagedListLiveData.observe(this, object : Observer<PagedList<Post>> {
            override fun onChanged(t: PagedList<Post>?) {
                adapter?.submitList(t) ?: throw NullPointerException()
            }
        })
        recyclerView.adapter = adapter
    }

    private inner class PostHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var post: Post
        private val vkOrTgImageView: ImageView = itemView.findViewById(R.id.vk_or_tg_image_view)
        private val textPost: TextView = itemView.findViewById(R.id.text_post)
        private val datePost: TextView = itemView.findViewById(R.id.date_post)

        fun bind(post: Post) {
            this.post = post
            vkOrTgImageView.setImageResource(post.fromVkOrTg)
            textPost.text = post.text
            datePost.text = post.date.toString()
        }
    }

    private inner class PostAdapter(var posts: List<Post>, diffUtilCallback: DiffUtil.ItemCallback<Post>) :
        PagedListAdapter<Post, PostHolder>(diffUtilCallback) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
            val view = layoutInflater.inflate(R.layout.post, parent, false)
            return PostHolder(view)
        }

        override fun onBindViewHolder(holder: PostHolder, position: Int) {
            getItem(position)?.let { holder.bind(it) }
        }
    }

    private inner class PostDiffer : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem?.id == newItem?.id
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean = oldItem == newItem
    }
}