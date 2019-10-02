package track.search

import org.http4k.client.OkHttp
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import track.search.EnvironmentKeys.CLIENT_KEY

internal fun getAccessToken(): Response {

    val request = Request(POST, "https://accounts.spotify.com/api/token")
        .query("grant_type", "client_credentials")
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("Authorization", "Basic $CLIENT_KEY")

    val client = OkHttp()

    return client(request)
}