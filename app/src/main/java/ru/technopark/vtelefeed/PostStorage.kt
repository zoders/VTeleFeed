package ru.technopark.vtelefeed

import androidx.lifecycle.ViewModel

class PostStorage : ViewModel() {
    val posts = mutableListOf<Post>()

    init {
        for (i in 0 .. 20) {
            val post = Post()
            post.id = i
            post.fromVkOrTg = if (i % 2 == 0) R.drawable.vk else R.drawable.tg
            for (j in 0 .. 20) post.text += "текст поста "
            posts += post
        }
    }

    fun getData(startPosition: Int, loadSize: Int): List<Post> =
        if (startPosition == 20) emptyList()
        else posts.subList(startPosition, startPosition + loadSize)
}
