package ru.technopark.vtelefeed.ui.postlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.TdApi
import ru.technopark.vtelefeed.data.tg.Offset
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.data.tg.TelegramDataSource
import ru.technopark.vtelefeed.data.tg.TgClient
import java.util.concurrent.Executors

class PostStorage : ViewModel() {

    private val tgSource: TelegramDataSource by lazy { TgClient.tgSource }

    private var offset: Offset = Offset()

    val pagedListLiveData: LiveData<PagedList<Post>>

    val authState: LiveData<TdApi.AuthorizationState?> = TgClient.authStateFlow.asLiveData()

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
            val firstChannelsMessages = tgSource.getChannelsPosts(requestedLoadSize)
            offset = firstChannelsMessages.offset
            callback.onResult(
                firstChannelsMessages.posts.map { Post(it) },
                requestedStartPosition,
                firstChannelsMessages.posts.size
            )
        }
    }

    fun loadRangePosts(
        loadSize: Int,
        callback: PositionalDataSource.LoadRangeCallback<Post>
    ) {
        viewModelScope.launch {
            val channelsMessages = tgSource.getChannelsPosts(loadSize, offset)
            offset = channelsMessages.offset
            callback.onResult(
                channelsMessages.posts.map { Post(it) }
            )
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val TAG = "PostStorage"
    }
}
