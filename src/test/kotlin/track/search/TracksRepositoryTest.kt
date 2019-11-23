package track.search

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

object TracksRepositoryTest : Spek({
    // TODO should mock a session to pass in to TracksRepository() to isolate
    val tracksRepository = TracksRepository()
    val playlistLimit = 2
    val tracksLimit = 3

    describe("playlistFinder()") {
        val fakeSpotifyPlaylistsResponse = """{"items": [{"tracks": {"href": "http://playlistTracks"}}]}"""

        it("should return OK (200)") {
            assertThat(
                tracksRepository.playlistFinder(
                    playlistLimit = playlistLimit,
                    offset = 0,
                    spotifyHttpHandler = { Response(OK).body(fakeSpotifyPlaylistsResponse) }
                ).status
            ).isEqualTo(OK)
        }

        it("should return Playlists for user 'mattthompson34") {
            assertThat(
                tracksRepository.playlistFinder(
                    playlistLimit = playlistLimit,
                    offset = 0,
                    spotifyHttpHandler = { Response(OK).body(fakeSpotifyPlaylistsResponse) }
                ).bodyString()
            ).isEqualTo("""{"items": [{"tracks": {"href": "http://playlistTracks"}}]}""")
        }
    }

    describe("getTracks()") {
        val fakePlaylistFinderResponse = """{"items": [{"tracks": {"href": "http://playlistTracks"}}]}"""

        val fakePlaylistFinder = Response(OK)
            .body(fakePlaylistFinderResponse)
            .header("Content-Type", "application/json")

        val fakeSpotifyGetTracksResponseWithNameAndArtist = """{
          "items": [
            {
              "track": {
                "artists": [
                  {
                    "name": "artistName"
                  }
                ],
                "id": "trackId",
                "name": "trackName"
              }
            }
          ]
        }"""

        it("should return a list") {
            assertThat(tracksRepository.getTracks(
                playlists = fakePlaylistFinder,
                tracksLimit = tracksLimit,
                spotifyHttpHandler = { Response(OK).body(fakeSpotifyGetTracksResponseWithNameAndArtist) }
            )).isInstanceOf(List::class.java)
        }

        it("should return list of tracks with trackId with trackName") {
            assertThat(tracksRepository.getTracks(
                playlists = fakePlaylistFinder,
                tracksLimit = tracksLimit,
                spotifyHttpHandler = { Response(OK).body(fakeSpotifyGetTracksResponseWithNameAndArtist) }
            )).isEqualTo(listOf(Track("trackId", "trackName")))
        }
    }

    describe("getTracksWithAudioFeatures()") {
        val fakeSpotifyAudioFeaturesResponse = """{"id":"trackId","valence":0.8,"tempo":100.0}"""

        val listOfTracks = listOf(Track("trackId", "trackName"))

        val enrichedTrackWithAudioFeatures = EnrichedTrackWithAudioFeatures(
            id = "trackId",
            name = "trackName",
            valence = 0.8,
            tempo = 100.0
        )

        val enrichedTracksWithAudioFeatures = listOf(enrichedTrackWithAudioFeatures)

        it("should return a list") {
            assertThat(tracksRepository.getTracksWithAudioFeatures(
                tracks = listOfTracks,
                spotifyHttpHandler = { Response(OK).body(fakeSpotifyAudioFeaturesResponse) }
            )).isInstanceOf(List::class.java)
        }

        it("should return a list of EnrichedTracksWithAudioFeatures") {
            assertThat(tracksRepository.getTracksWithAudioFeatures(
                tracks = listOfTracks,
                spotifyHttpHandler = { Response(OK).body(fakeSpotifyAudioFeaturesResponse) }
            )).isEqualTo(enrichedTracksWithAudioFeatures)
        }

        it("should return a filtered list based on valence") {
            assertThat(
                tracksRepository.getTracksWithAudioFeatures(
                    tracks = listOfTracks,
                    spotifyHttpHandler = { Response(OK).body(fakeSpotifyAudioFeaturesResponse) },
                    valence = 0.7
                )
            ).isEqualTo(listOf(enrichedTrackWithAudioFeatures))

            assertThat(
                tracksRepository.getTracksWithAudioFeatures(
                    tracks = listOfTracks,
                    spotifyHttpHandler = { Response(OK).body(fakeSpotifyAudioFeaturesResponse) },
                    valence = 0.9
                )
            ).isEqualTo(emptyList<EnrichedTrackWithAudioFeatures>())
        }
    }

    describe("deserializePlaylistResponse()") {
        it("should return list of Playlist ids") {
            val fakePlaylistFinderResponse = """{
                "items": [
                    {"tracks": {"href": "http://aPlaylist/tracks"} }
                ]
            }"""

            val fakePlayListFinder = Response(OK)
                .body(fakePlaylistFinderResponse)
                .header("Content-Type", "application/json")

            val tracks = PlaylistTracksLink("http://aPlaylist/tracks")

            val deserializedPlaylists = Playlists(listOf(Playlist(tracks)))

            assertThat(tracksRepository.deserializePlaylistResponse(fakePlayListFinder)).isEqualTo(deserializedPlaylists)
        }
    }

    describe("deserializeTracksResponse()") {
        it("should return a list of Track ids") {
            val fakeTracksFinderResponse = """{
                "items": [
                    {"track": {"id": "firstTrackId", "name": "firstTrackName"} },
                    {"track": {"id": "secondTrackId", "name": "secondTrackName"} }
                ]
            }"""

            val fakeTracksFinder = Response(OK)
                .body(fakeTracksFinderResponse)
                .header("Content-Type", "application/json")

            val firstTrack = TrackItem(Track("firstTrackId", "firstTrackName"))
            val secondTrack = TrackItem(Track("secondTrackId", "secondTrackName"))
            val deserializedTracks = TrackList(listOf(firstTrack, secondTrack))

            assertThat(tracksRepository.deserializeTracksResponse(fakeTracksFinder)).isEqualTo(deserializedTracks)
        }
    }

    describe("deserializeAudioFeaturesResponse()") {
        it("should return the audio features for a track") {
            val fakeAudioFeaturesResponse = """{
                "valence": 0.5,
                "tempo": 125.0,
                "id": "trackId"
            }"""

            val fakeAudioFeaturesFinder = Response(OK)
                .body(fakeAudioFeaturesResponse)
                .header("Content-Type", "application/json")

            val trackWithAudioFeatures = TrackWithAudioFeatures(
                id = "trackId",
                valence = 0.5,
                tempo = 125.0
            )

            assertThat(tracksRepository.deserializeAudioFeaturesResponse(fakeAudioFeaturesFinder))
                .isEqualTo(trackWithAudioFeatures)
        }
    }
})