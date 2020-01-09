import it.dimension.todo.data.ToDo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

private fun <T> Result<T, Exception>.toCallbacks(success: (T) -> Unit, error: (Exception) -> Unit) {
    when (this) {
        is Result.Success -> success(this.value)
        is Result.Error -> error(this.exception)
    }
}

expect fun callbackDispatcher(): CoroutineContext

/**
 * Wraps a [Client] providing callback-style access to suspend functions.
 *
 * Note: callbacks will be invoked on the [MainScope].
 */
class CallbackClient(private val client: Client) {

    private val callbackScope = CoroutineScope(callbackDispatcher())

    fun create(title: String?, success: (ToDo) -> Unit, error: (Exception) -> Unit) {
        callbackScope.launch {
            client.create(title).toCallbacks(success, error)
        }
    }

    fun get(id: Long, success: (ToDo) -> Unit, error: (Exception) -> Unit) {
        callbackScope.launch {
            client.get(id).toCallbacks(success, error)
        }
    }

    fun getAll(success: (List<ToDo>) -> Unit, error: (Exception) -> Unit) {
        callbackScope.launch {
            client.getAll().toCallbacks(success, error)
        }
    }

    fun update(todo: ToDo, success: (ToDo) -> Unit, error: (Exception) -> Unit) {
        callbackScope.launch {
            client.update(todo).toCallbacks(success, error)
        }
    }

    fun toggle(todo: ToDo, success: (ToDo) -> Unit, error: (Exception) -> Unit) {
        callbackScope.launch {
            client.toggle(todo).toCallbacks(success, error)
        }
    }

    fun delete(todo: ToDo, success: (Boolean) -> Unit, error: (Exception) -> Unit) {
        callbackScope.launch {
            client.delete(todo).toCallbacks(success, error)
        }
    }
}