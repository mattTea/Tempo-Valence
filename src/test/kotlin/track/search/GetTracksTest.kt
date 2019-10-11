package track.search

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import org.http4k.client.OkHttp
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object GetTracksFunctionalTest : Spek({

    // TODO this client times out after 10 seconds -> find way to set longer for more tracks
    val client = OkHttp()

    val server = server(3001)

    beforeEachTest {
        server.start()
    }

    afterEachTest {
        server.stop()
    }

    describe("GET /tracks") {

        it("should return 200 (OK)", timeout = 250000) {
            val request = Request(GET, "http://localhost:${server.port()}/tracks")

            val response = client(Request(GET, "http://localhost:${server.port()}/tracks"))

            assertThat(response.status).isEqualTo(OK)
        }

        it("should return body string", timeout = 250000) {
            val response = client(Request(GET, "http://localhost:${server.port()}/tracks"))

            assertThat(response.bodyString()).contains("TrackWithAudioFeatures")
        }
    }
})