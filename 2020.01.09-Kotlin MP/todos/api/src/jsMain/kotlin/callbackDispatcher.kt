import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

actual fun callbackDispatcher(): CoroutineContext = Dispatchers.Default