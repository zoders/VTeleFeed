package ru.technopark.vtelefeed.data.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.technopark.vtelefeed.data.Post

@Dao
interface PostDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(posts: List<Post>)

    @Query("SELECT * FROM posts_table ORDER BY date")
    suspend fun getSource(): DataSource.Factory<Int, Post>

}
