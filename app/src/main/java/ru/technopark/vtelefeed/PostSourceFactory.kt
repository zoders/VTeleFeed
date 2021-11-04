package ru.technopark.vtelefeed

import androidx.paging.DataSource

class PostSourceFactory(val postStorage: PostStorage) : DataSource.Factory<Int, Post>() {
    override fun create(): DataSource<Int, Post> {
        return PostDataSource(postStorage)
    }
}