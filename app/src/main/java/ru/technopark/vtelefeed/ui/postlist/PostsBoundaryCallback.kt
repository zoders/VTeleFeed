package ru.technopark.vtelefeed.ui.postlist

import androidx.paging.PagedList
import ru.technopark.vtelefeed.data.Post

class PostsBoundaryCallback(private val loader: PostsLoader) : PagedList.BoundaryCallback<Post>() {

    override fun onZeroItemsLoaded() {
        loader.loadFirstItems()
    }

    override fun onItemAtEndLoaded(itemAtEnd: Post) {
        loader.loadNextItems(itemAtEnd)
    }
}
