package ru.technopark.vtelefeed.data.tg

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import ru.technopark.vtelefeed.data.TgPost
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TelegramDataSource(private val client: Client) {

    suspend fun getChannelsPosts(
        limit: Int,
        offset: Offset = Offset(),
        minDate: Int = 0,
        maxDate: Int = 0
    ): List<TgPost> {
        val query = " "

        var lastMessage: TdApi.Message? = null
        val channelPosts = mutableListOf<TgPost>()

        while (channelPosts.size < limit) {
            val allMessages = searchMessages(
                query,
                Offset(
                    lastMessage?.date ?: offset.date,
                    lastMessage?.chatId ?: offset.chatId,
                    lastMessage?.id ?: offset.messageId
                ),
                limit,
                SearchFilter(
                    TdApi.SearchMessagesFilterEmpty(),
                    minDate,
                    maxDate
                )
            ).messages.toList()

            lastMessage = allMessages.lastOrNull()

            val newChannelMessages = allMessages
                .map { msg ->
                    coroutineScope {
                        async {
                            val chat = getChat(msg.chatId)
                            TgPost(msg, chat).apply {
                                val content = msg.content
                                if (photo == null && content is TdApi.MessagePhoto) {
                                    photo = loadPhoto(content.photo.sizes.last().photo.id).let {
                                        (it as? TdApi.File)?.local?.path
                                    }
                                }
                                if (chatPhoto == null) {
                                    chat.photo?.small?.id?.let { chatPhotoId ->
                                        chatPhoto =
                                            (loadPhoto(chatPhotoId) as? TdApi.File)?.local?.path
                                    }
                                }
                            }
                        }
                    }
                }
                .awaitAll()
                .filter { post -> post.isChannel }

            channelPosts.addAll(newChannelMessages)
        }

        return channelPosts.take(limit)
    }

    private suspend fun searchMessages(
        query: String,
        offset: Offset = Offset(),
        limit: Int = 10,
        filter: SearchFilter
    ): TdApi.Messages = suspendCoroutine { cont ->
        client.send(
            TdApi.SearchMessages(
                TdApi.ChatListMain(),
                query,
                offset.date,
                offset.chatId,
                offset.messageId,
                limit,
                filter.tgFilter,
                filter.minDate,
                filter.maxDate
            ),
            { response -> cont.resume(response as TdApi.Messages) },
            { e -> cont.resumeWithException(e) }
        )
    }

    private suspend fun getChat(chatId: Long): TdApi.Chat = suspendCoroutine { cont ->
        client.send(
            TdApi.GetChat(chatId),
            { response -> cont.resume(response as TdApi.Chat) },
            { e -> cont.resumeWithException(e) }
        )
    }

    private suspend fun loadPhoto(fileId: Int): TdApi.Object = suspendCoroutine { cont ->
        client.send(
            TdApi.DownloadFile(
                fileId,
                TgClient.Priorities.HIGH.toInt(),
                0,
                0,
                true
            ),
            { obj -> cont.resume(obj) },
            { e -> cont.resumeWithException(e) }
        )
    }

    companion object {
        private const val TAG = "TelegramDataSource"
    }
}
