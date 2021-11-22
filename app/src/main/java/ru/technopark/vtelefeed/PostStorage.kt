package ru.technopark.vtelefeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import kotlinx.coroutines.launch
import java.util.concurrent.Executors

class PostStorage : ViewModel() {

    private val tgClient = TelegramClient.instance

    private val tgSource: TelegramDataSource = TelegramDataSource(tgClient.client!!)

    private var offset: Offset = Offset()

    val posts = mutableListOf<Post>()
    val authState: LiveData<Boolean> = tgClient.authReadyLiveData

    val pagedListLiveData: LiveData<PagedList<Post>>

    init {
        val factory = PostSourceFactory(this)
        val config =
            PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(PAGE_SIZE).build()
        pagedListLiveData = LivePagedListBuilder(factory, config)
            .setFetchExecutor(Executors.newSingleThreadExecutor()).build()
    }

    fun loadInitialPosts(
        requestedStartPosition: Int,
        requestedLoadSize: Int,
        callback: PositionalDataSource.LoadInitialCallback<Post>
    ) {
        viewModelScope.launch {
            val firstChannelsMessages = tgSource.getChannelsMessages(requestedLoadSize)
            offset = firstChannelsMessages.offset
            callback.onResult(
                firstChannelsMessages.messages.map { Post(it) },
                requestedStartPosition,
                firstChannelsMessages.messages.size
            )
        }
    }

    fun loadRangePosts(
        loadSize: Int,
        callback: PositionalDataSource.LoadRangeCallback<Post>
    ) {
        viewModelScope.launch {
            val channelsMessages = tgSource.getChannelsMessages(loadSize, offset)
            offset = channelsMessages.offset
            callback.onResult(
                channelsMessages.messages.map { Post(it) }
            )
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
    }
}
