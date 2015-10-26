package com.example.nitu.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nitus on 10/6/2015.
 */
public class TrailerData implements Parcelable {
    String trailerKey;
    String trailerName;

    public TrailerData() {

    }

    public TrailerData(Parcel in) {
        setTrailerKey(in.readString());
        setTrailerName(in.readString());
    }

    public static final Parcelable.Creator<TrailerData> CREATOR = new Parcelable.Creator<TrailerData>() {
        public TrailerData createFromParcel(Parcel in) {
            return new TrailerData(in);
        }

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
        dest.writeString(getTrailerKey());
        dest.writeString(getTrailerName());
    }

    public String getTrailerKey() {
        return trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }

    public String getTrailerName() {
        return trailerName;
    }

    public void setTrailerName(String trailerName) {
        this.trailerName = trailerName;
    }

}

