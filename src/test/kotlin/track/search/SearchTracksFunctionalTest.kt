package track.search

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import org.http4k.client.OkHttp
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object SearchTracksFunctionalTest : Spek({

    val fakeSpotifyResponse = """[
        |TrackWithAudioFeatures(id=2fyV7tqiCT2YU7KoKTu1hr, valence=0.778, tempo=77.262),
        |TrackWithAudioFeatures(id=0f7PA0ivwtkehbA5gitH8M, valence=0.912, tempo=134.897),
        |TrackWithAudioFeatures(id=11uFqlWnAlFMeQmjHKY38X, valence=0.0625, tempo=195.986)
        |]""".trimMargin()

    val fakeSpotify =
        routes("/fake/tracks" bind GET to {
            Response(OK)
                .body(fakeSpotifyResponse)
                .header("Content-Type", "application/json")
        })

    val fakeSpotifyServer = fakeSpotify.asServer(Jetty(0))

    val client = OkHttp()

    beforeEachTest {
        fakeSpotifyServer.start()
    }

    afterEachTest {
        fakeSpotifyServer.stop()
    }

    describe("GET /tracks") {

        it("should return 200 (OK)", timeout = 250000) {
            val response = client(Request(GET, "http://localhost:${fakeSpotifyServer.port()}/fake/tracks"))
            assertThat(response.status).isEqualTo(OK)
        }

        it("should return body string", timeout = 250000) {
            val response = client(Request(GET, "http://localhost:${fakeSpotifyServer.port()}/fake/tracks"))
            assertThat(response.bodyString()).contains("id=2fyV7tqiCT2YU7KoKTu1hr")
        }
    }
})