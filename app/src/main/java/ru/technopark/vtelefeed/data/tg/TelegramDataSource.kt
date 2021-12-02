package ru.technopark.vtelefeed.data.tg

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TelegramDataSource(private val client: Client) {

    suspend fun getChannelsPosts(
        limit: Int,
        offset: Offset = Offset(),
        minDate: Int = 0,
        maxDate: Int = 0
    ): ChannelsTgPosts {
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
                            TgPost(msg, getChat(msg.chatId)).apply {
                                chat.photo?.let { photo ->
                                    photo.small = loadPhoto(photo.small.id) as? TdApi.File
                                }
                                (message.content as? TdApi.MessagePhoto)?.photo?.let { photo ->
                                    photo.sizes.last().photo =
                                        loadPhoto(photo.sizes.last().photo.id) as? TdApi.File
                                }
                            }
                        }
                    }
                }
                .awaitAll()
                .filter { post ->
                    val chatType = post.chat.type
                    chatType is TdApi.ChatTypeSupergroup && chatType.isChannel
                }

            channelPosts.addAll(newChannelMessages)
        }

        val channelMessagesWithLimit = channelPosts.take(limit)

        val lastChannelMessage = channelMessagesWithLimit.last()

        return ChannelsTgPosts(
            channelMessagesWithLimit,
            Offset(
                lastChannelMessage.message.date,
                lastChannelMessage.message.chatId,
                lastChannelMessage.message.id
            )
        )
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
