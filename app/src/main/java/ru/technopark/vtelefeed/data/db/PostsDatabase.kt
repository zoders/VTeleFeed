package ru.technopark.vtelefeed.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.technopark.vtelefeed.VTeleFeedApplication
import ru.technopark.vtelefeed.data.Post
import ru.technopark.vtelefeed.data.VKPost

@Database(entities = [VKPost::class, Post::class], version = 1, exportSchema = false)
@TypeConverters(VKPostTypeConverter::class, TgPostTypeConverter::class)
abstract class PostsDatabase : RoomDatabase() {
    abstract fun vkPostDao(): VKPostDao
    abstract fun postDao(): PostDao

    companion object {
        private const val DATABASE_NAME = "PostsDatabase"

        val instance by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            Room.databaseBuilder(
                VTeleFeedApplication.instance,
                PostsDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
}
