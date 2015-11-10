package com.example.nitu.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitus on 10/6/2015.
 */
public class MovieData implements Parcelable, Serializable {
    private static final long serialVersionUID = 1L;
    public String title;
    public Long id;
    public String original_title;
    public String overview;
    public String release_date;
    public String poster_path;
    public Double popularity;
    public Double vote_average;
    public Integer vote_count;

    public MovieData() {
    }

    public MovieData(Parcel in) {
        title = in.readString();
        id = (long) in.readDouble();
        original_title = in.readString();
        overview = in.readString();
        release_date = in.readString();
        poster_path = in.readString();
        popularity = in.readDouble();
        vote_average = in.readDouble();
        vote_count = in.readInt();
    }


    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeDouble(id);
        dest.writeString(original_title);
        dest.writeString(overview);
        dest.writeString(release_date);
        dest.writeString(poster_path);
        dest.writeDouble(popularity);
        dest.writeDouble(vote_average);
        dest.writeInt(vote_count);
    }

    public static final Parcelable.Creator<MovieData> CREATOR = new Parcelable.Creator<MovieData>() {
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };

    @Override
    public String toString() {
        return "MovieData{" + "trailer_title='" + title + "'";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieData that = (MovieData) o;

        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (original_title != null ? !original_title.equals(that.original_title) : that.original_title != null)
            return false;
        if (overview != null ? !overview.equals(that.overview) : that.overview != null)
            return false;
        if (release_date != null ? !release_date.equals(that.release_date) : that.release_date != null)
            return false;
        if (poster_path != null ? !poster_path.equals(that.poster_path) : that.poster_path != null)
            return false;
        if (popularity != null ? !popularity.equals(that.popularity) : that.popularity != null)
            return false;
        if (vote_average != null ? !vote_average.equals(that.vote_average) : that.vote_average != null)
            return false;
        return !(vote_count != null ? !vote_count.equals(that.vote_count) : that.vote_count != null);

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (original_title != null ? original_title.hashCode() : 0);
        result = 31 * result + (overview != null ? overview.hashCode() : 0);
        result = 31 * result + (release_date != null ? release_date.hashCode() : 0);
        result = 31 * result + (poster_path != null ? poster_path.hashCode() : 0);
        result = 31 * result + (popularity != null ? popularity.hashCode() : 0);
        result = 31 * result + (vote_average != null ? vote_average.hashCode() : 0);
        result = 31 * result + (vote_count != null ? vote_count.hashCode() : 0);
        return result;
    }
}

