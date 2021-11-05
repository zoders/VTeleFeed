package ru.technopark.vtelefeed

import io.reactivex.rxjava3.core.Maybe
import io.reactivex.rxjava3.core.Observable
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

    fun getChannelMessagesSingle(
        query: String,
        offsetDate: Int = 0,
        offsetChatId: Long = 0L,
        offsetMessageId: Long = 0L,
        limit: Int = 10,
        filter: TdApi.SearchMessagesFilter = TdApi.SearchMessagesFilterEmpty(),
        minDate: Int = 0,
        maxDate: Int = 0
    ): Maybe<List<TdApi.Message>> {
        var haveMoreMessages: Boolean

        return client.sendSingle(
            TdApi.SearchMessages(
                TdApi.ChatListMain(),
                query,
                offsetDate,
                offsetChatId,
                offsetMessageId,
                limit,
                filter,
                minDate,
                maxDate
            )
        )
            .flatMapObservable { response ->
                Observable.fromArray(*(response as TdApi.Messages).messages)
            }
            .flatMap<Pair<TdApi.Message, TdApi.Chat>> { message ->

                val chatObservable =
                    client.sendSingle(TdApi.GetChat(message.chatId)).toObservable()

                return@flatMap Observable.fromArray(message)
                    .zipWith(chatObservable)
                    { zipMessage, zipChat -> zipMessage to (zipChat as TdApi.Chat) }
            }
            .filter { pair ->
                val chatType = pair.second.type
                chatType is TdApi.ChatTypeSupergroup && chatType.isChannel
            }
            .map { it.first }

            .toList()
            .filter { it.isNotEmpty() }
    }

    suspend fun getChannelsMessages(
        limit: Int,
        offsetMessage: TdApi.Message? = null,
        minDate: Int = 0,
        maxDate: Int = 0
    ): ChannelsMessages {
        val query = " "

        var messages = listOf<TdApi.Message>()
        val posts = mutableListOf<TdApi.Message>()

        while (posts.size < limit) {
            val lastMessage = messages.lastOrNull()

            messages = searchMessages(
                query,
                lastMessage?.date ?: offsetMessage?.date ?: 0,
                lastMessage?.chatId ?: offsetMessage?.chatId ?: 0L,
                lastMessage?.id ?: offsetMessage?.id ?: 0L,
                limit,
                TdApi.SearchMessagesFilterEmpty(),
                minDate,
                maxDate
            ).messages.toList()

            val channelMessages = messages
                .map { msg -> withContext(coroutineContext) { async { msg to getChat(msg.chatId) } } }
                .awaitAll()
                .filter { pair: Pair<TdApi.Message, TdApi.Chat> ->
                    val chatType = pair.second.type
                    chatType is TdApi.ChatTypeSupergroup && chatType.isChannel
                }
                .map { it.first }

            posts.addAll(channelMessages)
        }

        return ChannelsMessages(posts, messages.last())
    }

    private suspend fun searchMessages(
        query: String,
        offsetDate: Int = 0,
        offsetChatId: Long = 0L,
        offsetMessageId: Long = 0L,
        limit: Int = 10,
        filter: TdApi.SearchMessagesFilter = TdApi.SearchMessagesFilterEmpty(),
        minDate: Int = 0,
        maxDate: Int = 0
    ): TdApi.Messages = suspendCoroutine { cont ->
        client.send(
            TdApi.SearchMessages(
                TdApi.ChatListMain(),
                query,
                offsetDate,
                offsetChatId,
                offsetMessageId,
                limit,
                filter,
                minDate,
                maxDate
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
}
