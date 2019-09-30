package track.search

import assertk.assertThat
import org.http4k.core.Method
import org.http4k.core.Request
import org.http4k.core.Status.Companion.OK
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object TrackSearchTest : Spek ({
    /*
    Tests to start in a similar vein to ProductSearchTest
    to test the endpoint request and response

    This and the below is wrong! ProductSearch is the http level at the entry to merchandiser api
    not the request out to ATG as I'm trying to assimilate here!

    Maybe start similar to http4k-catchLensFailure project
    Also look at AtgClient http requests in merchandiser -> look at atgProductFinder
    */

    describe("trackSearch() with no arguments") {
        it("should return SUCCESS") {
//            val endpoint = trackSearch() // track.search.trackSearch should return a ContractRoute
//            val response = endpoint(Request(Method.GET, " https://api.spotify.com"))

//            assertThat(response.status).isEqualTo(OK)
        }
    }
})