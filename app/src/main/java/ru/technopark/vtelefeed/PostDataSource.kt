package ru.technopark.vtelefeed

import android.util.Log
import androidx.paging.PositionalDataSource

private const val TAG = "PostDataSource"

class PostDataSource(private val postStorage: PostStorage) : PositionalDataSource<Post>() {
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Post>) {
        Log.d(
            TAG,
            "loadInitial, requestedStartPosition = ${params.requestedStartPosition}," +
                    " requestedLoadSize = ${params.requestedLoadSize}"
        )

        postStorage.loadInitialPosts(
            params.requestedStartPosition,
            params.requestedLoadSize,
            callback
        )
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Post>) {
        Log.d(
            TAG,
            "loadRange, startPosition = ${params.startPosition}," +
                    " loadSize = ${params.loadSize}"
        )
        postStorage.loadRangePosts(params.startPosition, params.loadSize, callback)
    }
}
