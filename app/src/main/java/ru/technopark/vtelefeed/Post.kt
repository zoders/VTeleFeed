package ru.technopark.vtelefeed

import java.util.Date

data class Post(
    var id: Int = 0,
	var fromVkOrTg: Int = 0,
	var text: String = "",
	var date: Date = Date()
)
