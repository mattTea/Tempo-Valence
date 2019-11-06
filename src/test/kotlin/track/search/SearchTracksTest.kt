package track.search

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object SearchTracksTest : Spek({

    val mockTrackWithAudioFeatures = mockk<TrackWithAudioFeatures>()

    val mockTracksRepository = mockk<TracksRepository>()
    every { mockTracksRepository.listTracksLinks(any()) } returns mockk()
    every { mockTracksRepository.getTracks(any(), any(), any())} returns mockk()
    every { mockTracksRepository.playlistFinder(any(), any(), any()) } returns mockk()
    every { mockTracksRepository.getTracksWithAudioFeatures(any(), any()) } returns listOf(mockTrackWithAudioFeatures)

    val endpoint = searchTracks(mockTracksRepository)

    describe("GET /") {
        val response = endpoint(Request(GET, "/"))

        it("should return OK (200)") {
            assertThat(response.status).isEqualTo(OK)
        }

        it("should return welcome message") {
            assertThat(response.bodyString()).isEqualTo("Welcome to TempoValence!")
        }
    }

    describe("GET /tracks") {
        val response = endpoint(Request(GET, "/tracks"))

        it("should return OK (200)") {
            assertThat(response.status).isEqualTo(OK)
        }

        it("should call getTracksWithAudioFeatures()") {
            verify { mockTracksRepository.getTracksWithAudioFeatures(any(), any()) }
        }
    }
})