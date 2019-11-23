package track.search

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.http4k.client.OkHttp
import org.http4k.core.Body
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.format.Jackson.auto
import kotlin.random.Random

internal class TracksRepository(session: SpotifySession = SpotifySession()) {
    // TODO refactor - lots of duplication in deserialisation methods and Track classes
    val client = OkHttp()

    private val token = jacksonObjectMapper()
        .readValue(
            session.getAccessToken().bodyString(),
            AccessToken::class.java
        ).access_token

    internal fun playlistFinder(
        playlistLimit: Int = 10,
        offset: Int = Random.nextInt(0, 100),
        spotifyHttpHandler: HttpHandler = client
    ): Response {
        return spotifyHttpHandler(Request(
            method = GET,
            uri = "https://api.spotify.com/v1/users/mattthompson34/playlists?access_token=$token&limit=$playlistLimit&offset=$offset"
        ))
    }

    internal fun getTracks(
        playlists: Response = playlistFinder(),
        tracksLinks: List<String>? = listTracksLinks(playlists),
        tracksLimit: Int = 3,
        spotifyHttpHandler: HttpHandler = client
    ): List<Track> {
        val tracks = mutableListOf<Track>()

        tracksLinks?.map { tracksLink ->
            val response = spotifyHttpHandler(Request(GET, "$tracksLink?access_token=$token&limit=$tracksLimit"))
            deserializeTracksResponse(response).items?.map { tracks += Track(it.track.id, it.track.artists, it.track.name) }
        }

        return tracks
    }

    internal fun getTracksWithAudioFeatures(
        tracks: List<Track> = getTracks(),
        valence: Double = 0.0,
        spotifyHttpHandler: HttpHandler = client
    ): List<EnrichedTrackWithAudioFeatures> {
        val enrichedTracksWithAudioFeatures = mutableListOf<EnrichedTrackWithAudioFeatures>()

        tracks.map { track ->
            val response = spotifyHttpHandler(
                Request(GET, "https://api.spotify.com/v1/audio-features/${track.id}?access_token=$token")
            )

            val trackWithAudioFeatures = deserializeAudioFeaturesResponse(response)
            enrichedTracksWithAudioFeatures += trackWithAudioFeatures.toEnrichedTrackWithAudioFeatures(track.name, track.artists[0].name)
        }

        return enrichedTracksWithAudioFeatures.filter { it.valence > valence }
    }

    internal fun listTracksLinks(playlists: Response): List<String>? {
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

internal data class Artist(
    val name: String
)

internal data class Track(
    val id: String,
    val artists: List<Artist>,
    val name: String
)

internal data class TrackWithAudioFeatures(
    val id: String,
    val valence: Double,
    val tempo: Double
) {
    fun toEnrichedTrackWithAudioFeatures(name: String, artist: String): EnrichedTrackWithAudioFeatures {
        return EnrichedTrackWithAudioFeatures(
            id = this.id,
            name = name,
            artist = artist,
            valence = this.valence,
            tempo = this.tempo
        )
    }
}

internal data class EnrichedTrackWithAudioFeatures(
    val id: String,
    val name: String,
    val artist: String,
    val valence: Double,
    val tempo: Double
)

internal data class EnrichedTracksWithAudioFeatures(val enrichedTracksWithAudioFeatures: List<EnrichedTrackWithAudioFeatures>) {
    companion object {
        val format = Body.auto<EnrichedTracksWithAudioFeatures>().toLens()
    }
}
