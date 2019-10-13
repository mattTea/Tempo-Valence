package track.search

import assertk.assertThat
import assertk.assertions.isInstanceOf
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object TracksRepositoryIntegrationTest : Spek({
    describe("TracksRepository.getTracksWithAudioFeatures()") {
        it("should return list of tracks with tempo and valence details", timeout = 250000) {
            val tracksRepository = TracksRepository()
            val tracks = tracksRepository.getTracksWithAudioFeatures(
                trackIds = tracksRepository.getTracks(
                    playlists = tracksRepository.playlistFinder(playlistLimit = 2),
                    tracksLimit = 3
                )
            )

            assertThat(tracks).isInstanceOf(List::class.java)
            println(tracks.size)
        }
    }
})