package track.search

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
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

    describe("deserializePlaylistResponse()") {
        it("should return list of Playlist ids") {
            val fakePlaylistFinderResponse = """{
                "items": [
                    {"playlistTracksLink": {"href": "https://api.spotify.com/v1/playlists/5SBdn3LK0VTTHx4daMNFCa/tracks"} }
                ]
            }"""

            val fakePlayListFinder = Response(OK)
                .body(fakePlaylistFinderResponse)
                .header("Content-Type", "application/json")

            val tracks = PlaylistTracksLink("https://api.spotify.com/v1/playlists/5SBdn3LK0VTTHx4daMNFCa/tracks")
            val deserializedPlaylists = Playlists(listOf(
                Playlist(tracks)
            ))

//            println(jacksonObjectMapper().writeValueAsString(Playlists(listOf(Playlist(tracks)))))
            assertThat(tracksRepository.deserializePlaylistResponse(fakePlayListFinder)).isEqualTo(deserializedPlaylists)
        }
    }

    describe("deserializeTracksResponse()") {
        it("should return a list of Track ids") {
            val fakeTracksFinderResponse = """{
                "items": [
                    {"track": {"id": "7di4QTqNCZjX4JUFKhWQsr"} },
                    {"track": {"id": "5M3xy3FI55IhNEDSiB2aTn"} }
                ]
            }"""

            val fakeTracksFinder = Response(OK)
                .body(fakeTracksFinderResponse)
                .header("Content-Type", "application/json")

            val firstTrack = TrackItem(Track("7di4QTqNCZjX4JUFKhWQsr"))
            val secondTrack = TrackItem(Track("5M3xy3FI55IhNEDSiB2aTn"))
            val deserializedTracks = TrackList(listOf(firstTrack, secondTrack))

            assertThat(tracksRepository.deserializeTracksResponse(fakeTracksFinder)).isEqualTo(deserializedTracks)
        }
    }

    describe("getTracks()") {
        // TODO should mock call and responses to tracksLinks
        it("should return a list of track ids") {
            val fakePlaylistFinderResponse = """{
                "items": [
                    {"playlistTracksLink": {"href": "https://api.spotify.com/v1/playlists/5SBdn3LK0VTTHx4daMNFCa/tracks"} },
                    {"playlistTracksLink": {"href": "https://api.spotify.com/v1/playlists/1UwtzP4yehAaJjEn1NQcOe/tracks"} }
                ]
            }"""

            val fakePlayListFinder = Response(OK)
                .body(fakePlaylistFinderResponse)
                .header("Content-Type", "application/json")

            val aTrackId = "7di4QTqNCZjX4JUFKhWQsr"

            assertThat(tracksRepository.getTracks(fakePlayListFinder)).contains(aTrackId)
        }
    }
})