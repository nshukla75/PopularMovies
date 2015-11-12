package com.example.nitu.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by nitus on 10/6/2015.
 */
public class TrailerData  implements Parcelable, Serializable {
    public final String youtube_key; // Key
    public final String trailer_title;//name
    public final Long movie_key;// movie_Key Long
    public final String trailer_key;

    protected TrailerData(Parcel in) {
        youtube_key = in.readString();
        trailer_title = in.readString();
        movie_key = in.readLong();
        trailer_key=in.readString();
    }

    public TrailerData(String youtube_key, String title, Long movie_key, String trailer_key) {
        this.youtube_key = youtube_key;
        this.trailer_title = title;
        this.movie_key = movie_key;
        this.trailer_key = trailer_key;
    }

    public static final Parcelable.Creator<TrailerData> CREATOR = new Parcelable.Creator<TrailerData>() {
        @Override
        public TrailerData createFromParcel(Parcel in) {
            return new TrailerData(in);
        }

        @Override
        public TrailerData[] newArray(int size) {
            return new TrailerData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(youtube_key);
        dest.writeString(trailer_title);
        dest.writeLong(movie_key);
        dest.writeString(trailer_key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TrailerData that = (TrailerData) o;

        if (youtube_key != null ? !youtube_key.equals(that.youtube_key) : that.youtube_key != null)
            return false;
        if (trailer_title != null ? !trailer_title.equals(that.trailer_title) : that.trailer_title != null)
            return false;
        if (movie_key != null ? !movie_key.equals(that.movie_key) : that.movie_key != null)
            return false;
        return !(trailer_key != null ? !trailer_key.equals(that.trailer_key) : that.trailer_key != null);
    }

    @Override
    public int hashCode() {
        int result = youtube_key != null ? youtube_key.hashCode() : 0;
        result = 31 * result + (trailer_title != null ? trailer_title.hashCode() : 0);
        result = 31 * result + (movie_key != null ? movie_key.hashCode() : 0);
        result = 31 * result + (trailer_key != null ? trailer_key.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "TrailerListObj{" +
                "movie_key='" + movie_key + '\'' +
                ", youtube_key='" + youtube_key + '\'' +
                ", trailer_title='" + trailer_title + '\'' +
                ", trailer_key='" + trailer_title + '\'' +
                '}';
    }
}

