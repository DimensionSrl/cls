import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import it.dimension.todo.data.ToDo
import it.dimension.cls.todos.api.Result

expect fun configuredClient(): HttpClient

class Client(
    /**
     * The host.
     * For example http://localhost:8080.
     */
    val host: String
) : Api {

    private val client: HttpClient by lazy {
        configuredClient()
    }

    override suspend fun create(title: String?): Result<ToDo, Exception> {
        val todo = title?.let { ToDo(title = it) } ?: ToDo.EMPTY
        return try {
            client.post<ToDo>("$host/todos") {
                body = todo
                contentType(ContentType.Application.Json)
            }.let { response ->
                Result.Success(response)
            }
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }

    override suspend fun update(todo: ToDo): Result<ToDo, Exception> {
        return try {
            client.put<ToDo>("$host/todos/${todo.id}") {
                body = todo
                contentType(ContentType.Application.Json)
            }.let { response ->
                Result.Success(response)
            }
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }

    override suspend fun delete(todo: ToDo): Result<Boolean, Exception> {
        return try {
            client.delete<HttpResponse>("$host/todos/${todo.id}").let { response ->
                Result.Success(response.status.isSuccess())
            }
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }

    override suspend fun toggle(todo: ToDo): Result<ToDo, Exception> {
        fun ToDo.toggle() = copy(done = done.not())
        return update(todo.toggle())
    }

    override suspend fun getAll(): Result<List<ToDo>, Exception> {
        return try {
            client.get<List<ToDo>>("$host/todos").let { response ->
                Result.Success(response)
            }
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }

    override suspend fun get(id: Long): Result<ToDo, Exception> {
        return try {
            client.get<ToDo>("$host/todos/$id").let { response ->
                Result.Success(response)
            }
        } catch (ex: Exception) {
            Result.Error(ex)
        }
    }
}