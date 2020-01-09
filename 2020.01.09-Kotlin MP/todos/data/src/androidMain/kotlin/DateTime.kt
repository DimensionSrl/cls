package it.dimension.todo.data

import java.text.SimpleDateFormat
import java.util.*

private object DateTimeFormatter {
    val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sssZZZ", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
}

actual typealias DateTime = Date

actual fun DateTime.toIso8601Timestamp(): String = DateTimeFormatter.formatter.format(this)
actual fun dateTime(iso8601TimeStamp: String): DateTime = DateTimeFormatter.formatter.parse(iso8601TimeStamp)
actual fun dateTimeNow(): DateTime = Date()