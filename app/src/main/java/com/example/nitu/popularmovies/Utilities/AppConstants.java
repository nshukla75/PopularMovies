package com.example.nitu.popularmovies.Utilities;

/**
 * Created by nitus on 10/26/2015.
 */
public class AppConstants {
    public static final String MOVIE_API_KEY = "7537b743615a000671a98c32d354df39";
    public static final String BASE_URL = "http://api.themoviedb.org/3/discover/movie";
    public static final String SORT_BY="sort_by";
    public static final String PAGE_NO="page";
    public static final String API_KEY="api_key";
    public static final String POPULARITY = "popularity.desc";
    public static final String RATING = "vote_average.desc";
    public static final String POPULAR_MOVIES_URL = "http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key="+MOVIE_API_KEY;
    public static final String MOVIE_W185_URL="http://image.tmdb.org/t/p/w185";
    public static final String MOVIE_URL="http://image.tmdb.org/t/p/w342";
    public static final String MOVIE_DETAIL_URL="http://image.tmdb.org/t/p/w780";
    public static final String DETAIL_MOVIE_OBJECT="movie_detail";
    public static final String MOVIE_LIST_FROM_BUNDLE_KEY="movie_list_from_bundle_key";
    public static final String MOVIE_DTL_FROM_BUNDLE_KEY="movie_list_from_bundle_key";
    public static final int FETCH_LIMIT=12;
    public static final String MOVIE_YOUTUBE_URL="https://www.youtube.com/watch?v=";
    public static final String MOVIE_REVIEWS_TRAILER_BASE_URL="http://api.themoviedb.org/3/movie";

}
