package track.search

import assertk.assertThat
import assertk.assertions.isInstanceOf
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object TracksRepositoryIntegrationTest : Spek({
    describe("TracksRepository.getTracksWithAudioFeatures()") {
        it("should return list of tracks with tempo and valence details", timeout = 250000) {
            val tracks = TracksRepository().getTracksWithAudioFeatures()

            println("Tracks with audio features: $tracks")
            println(tracks.size)

            assertThat(tracks).isInstanceOf(List::class.java)
        }
    }
})