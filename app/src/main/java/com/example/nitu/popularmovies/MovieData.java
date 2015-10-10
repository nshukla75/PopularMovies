package com.example.nitu.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nitus on 10/6/2015.
 */
public class MovieData implements Parcelable {
    private int id;
    private String movie_id;
    private String original_title;
    private String poster_path;
    private String overview;
    private String release_date;
    private String vote_average;
    private String vote_count;

    public MovieData(){}
    public MovieData(String movie_id,
                     String original_title,
                     String poster_path,
                     String overview,
                     String release_date,
                     String vote_average,
                     String vote_count){
        this.movie_id = movie_id;
        this.original_title = original_title;
        this.poster_path = poster_path;
        this.overview = overview;
        this.release_date = release_date;
        this.vote_average = vote_average;
        this.vote_count = vote_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovie_id() {
        return movie_id;
    }

    public void setMovie_id(String movie_id) {
        this.movie_id = movie_id;
    }

    public String getTitle() {
        return original_title;
    }

    public void setTitle(String original_title) {
        this.original_title = original_title;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRelease_date() {
        return this.release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getVote_average() {
        return this.vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getVote_count() {
        return this.vote_count;
    }

    public void setVote_count(String vote_count) {
        this.vote_count = vote_count;
    }

    public int describeContents() {
        return 0;
    }

    private MovieData(Parcel in){
        movie_id = in.readString();
        original_title = in.readString();
        poster_path = in.readString();
        overview = in.readString();
        release_date = in.readString();
        vote_average = in.readString();
        vote_count = in.readString();
    }

    public void writeToParcel(Parcel out,int flags){
        out.writeString(movie_id);
        out.writeString(original_title);
        out.writeString(poster_path);
        out.writeString(overview);
        out.writeString(release_date);
        out.writeString(vote_average);
        out.writeString(vote_count);
    }

    public static final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>() {
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

}

