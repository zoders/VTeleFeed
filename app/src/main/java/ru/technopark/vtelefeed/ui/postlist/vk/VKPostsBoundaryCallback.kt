package ru.technopark.vtelefeed.ui.postlist.vk

import androidx.paging.PagedList
import ru.technopark.vtelefeed.data.VKPost

class VKPostsBoundaryCallback(private val loader: VKPostsLoader) : PagedList.BoundaryCallback<VKPost>() {

    override fun onZeroItemsLoaded() {
        loader.loadVkFirstItems()

    }

    override fun onItemAtEndLoaded(itemAtEnd: VKPost) {
        loader.loadVkNextItems()
    }
}
