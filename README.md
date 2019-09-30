TempoValence
============

Search Spotify's api for music based on its Tempo (beats per minute) and Valence (positivity or happiness)

------

## Technologies I plan to use

- Kotlin
- http4k (http framework)
- result4k (for exception handling)
- OAuth
- (GCP, Kubernetes, Docker and an automated CI pipeline)

------

## Structure

Structure code similar to merchandiser...

- Methods that handle initial http request (from UI, eg)
- Methods that deal with the external api/client http requests
- Try to use repository pattern

------

## Spotify api

Link to spotify [api console](https://developer.spotify.com/documentation/web-api/)


Possible future link to song lyrics too with [Lyrics ovh](https://lyricsovh.docs.apiary.io/#)
    - Pretty basic
    - (No auth)

------

## Useful links

https://github.com/thelinmichael/spotify-web-api-java

https://medium.com/@elfanos/spotify-api-with-kotlin-react-and-redux-b1e23bb39b8c