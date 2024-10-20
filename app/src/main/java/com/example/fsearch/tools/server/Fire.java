package com.example.fsearch.tools.server;

import java.util.Date;

public class Fire {
    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    public Fire(Integer id, Integer droneId, Integer fireRate, Double latitude, Double longtitude, Date date, byte[] photo) {
        super();
        this.id = id;
        this.fireRate = fireRate;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.date = date;
        this.droneId = droneId;
        this.photo = photo;
    }

    public Integer getDroneId() {
        return droneId;
    }

    public void setDroneId(Integer droneId) {
        this.droneId = droneId;
    }

    private Integer id, fireRate, droneId;
    private Double latitude, longtitude;
    private Date date;
    private byte[] photo;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double altitude) {
        this.latitude = altitude;
    }

    public Integer getFireRate() {
        return fireRate;
    }

    public void setFireRate(Integer fireRate) {
        this.fireRate = fireRate;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
