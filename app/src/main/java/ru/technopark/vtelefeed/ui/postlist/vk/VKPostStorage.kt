package ru.technopark.vtelefeed.ui.postlist.vk

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.vk.api.sdk.VK
import kotlinx.coroutines.launch
import ru.technopark.vtelefeed.data.VKPost
import ru.technopark.vtelefeed.data.db.PostsDatabase
import ru.technopark.vtelefeed.data.db.VKPostDao
import ru.technopark.vtelefeed.data.vk.VKDataSource
import java.util.concurrent.Executors

class VKPostStorage : ViewModel(), VKPostsLoader {

    private val vkSource = VKDataSource()
    private val vkPostDao: VKPostDao = PostsDatabase.instance.vkPostDao()
    private val _refresh = MutableLiveData(false)
    private val isVKLogged = MutableLiveData(VK.isLoggedIn())
    val pagedListLiveData: LiveData<PagedList<VKPost>>
    val vkAuthState: LiveData<Boolean> = isVKLogged
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
            .setBoundaryCallback(VKPostsBoundaryCallback(this))
            .setFetchExecutor(Executors.newSingleThreadExecutor()).build()
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
        const val APP_PREFERENCES = "next_from"
        private const val PAGE_SIZE = 20
        private const val PREFETCH_DISTANCE = 10
        private const val TAG = "PostStorage"
    }
}
