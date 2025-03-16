package com.schoolbus.app.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Stop {
    private String id;
    private String name;
    private int order;
    private String scheduledTime;
    private double latitude;
    private double longitude;
    private String routeId;
    private String address;
    private String arrivalTime;
    private String departureTime;

    // Required empty constructor for Firebase
    public Stop() {
    }

    public Stop(String id, String name, int order, String scheduledTime, double latitude, double longitude, String routeId) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.scheduledTime = scheduledTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.routeId = routeId;
    }

    public Stop(String id, String name, int order, String scheduledTime, double latitude, double longitude, String routeId, String address, String arrivalTime, String departureTime) {
        this.id = id;
        this.name = name;
        this.order = order;
        this.scheduledTime = scheduledTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.routeId = routeId;
        this.address = address;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(String scheduledTime) {
        this.scheduledTime = scheduledTime;
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

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }
    
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(String arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }
} 