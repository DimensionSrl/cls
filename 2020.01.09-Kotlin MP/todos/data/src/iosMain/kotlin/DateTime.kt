package it.dimension.todo.data

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSTimeZone
import platform.Foundation.timeZoneWithName

object DateTimeFormatter {
    val formatter = NSDateFormatter().apply {
        dateFormat = "yyyy-MM-dd'T'HH:mm:ss.sssZZZ"
        timeZone = NSTimeZone.timeZoneWithName("UTC")!!
    }
}

actual typealias DateTime = NSDate

actual fun DateTime.toIso8601Timestamp(): String = DateTimeFormatter.formatter.stringFromDate(this)
actual fun dateTime(iso8601TimeStamp: String): DateTime = DateTimeFormatter.formatter.dateFromString(iso8601TimeStamp)!!
actual fun dateTimeNow(): DateTime = NSDate()