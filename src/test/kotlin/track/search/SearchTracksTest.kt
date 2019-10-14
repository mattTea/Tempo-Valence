package track.search

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.every
import io.mockk.mockk
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object SearchTracksTest : Spek({

    describe("GET /tracks") {

        val mockTrackWithAudioFeatures = mockk<TrackWithAudioFeatures>()

        val mockTracksRepository = mockk<TracksRepository>()
        every { mockTracksRepository.getTracksWithAudioFeatures() } returns listOf(mockTrackWithAudioFeatures)

        val endpoint = searchTracks(mockTracksRepository)
        val response = endpoint(Request(GET, "/tracks"))

        it("should return OK (200)") {
            assertThat(response.status).isEqualTo(OK)
        }
    }
})