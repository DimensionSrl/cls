import it.dimension.todo.data.ToDo

//TODO: use some kind Response as return type
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
    suspend fun toggle(todo: ToDo): Result<ToDo, Exception> //TODO: too specific?

    /**
     * Retrieves all the [ToDo]s.
     */
    suspend fun getAll(): Result<List<ToDo>, Exception>

    /**
     * Retrieve a specific [ToDo].
     */
    suspend fun get(id: Long): Result<ToDo?, Exception> //TODO: useless?
}