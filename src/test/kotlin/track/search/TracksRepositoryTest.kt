package track.search

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.containsAll
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import org.http4k.core.Method.GET
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.http4k.routing.bind
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object TracksRepositoryTest : Spek({
    // TODO should mock a session to pass in to TracksRepository() to isolate
    val tracksRepository = TracksRepository()
    val playlistLimit = 2
    val tracksLimit = 3

    describe("playlistFinder()") {

        val fakeSpotifyPlaylistsResponse = """"items": [{
            "href": "https://api.spotify.com/v1/playlists/0mSxv5ujYkMyXsE8RYemS5",
            "id": "0mSxv5ujYkMyXsE8RYemS5",
            "name": "Prince Fatty – In the Viper's Shadow",
            "owner": {
              "display_name": "mattthompson34",
              "external_urls": {
                "spotify": "https://open.spotify.com/user/mattthompson34"
              },
              "href": "https://api.spotify.com/v1/users/mattthompson34",
              "id": "mattthompson34",
              "type": "user",
              "uri": "spotify:user:mattthompson34"
            },
            "tracks": {
              "href": "https://api.spotify.com/v1/playlists/0mSxv5ujYkMyXsE8RYemS5/tracks",
              "total": 10
            }
          }]"""

        val fakeSpotify =
            "/v1/users/mattthompson34/playlists/" bind GET to {
                Response(OK)
                    .body(fakeSpotifyPlaylistsResponse)
                    .header("Content-Type", "application/json")
            }

        it("should return OK (200)") {
            assertThat(tracksRepository.playlistFinder(
                playlistLimit = playlistLimit,
                offset = 0,
                spotifyHttpHandler = fakeSpotify
            ).status).isEqualTo(OK)
        }

        it("should return Playlists for user 'mattthompson34") {
            assertThat(
                tracksRepository.playlistFinder(
                    playlistLimit = playlistLimit,
                    offset = 0,
                    spotifyHttpHandler = fakeSpotify
                ).bodyString()
            ).contains("mattthompson34")
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

            val deserializedPlaylists = Playlists(listOf(Playlist(tracks)))

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
            assertThat(tracksRepository.getTracks(
                playlists = fakePlaylistFinder,
                tracksLimit = tracksLimit
            )).isInstanceOf(List::class.java)
        }

        it("should return list containing track ids") {
            val aTrackId = "7di4QTqNCZjX4JUFKhWQsr"
            val anotherTrackId = "5M3xy3FI55IhNEDSiB2aTn"

            assertThat(tracksRepository.getTracks(playlists = fakePlaylistFinder, tracksLimit = tracksLimit))
                .containsAll(aTrackId, anotherTrackId)
        }
    }

    describe("getTracksWithAudioFeatures()") {
        val fakeSpotifyAudioFeaturesResponse = """{"id":"firstTrackId","valence":0.8,"tempo":100.0}"""

        val listOfTrackIds = listOf("firstTrackId")

        val track1 = TrackWithAudioFeatures(
            id = "firstTrackId",
            valence = 0.8,
            tempo = 100.0
        )

        val tracksWithAudioFeatures = listOf(track1)

        it("should return a list") {
            assertThat(tracksRepository.getTracksWithAudioFeatures(
                trackIds = listOfTrackIds,
                spotifyHttpHandler = { Response(OK).body(fakeSpotifyAudioFeaturesResponse) }
            )).isInstanceOf(List::class.java)
        }

        it("should return a list of TracksWithAudioFeatures") {
            assertThat(tracksRepository.getTracksWithAudioFeatures(
                trackIds = listOfTrackIds,
                spotifyHttpHandler = { Response(OK).body(fakeSpotifyAudioFeaturesResponse) }
            )).isEqualTo(tracksWithAudioFeatures)
        }

        it("should return a filtered list based on valence") {
            assertThat(tracksRepository.getTracksWithAudioFeatures(
                trackIds = listOfTrackIds,
                spotifyHttpHandler = { Response(OK).body(fakeSpotifyAudioFeaturesResponse) },
                valence = 0.7
            )).isEqualTo(listOf(track1))
        }
    }
})