package track.search

import org.http4k.core.HttpHandler
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.core.then
import org.http4k.filter.ServerFilters.CatchLensFailure
import org.http4k.lens.Query
import org.http4k.routing.bind
import org.http4k.routing.routes

internal fun searchTracks(tracksRepository: TracksRepository): HttpHandler = CatchLensFailure.then(
    routes(
        "/" bind GET to {
            Response(OK).body("Welcome to TempoValence!")
        },

        "/tracks" bind GET to { request ->
            val valence = Query.optional("valence")(request)
            val tracks = tracksRepository.getTracksWithAudioFeatures(valence = valence?.toDouble() ?: 0.0)

            Response(OK).body(tracks.toString())
        }
    )
)
