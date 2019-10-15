package track.search

import org.http4k.server.Http4kServer
import org.http4k.server.Jetty
import org.http4k.server.asServer

fun main() {
    server(9000)
}

fun server(port: Int): Http4kServer = searchTracks(TracksRepository()).asServer(Jetty(port)).start()