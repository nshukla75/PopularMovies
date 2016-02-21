# PopularMovies

To fetch popular movies, project is using the API from themoviedb.org.If you don’t already have an account, you will need to create    one in order to request an API Key.In your request for a key, state that your usage will be for educational/non-commercial use.You     will also need to provide some personal information to complete the request.Once you submit your request, you should receive your key via email shortly after.In order to request popular movies you will want to request data from the /discover/movie endpoint. An API Key is required.Once you obtain your key, you append it to gradle.properties

##P2 - Popular Movies App, Stage 2 
###The Rubric

####Required Components
    To “meet specifications”, your app must fulfill all the criteria listed in this section of the rubric.

- **User Interface - Layout**

  - Movies are displayed in the main layout via a grid of their corresponding movie poster thumbnails
  - UI contains an element (e.g., a spinner or settings menu) to toggle the sort order of the movies by: most popular, highest rated,      and favorites
  - UI contains a screen for displaying the details for a selected movie
  - Movie Details layout contains title, release date, movie poster, vote average, and plot synopsis.
  - Movie Details layout contains a section for displaying trailer videos and user reviews
  - Tablet UI uses a Master-Detail layout implemented using fragments. The left fragment is for discovering movies. The right fragment     displays the movie details view for the currently selected movie.

- **User Interface - Function**

  - When a user changes the sort criteria (most popular, highest rated, and favorites) the main view gets updated correctly.
  - When a movie poster thumbnail is selected, the movie details screen is launched [Phone] or displayed in a fragment [Tablet]
  - When a trailer is selected, app uses an Intent to launch the trailer
  - In the movies detail screen, a user can tap a button(for example, a star) to mark it as a Favorite

- **Network API Implementation**

  - In a background thread, app queries the /discover/movies API with the query parameter for the sort criteria specified in the           settings menu. (Note: Each sorting criteria is a different API call.)
  - This query can also be used to fetch the related metadata needed for the detail view.
  - App requests for related videos for a selected movie via the /movie/{id}/videos endpoint in a background thread and displays those     details when the user selects a movie.
  - App requests for user reviews for a selected movie via the /movie/{id}/reviews endpoint in a background thread and displays those      details when the user selects a movie.

- **Data Persistence**

  - App saves a “Favorited” movie to SharedPreferences or a database using the movie’s id.
  - When the “favorites” setting option is selected, the main view displays the entire favorites collection based on movie IDs stored      in SharedPreferences or a database.

####Common Project Requirements
  - App conforms to common standards found in the Android Nanodegree General Project Guidelines

####Optional Components
  To receive “exceeds specifications”, your app must fully implement all the criteria listed in this section of the rubric.

  - App persists favorite movie details using a database
  - App displays favorite movie details even when offline
  - App uses a ContentProvider* to populate favorite movie details  *Student may use a library to generate a content provider rather       than build one by hand
  - Sharing Functionality
  - Movie Details View includes an Action Bar item that allows the user to share the first trailer video URL from the list of trailers
  - App uses a share Intent to expose the external youtube URL for the trailer





