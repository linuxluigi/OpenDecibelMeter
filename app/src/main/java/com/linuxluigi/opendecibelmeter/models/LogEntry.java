package com.linuxluigi.opendecibelmeter.models;

import java.util.Date;
import java.util.Locale;

public class LogEntry {

    private int id;
    private Double decibel, latitude, longitude;
    private Date timestamp;

    public LogEntry(int id, Double decibel, Double latitude, Double longitude, Date timestamp) {
        this.id = id;
        this.decibel = decibel;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Double getDecibel() {
        return decibel;
    }

    public void setDecibel(Double decibel) {
        this.decibel = decibel;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getPosition() {
        return String.format(Locale.GERMANY,"%f %f", this.latitude, this.longitude);
    }

    @Override
    public String toString() {
        return "LogEntry{" +
                "id=" + id +
                ", decibel=" + decibel +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", timestamp=" + timestamp +
                '}';
    }
}
