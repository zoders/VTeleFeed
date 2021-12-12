package ru.technopark.vtelefeed.ui.postlist.tg

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import ru.technopark.vtelefeed.ui.postlist.TgPostsBoundaryCallback
import ru.technopark.vtelefeed.ui.postlist.TgPostsLoader
import java.util.concurrent.Executors

class TgPostStorage : ViewModel(), TgPostsLoader {

    private val tgSource: TelegramDataSource by lazy { TgClient.tgSource }
    private val postDao: PostDao = PostsDatabase.instance.postDao()

    private val _refresh = MutableLiveData<Boolean>()

    val pagedListLiveData: LiveData<PagedList<Post>>

    val authState: LiveData<TdApi.AuthorizationState?> = TgClient.authStateFlow.asLiveData()

    val refresh: LiveData<Boolean> = _refresh

    init {
        val factory = postDao.getSource()
        val config =
            PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .setPageSize(PAGE_SIZE)
                .build()
        pagedListLiveData = LivePagedListBuilder(factory, config)
            .setBoundaryCallback(TgPostsBoundaryCallback(this))
            .setFetchExecutor(Executors.newSingleThreadExecutor()).build()
    }

    override fun loadFirstItems() {
        viewModelScope.launch {
            _refresh.value = true
            val firstChannelsMessages = tgSource.getChannelsPosts(PAGE_SIZE)
            postDao.saveAll(firstChannelsMessages.map { Post(it) })
            _refresh.value = false
        }
    }

    override fun loadNextItems(lastItem: Post) {
        viewModelScope.launch {
            val offset = Offset(lastItem.date, lastItem.innerPost.chatId, lastItem.innerPost.id)
            val channelsMessages = tgSource.getChannelsPosts(PAGE_SIZE, offset)
            postDao.saveAll(channelsMessages.map { Post(it) })
        }
    }

    fun refreshPostsDatabase() {
        viewModelScope.launch {
            _refresh.value = true
            postDao.deleteAll()
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 10
        private const val TAG = "PostStorage"
    }
}
