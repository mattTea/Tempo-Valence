package track.search

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.client.OkHttp
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import com.fasterxml.jackson.annotation.JsonProperty



internal class TracksRepository(session: SpotifySession = SpotifySession()) {
    private val token = jacksonObjectMapper()
        .readValue(
            session.getAccessToken().bodyString(),
            AccessToken::class.java
        ).access_token

    internal fun playlistFinder(): Response {
        val url = "https://api.spotify.com/v1/users/mattthompson34/playlists?access_token=$token&limit=50"
        val request = Request(method = GET, uri = url)
        val client = OkHttp()

        println(client(request))
        return client(request)
    }


    internal fun getTracks(playlists: Response): String {
        /*
        1. get tracks for each playlist -> items[i].tracks.href (i.e. https://api.spotify.com/v1/playlists/5SBdn3LK0VTTHx4daMNFCa/tracks)
        - deserialize playlist response to a List<Playlist> -> only need playlistIds
        - loop through listOf(playlistIds)
        - for each one call /playlists/$playlistId/tracks endpoint

        2. get each track id -> items[i].track.id (i.e. "7di4QTqNCZjX4JUFKhWQsr")
        - deserialize each tracks response to a List<Track> -> only need trackIds
        */

        val playlists = jacksonObjectMapper().readValue<Playlists>(playlists.bodyString())

        val playlistId = ""
        val url = "https://api.spotify.com/v1/playlists/$playlistId/tracks"

        return ""
    }

    internal fun deserializePlaylistResponse(playlistFinder: Response): Playlists {
        return jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .readValue(playlistFinder.bodyString(),
                Playlists::class.java
            )
    }
}

data class Playlists(
    val playlists: List<Playlist>?
)

data class Playlist(var tracksHref: String) {

    @JsonProperty("tracks")
    private fun unpackNested(tracks: Map<String, Any>) {
        this.tracksHref = tracks["href"] as String
    }
}