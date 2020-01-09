import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.json.JsonFeature

actual fun configuredClient(): HttpClient {
    return HttpClient(Js) {
        install(JsonFeature)
    }
}