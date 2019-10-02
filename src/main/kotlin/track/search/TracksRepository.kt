package track.search

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.client.OkHttp
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response

internal class TracksRepository(session: SpotifySession = SpotifySession()) {
    private val token = jacksonObjectMapper().readValue<AccessToken>(session.getAccessToken().bodyString()).access_token

    private val url = "https://api.spotify.com/v1/users/mattthompson34/playlists?access_token=$token"

    // TODO change return type to List<Track> or change method name to getAllPlaylists()
    internal fun getAllTracks(): Response {
        val request = Request(method = GET, uri = url)
        val client = OkHttp()

        return client(request)
    }
}

data class AccessToken(
    val access_token: String,
    val token_type: String,
    val expires_in: Int,
    val scope: String
)

//data class Track(
//    val name: String,
//    val artist: String
//)