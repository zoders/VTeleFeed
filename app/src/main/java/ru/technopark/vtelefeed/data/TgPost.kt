package ru.technopark.vtelefeed.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.drinkless.td.libcore.telegram.TdApi

@Serializable
@SerialName("TgPost")
data class TgPost(
    override val id: Long,
    val text: String,
    override val date: Int,
    var photo: String? = null,
    val chatId: Long,
    val chatTitle: String,
    var chatPhoto: String? = null,
    val isChannel: Boolean
) : BasePost() {
    constructor(message: TdApi.Message, chat: TdApi.Chat) : this(
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
