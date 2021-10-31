package com.android.vtelefeed

import java.util.*

data class Post(var id:Int = 0, var fromVkOrTg: Int = 0, var text: String = "", var date: Date = Date())