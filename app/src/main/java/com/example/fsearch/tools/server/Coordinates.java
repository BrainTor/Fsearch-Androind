package com.example.fsearch.tools.server;

import java.util.Date;

public class Coordinates {
	private Integer id, droneID;
	private Date date;
	private Double latitude, longtitude, altitude, speed;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getDroneID() {
		return droneID;
	}

	public void setDroneID(Integer droneID) {
		this.droneID = droneID;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(Double longtitude) {
		this.longtitude = longtitude;
	}

	public Double getAltitude() {
		return altitude;
	}

	public void setAltitude(Double altitude) {
		this.altitude = altitude;
	}

	public Double getSpeed() {
		return speed;
	}

	public void setSpeed(Double speed) {
		this.speed = speed;
	}

	public Coordinates(Integer id, Integer droneID, Date date, Double latitude, Double longtitude, Double altitude,
			Double speed) {
		super();
		this.id = id;
		this.droneID = droneID;
		this.date = date;
		this.latitude = latitude;
		this.longtitude = longtitude;
		this.altitude = altitude;
		this.speed = speed;
	}
}