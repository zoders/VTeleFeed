package ru.technopark.vtelefeed.data.tg

import org.drinkless.td.libcore.telegram.TdApi

class SearchFilter(
    val tgFilter: TdApi.SearchMessagesFilter = TdApi.SearchMessagesFilterEmpty(),
    val minDate: Int = 0,
    val maxDate: Int = 0
)
