package track.search

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object TracksRepositoryTest : Spek({
    // TODO should mock a session to pass in to TracksRepository() to isolate
    val tracksRepository = TracksRepository()

    describe("getPlaylists()") {
        it("should return OK (200)") {
            assertThat(tracksRepository.playlistFinder().status).isEqualTo(OK)
        }

        it("should return Playlists for user 'mattthompson34") {
            assertThat(tracksRepository.playlistFinder().bodyString()).contains("mattthompson34")
        }
    }

    describe("deserializePlaylists()") {
        it("should return list of Playlist ids") {
            val fakePlaylistFinderResponse = """{
                "items": [
                    {"tracks": {"href": "https://api.spotify.com/v1/playlists/5SBdn3LK0VTTHx4daMNFCa/tracks"} }
                ]
            }"""

            val fakePlayListFinder = Response(OK)
                .body(fakePlaylistFinderResponse)
                .header("Content-Type", "application/json")

            val tracks = Tracks("https://api.spotify.com/v1/playlists/5SBdn3LK0VTTHx4daMNFCa/tracks")
            val deserializedPlaylists = Playlists(listOf(
                Playlist(tracks)
            ))

            println(jacksonObjectMapper().writeValueAsString(Playlists(listOf(Playlist(tracks)))))
            assertThat(tracksRepository.deserializePlaylistResponse(fakePlayListFinder)).isEqualTo(deserializedPlaylists)
        }
    }

    describe("getTracks()") {
        it("should...") {
            val fakePlaylistFinderResponse = """{
                "items": [
                    {"tracks": {"href": "https://api.spotify.com/v1/playlists/5SBdn3LK0VTTHx4daMNFCa/tracks"} },
                    {"tracks": {"href": "https://api.spotify.com/v1/playlists/5SBdn3LK0VTTHx4daMNFCa/tracks"} }
                ]
            }"""

            val fakePlayListFinder = Response(OK)
                .body(fakePlaylistFinderResponse)
                .header("Content-Type", "application/json")

            assertThat(tracksRepository.getTracks(fakePlayListFinder)).isEqualTo("")
        }
    }
})