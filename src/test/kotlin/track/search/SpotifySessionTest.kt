package track.search

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import org.http4k.core.Status.Companion.OK
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object SpotifySessionTest : Spek({

    describe("getAccessToken()") {
        val session = SpotifySession()

        it("should return OK (200)") {
            assertThat(session.getAccessToken().status).isEqualTo(OK)
        }

        it("should return body containing access_token String") {
            assertThat(session.getAccessToken().bodyString()).contains("access_token")
        }
    }
})