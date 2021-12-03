package ru.technopark.vtelefeed.ui.postlist

import ru.technopark.vtelefeed.data.Post

interface PostsLoader {
    fun loadFirstItems()
    fun loadNextItems(lastItem: Post)
}
