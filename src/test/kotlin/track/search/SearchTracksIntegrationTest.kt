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

object SearchTracksIntegrationTest : Spek({

    val client = OkHttp()

    val server = server(0)

    beforeEachTest {
        server.start()
    }

    afterEachTest {
        server.stop()
    }

    describe("GET /tracks endpoint") {

        it("should return 200 (OK)") {
            val response = client(Request(GET, "http://localhost:${server.port()}/tracks"))
            assertThat(response.status).isEqualTo(OK)
        }

        it("should return body string") {
            val response = client(Request(GET, "http://localhost:${server.port()}/tracks"))
            assertThat(response.bodyString()).contains("valence")
        }
    }
})