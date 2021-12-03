package ru.technopark.vtelefeed.ui.postlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.TdApi
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.data.db.PostDao
import ru.technopark.vtelefeed.data.db.PostsDatabase
import ru.technopark.vtelefeed.data.tg.Offset
import ru.technopark.vtelefeed.data.tg.TelegramDataSource
import ru.technopark.vtelefeed.data.tg.TgClient
import java.util.concurrent.Executors

class PostStorage : ViewModel() {

    private val tgSource: TelegramDataSource by lazy { TgClient.tgSource }
    private val postDao: PostDao = PostsDatabase.instance.postDao()

    val pagedListLiveData: LiveData<PagedList<Post>>

    val authState: LiveData<TdApi.AuthorizationState?> = TgClient.authStateFlow.asLiveData()

    init {
        val factory = postDao.getSource()
        val config =
            PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .setPageSize(PAGE_SIZE)
                .build()
        pagedListLiveData = LivePagedListBuilder(factory, config)
            .setBoundaryCallback(object : PagedList.BoundaryCallback<Post>() {
                override fun onZeroItemsLoaded() {
                    viewModelScope.launch {
                        val firstChannelsMessages = tgSource.getChannelsPosts(PAGE_SIZE)
                        postDao.saveAll(firstChannelsMessages.posts.map { Post(it) })
                    }
                }

                override fun onItemAtEndLoaded(itemAtEnd: Post) {
                    viewModelScope.launch {
                        val offset = Offset(itemAtEnd.date, itemAtEnd.tgPost.chatId, itemAtEnd.id)
                        val channelsMessages = tgSource.getChannelsPosts(PAGE_SIZE, offset)
                        postDao.saveAll(channelsMessages.posts.map { Post(it) })
                    }
                }


            })
            .setFetchExecutor(Executors.newSingleThreadExecutor()).build()
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 5
        private const val TAG = "PostStorage"
    }
}
