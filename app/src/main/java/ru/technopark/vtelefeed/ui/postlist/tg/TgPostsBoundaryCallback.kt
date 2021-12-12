package ru.technopark.vtelefeed.ui.postlist.tg

import androidx.paging.PagedList
import ru.technopark.vtelefeed.data.Post

class TgPostsBoundaryCallback(private val loader: TgPostsLoader) : PagedList.BoundaryCallback<Post>() {

    override fun onZeroItemsLoaded() {
        loader.loadFirstItems()
    }

    override fun onItemAtEndLoaded(itemAtEnd: Post) {
        loader.loadNextItems(itemAtEnd)
    }
}
