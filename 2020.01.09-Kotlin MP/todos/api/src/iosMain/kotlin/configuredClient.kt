import io.ktor.client.HttpClient
import io.ktor.client.engine.ios.Ios
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer

actual fun configuredClient(): HttpClient {
    return HttpClient(Ios) {
        install(JsonFeature) {
            serializer = KotlinxSerializer()
            // acceptContentTypes = listOf(ContentType.Application.Json)
        }
    }
}