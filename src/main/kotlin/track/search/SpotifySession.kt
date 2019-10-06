package track.search

import org.http4k.client.OkHttp
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import track.search.EnvironmentKeys.CLIENT_KEY

internal class SpotifySession {
    fun getAccessToken(): Response {

        val request = Request(POST, "https://accounts.spotify.com/api/token")
            .query("grant_type", "client_credentials")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("Authorization", "Basic $CLIENT_KEY")

        val client = OkHttp()

        println("Access Token response: ${client(request).body}")
        return client(request)
    }
}

data class AccessToken(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val scope: String
)