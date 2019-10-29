package track.search

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.http4k.client.OkHttp
import org.http4k.core.HttpHandler
import org.http4k.core.Method
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response

internal class TracksRepository(session: SpotifySession = SpotifySession()) {
    // TODO refactor - lots of duplication in deserialisation methods and Track classes
    val client = OkHttp()

    private val token = jacksonObjectMapper()
        .readValue(
            session.getAccessToken().bodyString(),
            AccessToken::class.java
        ).access_token

    internal fun playlistFinder(playlistLimit: Int = 2, spotifyHttpHandler: HttpHandler = client): Response {
        return spotifyHttpHandler(Request(
            method = GET,
            uri = "https://api.spotify.com/v1/users/mattthompson34/playlists?access_token=$token&limit=$playlistLimit"
        ))
    }

    internal fun getTracks(
        playlists: Response = playlistFinder(),
        tracksLinks: List<String>? = listTracksLinks(playlists),
        tracksLimit: Int = 3
    ): List<String> {
        val trackIds = mutableListOf<String>()

        tracksLinks?.map { tracksLink ->
            val response = client(Request(GET, "$tracksLink?access_token=$token&limit=$tracksLimit"))
            deserializeTracksResponse(response).items?.map { trackIds += it.track.id }
        }

        return trackIds
    }

    internal fun getTracksWithAudioFeatures(
        trackIds: List<String> = getTracks(),
        valence: Double = 0.0
    ): List<TrackWithAudioFeatures> {
        val tracksWithAudioFeatures = mutableListOf<TrackWithAudioFeatures>()

        trackIds.map {trackId ->
            val response = client(
                Request(GET, "https://api.spotify.com/v1/audio-features/$trackId?access_token=$token")
            )
            // Alternative could be... http4k filters -> interceptor in client to check for 429 and retry-after
            Thread.sleep(80)
            tracksWithAudioFeatures += deserializeAudioFeaturesResponse(response)
        }

//        return jacksonObjectMapper().writeValueAsString(tracksWithAudioFeatures.filter { it.valence > valence })
        return tracksWithAudioFeatures.filter { it.valence > valence }
    }

    private fun listTracksLinks(playlists: Response): List<String>? {
        return deserializePlaylistResponse(playlists)
            .items?.map { it.tracks.href }
    }

    internal fun deserializePlaylistResponse(playlists: Response): Playlists {
        return jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .readValue(
                playlists.bodyString(),
                Playlists::class.java
            )
    }

    internal fun deserializeTracksResponse(tracks: Response): TrackList {
        return jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .readValue(
                tracks.bodyString(),
                TrackList::class.java
            )
    }

    internal fun deserializeAudioFeaturesResponse(track: Response): TrackWithAudioFeatures {
        return jacksonObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
            .readValue(
                track.bodyString(),
                TrackWithAudioFeatures::class.java
            )
    }
}

internal data class Playlists(
    val items: List<Playlist>?
)

internal data class Playlist(val tracks: PlaylistTracksLink)

internal data class PlaylistTracksLink(val href: String)

internal data class TrackList(
    val items: List<TrackItem>?
)

internal data class TrackItem(val track: Track)

internal data class Track(val id: String)

internal data class TrackWithAudioFeatures(
    val id: String,
    val valence: Double,
    val tempo: Double
)