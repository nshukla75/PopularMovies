package com.example.nitu.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by nitus on 10/6/2015.
 */
public class MovieData implements Parcelable {
    long movieId;
    String title;
    String overview;
    String releaseDate;
    String posterPath;
    String voteCount;
    String movieLength;
    String voteAverage;
    ArrayList<TrailerData> trailersList;

    public MovieData() {
    }

    public MovieData(Parcel in) {
        setMovieId(in.readLong());
        setTitle(in.readString());
        setOverview(in.readString());
        setReleaseDate(in.readString());
        setPosterPath(in.readString());
        setVoteCount(in.readString());
        setMovieLength(in.readString());
        setVoteAverage(in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(getMovieId());
        dest.writeString(getTitle());
        dest.writeString(getOverview());
        dest.writeString(getReleaseDate());
        dest.writeString(getPosterPath());
        dest.writeString(getVoteCount());
        dest.writeString(getMovieLength());
        dest.writeString(getVoteAverage());
    }

    public static final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>() {
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    public long getMovieId() {
        return movieId;
    }

    public void setMovieId(long movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(String voteCount) {
        this.voteCount = voteCount;
    }

    public String getMovieLength() {
        return movieLength;
    }

    public void setMovieLength(String movieLength) {
        this.movieLength = movieLength;
    }

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public ArrayList<TrailerData> getTrailersList() {
        return trailersList;
    }

    public void setTrailersList(ArrayList<TrailerData> trailersList) {
        this.trailersList = trailersList;
    }

}

