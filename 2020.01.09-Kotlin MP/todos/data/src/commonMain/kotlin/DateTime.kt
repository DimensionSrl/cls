package it.dimension.todo.data

import kotlinx.serialization.*
import kotlinx.serialization.internal.StringDescriptor

@Serializer(forClass = DateTime::class)
object DateTimeSerializer : KSerializer<DateTime> {

    override val descriptor: SerialDescriptor = StringDescriptor.withName("DateTime")

    override fun serialize(encoder: Encoder, obj: DateTime) {
        encoder.encodeString(obj.toIso8601Timestamp())
    }

    override fun deserialize(decoder: Decoder): DateTime = dateTime(decoder.decodeString())
}

expect class DateTime

expect fun DateTime.toIso8601Timestamp(): String
expect fun dateTime(iso8601TimeStamp: String): DateTime

expect fun dateTimeNow(): DateTime