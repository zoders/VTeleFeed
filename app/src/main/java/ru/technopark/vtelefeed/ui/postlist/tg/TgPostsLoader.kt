package ru.technopark.vtelefeed.ui.postlist.tg

import ru.technopark.vtelefeed.data.Post

interface TgPostsLoader {
    fun loadFirstItems()
    fun loadNextItems(lastItem: Post)
}
