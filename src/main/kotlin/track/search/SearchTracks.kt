package track.search

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.ServerFilters.CatchLensFailure
import org.http4k.routing.bind
import org.http4k.routing.routes
import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun server(port: Int): Http4kServer = searchTracks(TracksRepository()).asServer(Jetty(port))

internal fun searchTracks(tracksRepository: TracksRepository): HttpHandler = CatchLensFailure.then(
    routes(
        "/tracks" bind GET to {
            Response(OK).body(tracksRepository.getTracksWithAudioFeatures().toString())
        }
    )
)
