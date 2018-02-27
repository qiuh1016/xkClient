package com.cetcme.xkclient.utils;

import java.io.Serializable;

public class GpsPosition implements Serializable {

	private static final long serialVersionUID = 1L;

	public double longitude;

	public double latitude;

	public GpsPosition() {
	}

	public GpsPosition(double longitude, double latitude) {
		this.longitude = longitude;
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
}
