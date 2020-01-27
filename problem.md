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

------

## Next...

1. Build api with with routes that hit specific RESTful endpoints
    - GET /
    - GET /tracks
    - GET /tracks?valence=<some_value>&tempo=<some_value>
    
2. Way to run app locally...

```bash
./runlocal.sh
```
    
3. New (4th Nov 2019) usage before continuing with hosting, etc
- Enter a valence value as at present
- `playlistFinder()` (could randomise an offset to call nth 25 playlists)
- `getTracks()` listTracksLinks (could return randomised 100 tracks) 
- `getTracksWithAudioFeatures()` (could return 3 random tracks over valence value)
    - also return names of tracks, artists, valence values and tempo for each
    
    - make call to "https://api.spotify.com/v1/tracks/$trackId" for first one
    - get item.name & item.artists[0].name and combine into TrackWithAudioFeatures class (or new class)
    - create a new function to do this...
        - getTracks & getTracksWithAudioFeatures probably just return one track to inject here
        
        - hit this "https://api.spotify.com/v1/tracks/$trackId?access_token=$token"
            - and return 'artists[0].name' & 'name'
    
    
23.11.19 - IDEA I shouldn't be injecting each repository method into the next in a sequence, but keeping them as isolated methods with a single 'endpoint' function that uses them all.
- I can then test the in isolation, then mock them all when I test the endpoint function    


- FUTURE: return a play button to hear the tracks


4. Deploy in GCP? (https://cloud.google.com/run/docs/quickstarts/build-and-deploy)
    - Hosting of app (api) - containerisation of app and management in k8s?
    - (https://cloud.google.com/kubernetes-engine/docs/tutorials/hello-app)
    
    Things to think about...
    - Endpoint needs a url to be accessed on
    - (https://cloud.google.com/kubernetes-engine/docs/tutorials/configuring-domain-name-static-ip)
    - Create docker image -> just in the pipeline or elsewhere? Maybe create one locally first?
    
        To package and deploy your application on GKE, you must:
        
        - Package your app into a Docker image
        - Run the container locally on your machine (optional)
        - Upload the image to a registry
        - Create a container cluster
        - Deploy your app to the cluster
        - Expose your app to the Internet
        - Scale up your deployment
        - Deploy a new version of your app
    
    - Authentication

5. CI pipeline (gitlab) - first build stage set up

6. Authorise api (cloud endpoints, with JWT etc)