package track.search

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import org.http4k.core.Status.Companion.OK
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object AuthenticateTest : Spek({

    describe("getAccessToken()") {
        it("should return OK (200)") {
            assertThat(getAccessToken().status).isEqualTo(OK)
        }

        it("should return body containing access_token String") {
            assertThat(getAccessToken().bodyString()).contains("access_token")
        }
    }
})