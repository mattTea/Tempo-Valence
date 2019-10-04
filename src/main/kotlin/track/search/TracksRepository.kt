package track.search

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.http4k.client.OkHttp
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response


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

        val tracksLinks = deserializePlaylistResponse(playlists)
            .items?.map { it.tracks.href }

        println(tracksLinks)

        val client = OkHttp()

        tracksLinks?.map { tracksLink ->
            val response = client(Request(GET, "$tracksLink?access_token=$token&limit=50"))
            // TODO get below line working!
//            val trackIds = deserializeTracksResponse(response).items?.map { it.id }

            println("response: $response")
        }

        return ""
    }

    internal fun deserializePlaylistResponse(playlists: Response): Playlists {
        return jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .readValue(playlists.bodyString(),
                Playlists::class.java
            )
    }

    internal fun deserializeTracksResponse(tracks: Response): TrackList {
        return jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .readValue(tracks.bodyString(),
                TrackList::class.java
            )
    }
}

internal data class Playlists(
    val items: List<Playlist>?
)

internal data class Playlist(val tracks: Tracks)

internal data class Tracks(val href: String)

internal data class TrackList(val items: List<Track>?)

internal data class Track(val id: String)