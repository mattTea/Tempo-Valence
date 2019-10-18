package track.search

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.doesNotContain
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
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

object SearchTracksFunctionalTest : Spek({

    val fakeApiResponse = """[
        |TrackWithAudioFeatures(id=2fyV7tqiCT2YU7KoKTu1hr, valence=0.778, tempo=77.262),
        |TrackWithAudioFeatures(id=0f7PA0ivwtkehbA5gitH8M, valence=0.912, tempo=134.897),
        |TrackWithAudioFeatures(id=11uFqlWnAlFMeQmjHKY38X, valence=0.0625, tempo=195.986)
        |]""".trimMargin()

//    val body = jacksonObjectMapper().readValue(fakeApiResponse, ListOfTracksWithAudioFeatures::class.java)
//    println(body)

    val fakeApi =
        routes(
            "/fake/tracks" bind GET to { request ->
                val valence = Query.optional("valence")(request)

                Response(OK)
                    .body(fakeApiResponse)
                    .header("Content-Type", "application/json")
            }
        )

    val fakeApiServer = fakeApi.asServer(Jetty(0))

    val client = OkHttp()

    beforeEachTest {
        fakeApiServer.start()
    }

    afterEachTest {
        fakeApiServer.stop()
    }

    describe("GET /tracks") {

        it("should return 200 (OK)", timeout = 250000) {
            val response = client(Request(GET, "http://localhost:${fakeApiServer.port()}/fake/tracks"))

            assertThat(response.status).isEqualTo(OK)
        }

        it("should return body string", timeout = 250000) {
            val response = client(Request(GET, "http://localhost:${fakeApiServer.port()}/fake/tracks"))

            assertThat(response.bodyString()).contains("id=2fyV7tqiCT2YU7KoKTu1hr")
        }
    }

    describe("GET /tracks?valence=aValue") {

        it("should return filtered list of tracks", timeout = 250000) {
            val response = client(
                Request(GET, "http://localhost:${fakeApiServer.port()}/fake/tracks?valence=0.7")
            )

            assertThat(response.bodyString()).doesNotContain("id=11uFqlWnAlFMeQmjHKY38X")
        }
    }
})

internal data class ListOfTracksWithAudioFeatures(
    val enrichedTracks: List<TrackWithAudioFeatures>?
)