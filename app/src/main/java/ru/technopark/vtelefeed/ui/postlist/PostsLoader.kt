package ru.technopark.vtelefeed.ui.postlist

import ru.technopark.vtelefeed.data.Post

interface PostsLoader {
    fun loadTgFirstItems()
    fun loadTgNextItems(lastItem: Post)
    fun loadVkFirstItems()
    fun loadVkNextItems()
}
