/**
 * [Result] is either [Success] or [Error].
 */
sealed class Result<out T, out E : Exception> {
    data class Success<T>(val value: T) : Result<T, Nothing>()
    data class Error<E : Exception>(val exception: E) : Result<Nothing, E>()
}