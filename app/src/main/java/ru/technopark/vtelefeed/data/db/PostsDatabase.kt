package ru.technopark.vtelefeed.data.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import ru.technopark.vtelefeed.VTeleFeedApplication
import ru.technopark.vtelefeed.data.Post

@Database(entities = [Post::class], version = 1, exportSchema = false)
@TypeConverters(TgPostTypeConverter::class)
abstract class PostsDatabase : RoomDatabase() {
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
