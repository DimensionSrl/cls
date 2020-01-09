import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.response.respondText
import io.ktor.routing.*
import io.ktor.serialization.serialization
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import it.dimension.todo.data.ToDo
import kotlinx.serialization.json.JsonConfiguration
import kotlin.random.Random

/**
 * Create a copy of [this] with the properties of [other] except [ToDo.id].
 */
private fun ToDo.copyWithValues(other: ToDo) = copy(title = other.title, done = other.done, createdAt = other.createdAt)

/**
 * Create a copy of [this] but with the given [id].
 */
private fun ToDo.copyWithId(id: Long) = copy(id = id)

/**
 * Backing data.
 */
private val messages: MutableMap<Long, ToDo> = mapOf(ToDo.EMPTY.id to ToDo.EMPTY).toMutableMap()

fun main(args: Array<String>) {
    val server = embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) {
            serialization(json = kotlinx.serialization.json.Json(JsonConfiguration.Stable.copy(prettyPrint = true)))
        }
        routing {
            get("/") {
                call.respondText("TO-DO Server", ContentType.Text.Plain)
            }
            get("todos/{id?}") {
                when (val id = call.parameters["id"]?.toLongOrNull()) {
                    null -> call.respond(synchronized(messages) {
                        // GET All
                        messages.values.toList().sortedBy { it.createdAt }
                    })
                    else -> when (val found = synchronized(messages) {
                        // GET {id}
                        messages[id]
                    }) {
                        null -> call.respond(HttpStatusCode.NotFound)
                        else -> call.respond(found)
                    }
                }
            }
            post("/todos") {
                val todo = call.receive<ToDo>()
                val created = synchronized(messages) {
                    todo.copyWithId(Random.nextLong()).also {
                        messages[it.id] = it
                    }
                }
                call.respond(created)
            }
            put("/todos/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                val todo: ToDo? = call.receive()
                when {
                    id == null -> call.respond(HttpStatusCode.NotFound)
                    todo == null -> call.respond(HttpStatusCode.BadRequest)
                    else -> {
                        val (code, updated) = synchronized(messages) {
                            when (val existing = messages[id]) {
                                null -> HttpStatusCode.Created to todo.copyWithId(id).also {
                                    messages[id] = it
                                }
                                else -> HttpStatusCode.OK to existing.copyWithValues(todo).also {
                                    messages[id] = it
                                }
                            }
                        }
                        call.respond(code, updated)
                    }
                }
            }
            delete("/todos/{id}") {
                val id = call.parameters["id"]?.toLongOrNull()
                val deleted = synchronized(messages) {
                    messages.remove(id) != null
                }
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }
        }
    }
    server.start(wait = true)
}