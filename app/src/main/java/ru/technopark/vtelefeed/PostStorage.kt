package ru.technopark.vtelefeed

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PositionalDataSource
import kotlinx.coroutines.launch

private const val PEREMENNAYA_S_PONYATNYM_NAZVANIEM_SPECIALNO_DLYA_STATIC_ANALYSIS = 20

class PostStorage : ViewModel() {

    private val tgClient = TelegramClient.instance

    private val tgSource: TelegramDataSource = TelegramDataSource(tgClient.client!!)

    private var offset: Offset = Offset()

    val posts = mutableListOf<Post>()
    val authState: LiveData<Boolean> = tgClient.authReadyLiveData

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
}
