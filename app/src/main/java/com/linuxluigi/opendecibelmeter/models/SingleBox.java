package com.linuxluigi.opendecibelmeter.models;

public class SingleBox {
    private String boxName;
    private String boxId;
    private String sensorId;
    private double latitude;
    private double longitude;
    private double decibel;

    public SingleBox(String name, String boxId, String sensorId) {
        this.boxName = name;
        this.boxId = boxId;
        this.sensorId = sensorId;
    }

    public SingleBox(String boxName, String boxId, String sensorId, double decibel, double latitude, double longitude) {
        this.boxName = boxName;
        this.boxId = boxId;
        this.sensorId = sensorId;
        this.decibel = decibel;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getBoxName() {
        return boxName;
    }

    public void setBoxName(String boxName) {
        this.boxName = boxName;
    }

    public String getBoxId() {
        return boxId;
    }

    public void setBoxId(String boxId) {
        this.boxId = boxId;
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getDecibel() {
        return decibel;
    }

    public void setDecibel(double decibel) {
        this.decibel = decibel;
    }

    @Override
    public String toString() {
        return "SingleBox{" +
                "boxName='" + boxName + '\'' +
                ", boxId='" + boxId + '\'' +
                ", sensorId='" + sensorId + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", decibel=" + decibel +
                '}';
    }
}
