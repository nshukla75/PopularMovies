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

