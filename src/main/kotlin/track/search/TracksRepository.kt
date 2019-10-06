package track.search

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.http4k.client.OkHttp
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response


internal class TracksRepository(session: SpotifySession = SpotifySession()) {
    val client = OkHttp()

    private val token = jacksonObjectMapper()
        .readValue(
            session.getAccessToken().bodyString(),
            AccessToken::class.java
        ).access_token

    internal fun playlistFinder(): Response {
        val url = "https://api.spotify.com/v1/users/mattthompson34/playlists?access_token=$token&limit=50"
        val request = Request(method = GET, uri = url)

        println(client(request))
        return client(request)
    }


    internal fun getTracks(
        playlists: Response = playlistFinder(),
        tracksLinks: List<String>? = listTracksLinks(playlists)
    ): List<String> {
        val client = OkHttp()
        val trackIds = mutableListOf<String>()

        tracksLinks?.map { tracksLink ->
            val response = client(Request(GET, "$tracksLink?access_token=$token&limit=50"))
            deserializeTracksResponse(response).items?.map { trackIds.add(it.track.id) }
        }

        return trackIds
    }

    private fun listTracksLinks(playlists: Response): List<String>? {
        return deserializePlaylistResponse(playlists)
            .items?.map { it.playlistTracksLink.href }
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

internal data class Playlist(val playlistTracksLink: PlaylistTracksLink)

internal data class PlaylistTracksLink(val href: String)

internal data class TrackList(
    val items: List<TrackItem>?
)

internal data class TrackItem(val track: Track)

internal data class Track(val id: String)