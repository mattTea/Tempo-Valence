package track.search

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import org.http4k.core.Response
import org.http4k.core.Status.Companion.OK
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import track.search.model.Artist
import track.search.model.EnrichedTrackWithAudioFeatures
import track.search.model.Playlist
import track.search.model.PlaylistTracksLink
import track.search.model.Playlists
import track.search.model.Track
import track.search.model.TrackItem
import track.search.model.TrackItems
import track.search.model.TrackWithAudioFeatures

object TracksRepositoryTest : Spek({
    val tracksRepository = TracksRepository()
    val playlistLimit = 2

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
                spotifyHttpHandler = { Response(OK).body(fakeSpotifyGetTracksResponseWithNameAndArtist) }
            )).isInstanceOf(List::class.java)
        }

        it("should return list of tracks with trackId, trackName and artist") {
            assertThat(tracksRepository.getTracks(
                playlists = fakePlaylistFinder,
                spotifyHttpHandler = { Response(OK).body(fakeSpotifyGetTracksResponseWithNameAndArtist) }
            )).isEqualTo(listOf(Track("trackId", listOf(Artist("artistName")), "trackName")))
        }
    }

    describe("getTracksWithAudioFeatures()") {
        val fakeSpotifyAudioFeaturesResponse = """{"id":"trackId","valence":0.8,"tempo":100.0}"""

        val listOfTracks = listOf(Track("trackId", listOf(Artist("artistName")), "trackName"))

        val enrichedTrackWithAudioFeatures = EnrichedTrackWithAudioFeatures(
            id = "trackId",
            name = "trackName",
            artist = "artistName",
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
                {
                  "track": {
                    "artists": [
                      {
                        "name": "firstArtistName"
                      }
                    ],
                    "id": "firstTrackId",
                    "name": "firstTrackName"
                  }
                },
                {
                  "track": {
                    "artists": [
                      {
                        "name": "secondArtistName"
                      }
                    ],
                    "id": "secondTrackId",
                    "name": "secondTrackName"
                  }
                }
              ]
            }"""

            val fakeTracksFinder = Response(OK)
                .body(fakeTracksFinderResponse)
                .header("Content-Type", "application/json")

            val firstTrack = TrackItem(Track("firstTrackId", listOf(Artist("firstArtistName")),"firstTrackName"))
            val secondTrack = TrackItem(Track("secondTrackId", listOf(Artist("secondArtistName")), "secondTrackName"))
            val deserializedTracks = TrackItems(listOf(firstTrack, secondTrack))

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