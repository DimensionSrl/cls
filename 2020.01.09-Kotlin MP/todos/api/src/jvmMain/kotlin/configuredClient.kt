import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer

actual fun configuredClient(): HttpClient {
    return HttpClient(OkHttp) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
            // acceptContentTypes = listOf(ContentType.Application.Json)
        }
    }
}