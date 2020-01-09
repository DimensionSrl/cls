package it.dimension.todo.data

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Serializable
data class ToDo(
    val id: Long = Random.nextLong(), //TODO: UUID? Default to 0 and let server generate it?
    val title: String,
    val done: Boolean = false,
    @Serializable(with = DateTimeSerializer::class)
    val createdAt: DateTime = dateTimeNow()
) {
    companion object {
        val EMPTY = ToDo(title = "Qualcosa da fare")
    }
}