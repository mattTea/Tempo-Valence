package track.search

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.http4k.contract.ContractRoute
import org.http4k.contract.bindContract
import org.http4k.routing.bind
import org.http4k.contract.contract
import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Method.POST
import org.http4k.core.Request
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import track.search.EnvironmentKeys.CLIENT_KEY

internal const val requestBody = """{"grant_type": "client_credentials"}"""

internal val request = Request(POST, "https://accounts.spotify.com/api/token")
    .body(requestBody)
    .header("Content-Type", "application/x-www-form-urlencoded")
    .header("Authorization", "Basic $CLIENT_KEY")

// TODO this isn't right, I don't need to call endpoints for this yet
internal val response = endpoints(request)


data class AccessTokenResponse(val access_token: String, val token_type: String, val expires_in: Int, val scope: String)

val token = jacksonObjectMapper().readValue<AccessTokenResponse>(response.toString())


internal fun endpoints(request: Request): HttpHandler {
    return contract {
        routes += spotifyEndpoints()
    }
}

internal fun spotifyEndpoints(): ContractRoute {
    return getTracks()
}

internal fun getTracks(): ContractRoute =
    "https://api.spotify.com/v1/users/mattthompson34/playlists?access_token=$token" bindContract GET to {
        Response(OK)
    }
