package track.search.model

import org.http4k.core.Body
import org.http4k.format.Jackson.auto

internal data class TrackItem(val track: Track)

internal data class TrackItems(
    val items: List<TrackItem>?
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
