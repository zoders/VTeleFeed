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

    @Query("DELETE FROM posts_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM posts_table ORDER BY date DESC")
    fun getSource(): DataSource.Factory<Int, Post>

}
