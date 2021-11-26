package ru.technopark.vtelefeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import androidx.paging.PositionalDataSource
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.TdApi
import java.util.concurrent.Executors

private const val PEREMENNAYA_S_PONYATNYM_NAZVANIEM_SPECIALNO_DLYA_STATIC_ANALYSIS = 20

class PostStorage : ViewModel() {

    private val tgSource: TelegramDataSource by lazy { TgClient.tgSource }

    private var offset: Offset = Offset()

    val posts = mutableListOf<Post>()

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
        startPosition: Int,
        loadSize: Int,
        callback: PositionalDataSource.LoadRangeCallback<Post>
    ) {
        viewModelScope.launch {
            val channelsMessages = tgSource.getChannelsMessages(loadSize, offset)
            offset = channelsMessages.offset
            callback.onResult(
                channelsMessages.messages.takeLast(loadSize).map { Post(it) }
            )
        }
    }

    fun getData(startPosition: Int, loadSize: Int): List<Post> =
        if (startPosition == PEREMENNAYA_S_PONYATNYM_NAZVANIEM_SPECIALNO_DLYA_STATIC_ANALYSIS)
            emptyList()
        else posts.subList(startPosition, startPosition + loadSize)

    companion object {
        private const val PAGE_SIZE = 20
        private const val TAG = "PostStorage"
    }
}
