package track.search

import assertk.assertThat
import assertk.assertions.isInstanceOf
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object TracksRepositoryIntegrationTest : Spek({
    describe("TracksRepository.getTracksWithAudioFeatures()") {
        xit("should return list of tracks with tempo and valence details", timeout = 30000) {
            val repository = TracksRepository()

            println("Tracks with audio features: ${repository.getTracksWithAudioFeatures()}")

            assertThat(repository.getTracksWithAudioFeatures()).isInstanceOf(List::class.java)
        }
    }
})