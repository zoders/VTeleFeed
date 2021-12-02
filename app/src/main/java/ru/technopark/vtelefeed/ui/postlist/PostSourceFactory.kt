package ru.technopark.vtelefeed.ui.postlist

import androidx.paging.DataSource
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.data.PostDataSource

class PostSourceFactory(val postStorage: PostStorage) : DataSource.Factory<Int, Post>() {
    override fun create(): DataSource<Int, Post> {
        return PostDataSource(postStorage)
    }
}
