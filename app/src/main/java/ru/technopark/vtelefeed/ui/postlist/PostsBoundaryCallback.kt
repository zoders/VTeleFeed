package ru.technopark.vtelefeed.ui.postlist

import androidx.paging.PagedList
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.data.VKPost

class PostsBoundaryCallback(private val loader: PostsLoader) : PagedList.BoundaryCallback<VKPost>() {

    override fun onZeroItemsLoaded() {
        loader.loadVkFirstItems()
    }

    override fun onItemAtEndLoaded(itemAtEnd: VKPost) {
        loader.loadVkNextItems()
    }
}
