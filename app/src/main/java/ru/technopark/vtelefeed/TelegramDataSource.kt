package ru.technopark.vtelefeed

import android.util.Log
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.drinkless.td.libcore.telegram.Client
import org.drinkless.td.libcore.telegram.TdApi
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class TelegramDataSource(private val client: Client) {

    suspend fun getChannelsMessages(
        limit: Int,
        offset: Offset = Offset(),
        minDate: Int = 0,
        maxDate: Int = 0
    ): ChannelsMessages {
        val query = " "

        var allMessages = listOf<TdApi.Message>()
        val channelMessages = mutableListOf<TdApi.Message>()

        while (channelMessages.size < limit) {
            val lastMessage = allMessages.lastOrNull()

            allMessages = searchMessages(
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

            val newChannelMessages = allMessages
                .map { msg ->
                    withContext(coroutineContext) {
                        async { msg to getChat(msg.chatId) }
                    }
                }
                .awaitAll()
                .filter { pair: Pair<TdApi.Message, TdApi.Chat> ->
                    val chatType = pair.second.type
                    chatType is TdApi.ChatTypeSupergroup && chatType.isChannel
                }
                .map { it.first }

            Log.i(TAG, newChannelMessages.toString())

            channelMessages.addAll(newChannelMessages)
        }

        val lastMessage = allMessages.last()

        return ChannelsMessages(
            channelMessages,
            Offset(lastMessage.date, lastMessage.chatId, lastMessage.id)
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

    companion object {
        private const val TAG = "TelegramDataSource"
    }
}
