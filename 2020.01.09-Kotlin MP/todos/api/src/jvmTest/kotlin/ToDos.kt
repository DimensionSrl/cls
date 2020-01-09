import it.dimension.todo.data.ToDo
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ToDos {

    @Test
    fun getAll() = runBlocking {
        client.create()
        client.create()
        client.getAll()
        return@runBlocking
    }

    @Test
    fun get() = runBlocking {
        client.update(ToDo.EMPTY.copy(17))
        client.get(17)
        return@runBlocking
    }

    @Test
    fun create() = runBlocking {
        client.create()
        return@runBlocking
    }

    @Test
    fun delete() = runBlocking {
        val todo = ToDo.EMPTY.copy(19)
        client.update(todo)
        client.delete(todo)
        return@runBlocking
    }

    @Test
    fun toggle() = runBlocking {
        val todo = ToDo.EMPTY.copy(20)
        client.update(todo)
        client.toggle(todo)
        val updated = client.get(todo.id)
        assertNotNull(updated)
        print("updated: $updated")
        assertTrue(updated is Result.Success)
        //assertNotEquals(todo.done, updated.value.done)
        return@runBlocking
    }

    @Test
    fun getAllCallback() = runBlocking {
        callbackClient.getAll(success = { all ->
            println("${all.size}")
            return@getAll
        }, error = { error ->
            throw error
        })
    }

    companion object {
        val client = Client("http://localhost:8080")
        val callbackClient = CallbackClient(client)
    }
}