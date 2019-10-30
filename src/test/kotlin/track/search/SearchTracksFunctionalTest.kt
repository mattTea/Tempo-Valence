package track.search

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import assertk.assertions.isEqualTo
import org.http4k.client.OkHttp
import org.http4k.core.Method.GET
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.lens.Query
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Jetty
import org.http4k.server.asServer
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

// TODO - this is not used, consider deleting
object SearchTracksFunctionalTest : Spek({

    val fakeSpotifyResponse = """[
        |TrackWithAudioFeatures(id=2fyV7tqiCT2YU7KoKTu1hr, valence=0.778, tempo=77.262),
        |TrackWithAudioFeatures(id=0f7PA0ivwtkehbA5gitH8M, valence=0.912, tempo=134.897),
        |TrackWithAudioFeatures(id=11uFqlWnAlFMeQmjHKY38X, valence=0.0625, tempo=195.986)
        |]""".trimMargin()

    val fakeSpotify =
        routes(
            "/fake/tracks" bind GET to { request ->
                val valence = Query.optional("valence")(request)

                Response(OK)
                    .body(fakeSpotifyResponse)
                    .header("Content-Type", "application/json")
            }
        )

    val fakeApiServer = fakeSpotify.asServer(Jetty(0))

    val client = OkHttp()

    beforeEachTest {
        fakeApiServer.start()
    }

    afterEachTest {
        fakeApiServer.stop()
    }

    /*
    this is just testing the fake server - make it call the tempo-valence api and mock the Spotify api response?
    - Change the spotify URI env var like we do in -> DIGITAL_MERCH_ATG_URI of (Uri.of("http://127.0.0.1:${atg.port()}/fake"))
    - so that we can fake the response?

    Possibly also (or alternatively) need to have a mocked repository, which will have a mocked valence arg
    - This is already being done in SearchTracksTest?
    */

    describe("GET /tracks") {

        xit("should return 200 (OK)", timeout = 250000) {
            val response = client(Request(GET, "http://localhost:${fakeApiServer.port()}/fake/tracks"))

            assertThat(response.status).isEqualTo(OK)
        }

        xit("should return body string", timeout = 250000) {
            val response = client(Request(GET, "http://localhost:${fakeApiServer.port()}/fake/tracks"))

            assertThat(response.bodyString()).contains("id=2fyV7tqiCT2YU7KoKTu1hr")
        }
    }

    describe("GET /tracks?valence=aValue") {

        xit("should return filtered list of tracks", timeout = 250000) {
            val response = client(
                Request(GET, "http://localhost:${fakeApiServer.port()}/fake/tracks?valence=0.7")
            )

            assertThat(response.bodyString()).doesNotContain("id=11uFqlWnAlFMeQmjHKY38X")
        }
    }
})
