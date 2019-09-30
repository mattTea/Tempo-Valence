Problem breakdown
=================

Start with simple integration to spotify api

- Return a `200 OK` response


------

## URLs and cURL commands

#### Command to GET audio features...

```shell script
curl -X GET "https://api.spotify.com/v1/audio-features/06AKEBrKUckW0KREUWRnvT" -H "Authorization: Bearer BQDp7eEdej4nNLtuYuPVCw8CA2gbXi9QRoLcLZM9bAs_5GgBu-IB0FJtGBXs5poegl2LFUdotBayDvbGdRI"
```

where `Bearer` is replaced by new access token

------

#### URL examples...

1. Audio features of a specific track
```http request
https://api.spotify.com/v1/audio-features/06AKEBrKUckW0KREUWRnvT?access_token=BQDp7eEdej4nNLtuYuPVCw8CA2gbXi9QRoLcLZM9bAs_5GgBu-IB0FJtGBXs5poegl2LFUdotBayDvbGdRI
```

2. User's playlists
```http request
https://api.spotify.com/v1/users/mattthompson34/playlists?access_token=BQDp7eEdej4nNLtuYuPVCw8CA2gbXi9QRoLcLZM9bAs_5GgBu-IB0FJtGBXs5poegl2LFUdotBayDvbGdRI
```

where `access_token` is replaced by new access token

------

### Structure of eventual app...

1. Set up the spotify `GET` to get all my playlists or downloaded songs

2. Enrich songs with valence and tempo (in my application)

3. (Possibly store this in datastore or cache)

4. http request into my app will search based on valence and tempo 