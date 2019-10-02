package track.search

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.http4k.core.Status.Companion.OK
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object TracksRepositoryTest : Spek({

    describe("getAllTracks()") {

        val tracksRepository = TracksRepository()

        it("should return OK (200)") {
            assertThat(tracksRepository.getAllTracks().status).isEqualTo(OK)
        }
    }
})