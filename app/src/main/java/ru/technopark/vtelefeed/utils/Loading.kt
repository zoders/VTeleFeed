package ru.technopark.vtelefeed.utils

import org.drinkless.td.libcore.telegram.TdApi

object Loading : TdApi.Object() {
    override fun getConstructor(): Int = 1000000
}
