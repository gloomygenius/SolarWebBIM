package edu.spbpu.models;

import lombok.Value;

import java.io.Serializable;

@Value
public class Position implements Serializable {
    private static final long serialVersionUID = 8603234177879853734L;
    final double latitude;
    final double longitude;
    final double altitude; //in meters

    public int getLatitudeIndex() {
        return (int) Math.round((latitude + 90) * 2);
    }
    public int getLongitudeIndex() {
        return (int) Math.round((longitude + 180) / 0.625);
    }
    public static int transformLatitude(double latitude){
        return (int) Math.round((latitude + 90) * 2);
    }
    public static int transformLongitude(double longitude){
        return (int) Math.round((longitude + 180) / 0.625);
    }
}