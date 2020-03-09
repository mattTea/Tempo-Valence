TempoValence
============

**N.B. Repo has moved to [GitLab](https://jl.githost.io/mattTea/tempo-valence) to make use of CI pipeline**


Search Spotify's api for music based on its Tempo (beats per minute) and Valence (positivity or happiness)

------

## To run app

1. Clone repo to your local machine

2. Navigate to root folder

3. Run the following from the command line...

    ```bash
    ./runlocal.sh
    ```
    
    Note - `runlocal.sh` is not included in the public repo as it contains private environment variables
    - To set locally and run app, use the following...
    
    ```bash
    export CLIENT_KEY=private-key
    
    ./gradlew run
    ```

    To run tests from within IntelliJ the `System.getenv("VARIABLE_NAME")` probably won't work.
    
    So, add CLIENT_KEY env var to the project run configuration -> Run > Edit Configurations... > Environment variables: (Browse... button)
    
    Visit the following...
    - `http://localhost:9000` -> route of app
    - `http://localhost:9000/tracks` -> selection of tracks without valence query param
    - `http://localhost:9000/tracks?valence=0.7` -> selection of tracks above valence query param
    
------

## Technologies

- Kotlin - used
- http4k (http framework) - used
- result4k (for exception handling) - planned
- OAuth - planned
- Gitlab CI - used
- GCP, Kubernetes, and Docker - planned

------

## Structure

Structure of code...

- Methods that handle initial http request (from UI, eg)
- Methods that deal with the external api/client http requests
- Use repository pattern

------

## Spotify api

Link to spotify [api console](https://developer.spotify.com/documentation/web-api/)
