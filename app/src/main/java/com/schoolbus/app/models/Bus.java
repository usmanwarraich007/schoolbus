package com.schoolbus.app.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Bus {
    private String id;
    private String busNumber;
    private int capacity;
    private String driverId;
    private String driverName;
    private String routeId;
    private String routeName;
    private String status;
    private Location currentLocation;
    private Map<String, Stop> stops;

    // Required empty constructor for Firebase
    public Bus() {
    }

    public Bus(String id, String busNumber, int capacity, String driverId, String driverName, 
               String routeId, String routeName, String status) {
        this.id = id;
        this.busNumber = busNumber;
        this.capacity = capacity;
        this.driverId = driverId;
        this.driverName = driverName;
        this.routeId = routeId;
        this.routeName = routeName;
        this.status = status;
        this.stops = new HashMap<>();
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBusNumber() {
        return busNumber;
    }

    public void setBusNumber(String busNumber) {
        this.busNumber = busNumber;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }

    @Exclude
    public long getLastUpdated() {
        return currentLocation != null ? currentLocation.getLastUpdated() : 0;
    }

    public Map<String, Stop> getStops() {
        return stops;
    }

    public void setStops(Map<String, Stop> stops) {
        this.stops = stops;
    }

    // Inner class for location
    public static class Location {
        private double latitude;
        private double longitude;
        private long lastUpdated;

        // Required empty constructor for Firebase
        public Location() {
        }

        public Location(double latitude, double longitude, long lastUpdated) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.lastUpdated = lastUpdated;
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

        public long getLastUpdated() {
            return lastUpdated;
        }

        public void setLastUpdated(long lastUpdated) {
            this.lastUpdated = lastUpdated;
        }
    }

    // Inner class for bus stops
    public static class Stop {
        private String name;
        private int order;
        private String scheduledTime;
        private Location location;

        // Required empty constructor for Firebase
        public Stop() {
        }

        public Stop(String name, int order, String scheduledTime, Location location) {
            this.name = name;
            this.order = order;
            this.scheduledTime = scheduledTime;
            this.location = location;
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

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }
    }
} 