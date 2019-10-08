package track.search

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsAll
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
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
                    {"tracks": {"href": "https://api.spotify.com/v1/playlists/5SBdn3LK0VTTHx4daMNFCa/tracks"} }
                ]
            }"""

            val fakePlayListFinder = Response(OK)
                .body(fakePlaylistFinderResponse)
                .header("Content-Type", "application/json")

            val tracks = PlaylistTracksLink("https://api.spotify.com/v1/playlists/5SBdn3LK0VTTHx4daMNFCa/tracks")
            val deserializedPlaylists = Playlists(listOf(
                Playlist(tracks)
            ))

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

    describe("deserializeAudioFeaturesResponse()") {
        it("should return the audio features for a track") {
            val fakeAudioFeaturesResponse = """{
                "danceability": 0.689,
                "valence": 0.535,
                "tempo": 126.019,
                "id": "5M3xy3FI55IhNEDSiB2aTn"
            }"""

            val fakeAudioFeaturesFinder = Response(OK)
                .body(fakeAudioFeaturesResponse)
                .header("Content-Type", "application/json")

            val trackWithAudioFeatures = TrackWithAudioFeatures(
                id = "5M3xy3FI55IhNEDSiB2aTn",
                valence = 0.535,
                tempo = 126.019
            )

            assertThat(tracksRepository.deserializeAudioFeaturesResponse(fakeAudioFeaturesFinder))
                .isEqualTo(trackWithAudioFeatures)
        }
    }

    describe("getTracks()") {
        // TODO should mock call and responses to individual tracksLinks
        val fakePlaylistFinderResponse = """{
                "items": [
                    {"tracks": {"href": "https://api.spotify.com/v1/playlists/5SBdn3LK0VTTHx4daMNFCa/tracks"} },
                    {"tracks": {"href": "https://api.spotify.com/v1/playlists/1UwtzP4yehAaJjEn1NQcOe/tracks"} }
                ]
            }"""

        val fakePlaylistFinder = Response(OK)
            .body(fakePlaylistFinderResponse)
            .header("Content-Type", "application/json")

        it("should return a list") {
            assertThat(tracksRepository.getTracks(fakePlaylistFinder)).isInstanceOf(List::class.java)
        }

        it("should return list containing track ids") {
            val aTrackId = "7di4QTqNCZjX4JUFKhWQsr"
            val anotherTrackId = "5M3xy3FI55IhNEDSiB2aTn"
            assertThat(tracksRepository.getTracks(fakePlaylistFinder)).containsAll(aTrackId, anotherTrackId)
        }
    }

    describe("getAudioFeatures()") {
        val listOfTrackIds = listOf("7di4QTqNCZjX4JUFKhWQsr", "5M3xy3FI55IhNEDSiB2aTn")

        it("should return a list") {
            assertThat(tracksRepository.getTracksWithAudioFeatures(listOfTrackIds)).isInstanceOf(List::class.java)
        }

        it("should return a list of TracksWithAudioFeatures") {
            val track1 = TrackWithAudioFeatures(
                id = "7di4QTqNCZjX4JUFKhWQsr",
                valence = 0.878,
                tempo = 117.024
            )

            val track2 = TrackWithAudioFeatures(
                id = "5M3xy3FI55IhNEDSiB2aTn",
                valence = 0.535,
                tempo = 126.019
            )

            val tracksWithAudioFeatures = listOf(track1, track2)
            assertThat(tracksRepository.getTracksWithAudioFeatures(listOfTrackIds)).isEqualTo(tracksWithAudioFeatures)
        }
    }
})