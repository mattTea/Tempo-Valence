package track.search.model

internal data class Playlists(
    val items: List<Playlist>?
)

internal data class Playlist(val tracks: PlaylistTracksLink)

internal data class PlaylistTracksLink(val href: String)