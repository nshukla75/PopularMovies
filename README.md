# PopularMovies

To fetch popular movies, project is using the API from themoviedb.org.
○ If you don’t already have an account, you will need to create one in order to request an API Key.
■ In your request for a key, state that your usage will be for educational/non-commercial use.
You will also need to provide some personal information to complete the request.
Once you submit your request, you should receive your key via email shortly after.

○ In order to request popular movies you will want to request data from the /discover/movie endpoint. An API Key is required.

○ Once you obtain your key, you append it to doInBackground() method in FetchMovieTask class in MainActivityFragment.java:
■  // Please Enter the key below
  String apiKeyStr =[YOUR API KEY]


P1 - Popular Movies App, Stage 1 Rubric
The Rubric
Required Components

1. To “meet specifications”, your app must fulfill all the criteria listed in this section of the rubric.

Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails
UI contains an element (i.e a spinner or settings menu) to toggle the sort order of the movies by: most popular, highest rated
UI contains a screen for displaying the details for a selected movie
Movie details layout contains title, release date, movie poster, vote average, and plot synopsis.
When a user changes the sort criteria (“most popular and highest rated”) the main view gets updated correctly.
When a movie poster thumbnail is selected, the movie details screen is launched

2. Network API Implementation 

In a background thread, app queries the /discover/movies API with the query parameter for the sort criteria specified in the settings menu. (Note: Each sorting criteria is a different API call.)
This query can also be used to fetch the related metadata needed for the detail view.

3. General Project Guidelines

App conforms to common standards found in the Android Nanodegree General Project Guidelines (NOTE: For Stage 1 of the Popular Movies App, it is okay if the app does not restore the data using onSaveInstanceState/onRestoreInstanceState)
Optional Components

There are no optional components for stage 1.
