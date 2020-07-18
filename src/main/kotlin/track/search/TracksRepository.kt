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
    // TODO lots of duplication in Track classes
    private val client = OkHttp()

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
        return spotifyHttpHandler(
            Request(
                method = GET,
                uri = "https://api.spotify.com/v1/users/mattthompson34/playlists?access_token=$token&limit=$playlistLimit&offset=$offset"
            )
        )
    }

    internal fun getTracks(
        playlists: Response = playlistFinder(),
        tracksLinks: List<String>? = listTracksLinks(playlists),
        // fields: String = "tracks.items(track(name,id,artists(name)))",
        tracksLimit: Int = 3,
        spotifyHttpHandler: HttpHandler = client
    ): List<Track> =
        tracksLinks?.flatMap { tracksLink ->
            deserializeTracksResponse(
                spotifyHttpHandler(Request(GET, "$tracksLink?access_token=$token&limit=$tracksLimit"))
            ).items?.map {
                Track(it.track.id, it.track.artists, it.track.name)
            } ?: emptyList()
        } ?: emptyList()

    internal fun getTracksWithAudioFeatures(
        tracks: List<Track> = getTracks(),
        valence: Double = 0.0,
        spotifyHttpHandler: HttpHandler = client
    ): List<EnrichedTrackWithAudioFeatures> =
        tracks.map { track ->
            deserializeAudioFeaturesResponse(
                spotifyHttpHandler(
                    Request(GET, "https://api.spotify.com/v1/audio-features/${track.id}?access_token=$token")
                )
            ).toEnrichedTrackWithAudioFeatures(track.name, track.artists[0].name)
        }.filter { it.valence > valence }

    internal fun listTracksLinks(playlists: Response): List<String>? {
        return deserializePlaylistResponse(playlists)
            .items?.map { it.tracks.href }
    }

    fun deserializePlaylistResponse(playlists: Response): Playlists =
        deserializeConfig().readValue(
            playlists.bodyString(),
            Playlists::class.java
        )

    fun deserializeTracksResponse(tracks: Response): TrackItems =
        deserializeConfig().readValue(
            tracks.bodyString(),
            TrackItems::class.java
        )

    fun deserializeAudioFeaturesResponse(track: Response): TrackWithAudioFeatures =
        deserializeConfig().readValue(
            track.bodyString(),
            TrackWithAudioFeatures::class.java
        )
}

private fun deserializeConfig() =
    jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)

internal data class Playlists(
    val items: List<Playlist>?
)

internal data class Playlist(val tracks: PlaylistTracksLink)

internal data class PlaylistTracksLink(val href: String)

internal data class TrackItems(
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
