package ru.technopark.vtelefeed.data.db

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.technopark.vtelefeed.data.BasePost
import ru.technopark.vtelefeed.data.VKPost

@Dao
interface VKPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveAll(posts: List<VKPost>)

    @Query("DELETE FROM vk_posts_table")
    suspend fun deleteAll()

    @Query("SELECT * FROM vk_posts_table ORDER BY date DESC")
    fun getSource(): DataSource.Factory<Int, VKPost>
}
