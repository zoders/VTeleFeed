package ru.technopark.vtelefeed.data.tg

import kotlinx.serialization.Serializable
import org.drinkless.td.libcore.telegram.TdApi

@Serializable
data class TgPost(
    val id: Long,
    val text: String,
    val date: Int,
    var photo: String? = null,
    val chatId: Long,
    val chatTitle: String,
    var chatPhoto: String? = null,
    val isChannel: Boolean
) {
    constructor(message: TdApi.Message, chat: TdApi.Chat) :
            this(
                message.id,
                when (val content = message.content) {
                    is TdApi.MessageText -> content.text.text
                    is TdApi.MessagePhoto -> content.caption.text
                    is TdApi.MessageVideo -> content.caption.text
                    else -> "---no-text---"
                },
                message.date,
                (message.content as? TdApi.MessagePhoto)
                    ?.photo?.sizes?.lastOrNull()?.photo?.local?.path?.takeIf { it.isNotEmpty() },
                chat.id,
                chat.title,
                chat.photo?.small?.local?.path?.takeIf { it.isNotEmpty() },
                chat.type.let { it is TdApi.ChatTypeSupergroup && it.isChannel }
            )
}
