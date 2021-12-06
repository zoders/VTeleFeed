package ru.technopark.vtelefeed.ui.postlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.vk.api.sdk.VK
import kotlinx.coroutines.launch
import org.drinkless.td.libcore.telegram.TdApi
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.data.db.PostDao
import ru.technopark.vtelefeed.data.db.PostsDatabase
import ru.technopark.vtelefeed.data.tg.Offset
import ru.technopark.vtelefeed.data.tg.TelegramDataSource
import ru.technopark.vtelefeed.data.tg.TgClient
import ru.technopark.vtelefeed.data.TgPost
import ru.technopark.vtelefeed.data.VKPost
import ru.technopark.vtelefeed.data.db.VKPostDao
import ru.technopark.vtelefeed.data.vk.VKDataSource
import java.util.concurrent.Executors

class PostStorage : ViewModel(), PostsLoader {

    private val tgSource: TelegramDataSource by lazy { TgClient.tgSource }
    private val vkSource = VKDataSource()
    private val vkPostDao: VKPostDao = PostsDatabase.instance.vkPostDao()
    private val _refresh = MutableLiveData(false)
    private val isVKLogged = MutableLiveData(VK.isLoggedIn())
    val pagedListLiveData: LiveData<PagedList<VKPost>>

    val authState: LiveData<TdApi.AuthorizationState?> = TgClient.authStateFlow.asLiveData()
    val vkAuthState: LiveData<Boolean> =  isVKLogged
    val refresh: LiveData<Boolean> = _refresh

    init {
        val factory = vkPostDao.getSource()
        val config =
            PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPrefetchDistance(PREFETCH_DISTANCE)
                .setPageSize(PAGE_SIZE)
                .build()
        pagedListLiveData = LivePagedListBuilder(factory, config)
            .setBoundaryCallback(PostsBoundaryCallback(this))
            .setFetchExecutor(Executors.newSingleThreadExecutor()).build()
    }

    override fun loadTgFirstItems() {
        viewModelScope.launch {
            _refresh.value = true
            val firstChannelsMessages = tgSource.getChannelsPosts(PAGE_SIZE)
            //postDao.saveAll(firstChannelsMessages.map { Post(innerPost = it) })
            _refresh.value = false
        }
    }

    override fun loadTgNextItems(lastItem: Post) {
        viewModelScope.launch {
            val offset = Offset(lastItem.date, (lastItem.innerPost as TgPost).chatId, lastItem.id)
            val channelsMessages = tgSource.getChannelsPosts(PAGE_SIZE, offset)
            //postDao.saveAll(channelsMessages.map { Post(innerPost = it) })
        }
    }

    override fun loadVkFirstItems() {
        if (VK.isLoggedIn()) {
            viewModelScope.launch {
                _refresh.value = true
                val vkPosts = vkSource.getVKResponse()
                vkPostDao.saveAll(vkPosts)
                _refresh.value = false
            }
        }
    }


    override fun loadVkNextItems() {
        if (VK.isLoggedIn()) {
            viewModelScope.launch {
                val nextFrom = vkSource.nextFrom
                val vkPosts = vkSource.getVKResponse(nextFrom)
                vkPostDao.saveAll(vkPosts)

            }
        }
    }

    fun refreshPostsDatabase() {
        viewModelScope.launch {
            _refresh.value = true
            vkPostDao.deleteAll()
        }
    }

    companion object {
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 10
        private const val TAG = "PostStorage"
    }
}
