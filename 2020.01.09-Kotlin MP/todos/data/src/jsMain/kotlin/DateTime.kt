package it.dimension.todo.data

import kotlin.js.Date

actual typealias DateTime = Date

actual fun DateTime.toIso8601Timestamp(): String = TODO("")
actual fun dateTime(iso8601TimeStamp: String): DateTime = TODO("")
actual fun dateTimeNow(): DateTime = TODO("")