package ru.technopark.vtelefeed

import android.util.Log
import androidx.paging.PositionalDataSource

private const val TAG = "PostDataSource"

class PostDataSource(val postStorage: PostStorage) : PositionalDataSource<Post>() {
    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Post>) {
        Log.d(TAG, "loadInitial, requestedStartPosition = ${params.requestedStartPosition}," +
                " requestedLoadSize = ${params.requestedLoadSize}")
        val result: List<Post> =
            postStorage.getData(params.requestedStartPosition, params.requestedLoadSize)
        callback.onResult(result, 0)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Post>) {
        Log.d(TAG, "loadRange, startPosition = ${params.startPosition}," +
                " loadSize = ${params.loadSize}")
        val result: List<Post> = postStorage.getData(params.startPosition, params.loadSize)
        callback.onResult(result)
    }
}
