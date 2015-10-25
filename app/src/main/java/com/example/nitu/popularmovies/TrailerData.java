package com.example.nitu.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nitus on 10/6/2015.
 */
public class TrailerData implements Parcelable {
    private int id;
    private String trailerId;
    private String trailerKey;
    private String trailerName;
    private String trailerSite;
    private String trailerSize;

    public TrailerData(){}
    public TrailerData(String trailerId,
                       String trailerKey,
                       String trailerName,
                       String trailerSite,
                       String trailerSize){
        this.trailerId = trailerId;
        this.trailerKey = trailerKey;
        this.trailerName = trailerName;
        this.trailerSite = trailerSite;
        this.trailerSize = trailerSize;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTrailerId() {
        return trailerId;
    }

    public void setTrailerId(String trailerId) {
        this.trailerId = trailerId;
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

    public String getTrailerSite() {
        return trailerSite;
    }

    public void setTrailerSite(String trailerSite) {
        this.trailerSite = trailerSite;
    }

    public String getTrailerSize() {
        return this.trailerSize;
    }

    public void setTrailerSize(String trailerSize) {
        this.trailerSize = trailerSize;
    }

    public int describeContents() {
        return 0;
    }

    private TrailerData(Parcel in){
        trailerId = in.readString();
        trailerKey = in.readString();
        trailerName = in.readString();
        trailerSite = in.readString();
        trailerSize = in.readString();
    }

    public void writeToParcel(Parcel out,int flags){
        out.writeString(trailerId);
        out.writeString(trailerKey);
        out.writeString(trailerName);
        out.writeString(trailerSite);
        out.writeString(trailerSize);
    }

    public static final Creator<TrailerData> CREATOR = new Creator<TrailerData>() {
        public TrailerData createFromParcel(Parcel in) {
            return new TrailerData(in);
        }

        public TrailerData[] newArray(int size) {
            return new TrailerData[size];
        }
    };
}

