import it.dimension.todo.data.ToDo
import it.dimension.cls.todos.api.Result

interface Api {

    /**
     * Create a new [ToDo].
     * @return The new [ToDo].
     */
    suspend fun create(title: String? = null): Result<ToDo, Exception>

    /**
     * Update a [ToDo].
     * @return The updated [ToDo].
     */
    suspend fun update(todo: ToDo): Result<ToDo, Exception>

    /**
     * Delete a [ToDo].
     */
    suspend fun delete(todo: ToDo): Result<Boolean, Exception>

    /**
     * Toggle the [ToDo.done] status of a [ToDo].
     */
    suspend fun toggle(todo: ToDo): Result<ToDo, Exception>

    /**
     * Retrieves all the [ToDo]s.
     */
    suspend fun getAll(): Result<List<ToDo>, Exception>

    /**
     * Retrieve a specific [ToDo].
     */
    suspend fun get(id: Long): Result<ToDo?, Exception>
}