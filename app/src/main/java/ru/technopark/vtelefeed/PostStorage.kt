package ru.technopark.vtelefeed

import androidx.lifecycle.ViewModel

private const val PEREMENNAYA_S_PONYATNYM_NAZVANIEM_SPECIALNO_DLYA_STATIC_ANALYSIS = 20

class PostStorage : ViewModel() {
    val posts = mutableListOf<Post>()

    init {
        for (i in 0..PEREMENNAYA_S_PONYATNYM_NAZVANIEM_SPECIALNO_DLYA_STATIC_ANALYSIS) {
            val post = Post()
            post.id = i
            post.fromVkOrTg = if (i % 2 == 0) R.drawable.vk else R.drawable.tg
            for (j in 0..PEREMENNAYA_S_PONYATNYM_NAZVANIEM_SPECIALNO_DLYA_STATIC_ANALYSIS)
                post.text += "текст поста "
            posts += post
        }
    }

    fun getData(startPosition: Int, loadSize: Int): List<Post> =
        if (startPosition == PEREMENNAYA_S_PONYATNYM_NAZVANIEM_SPECIALNO_DLYA_STATIC_ANALYSIS)
            emptyList()
        else posts.subList(startPosition, startPosition + loadSize)
}
