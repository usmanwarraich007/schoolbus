package com.schoolbus.app.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Route {
    private String id;
    private String name;
    private String description;
    private String schoolId;
    private String busId;
    private String startTime;
    private String endTime;
    private List<String> days;
    private Map<String, Boolean> stops;
    
    @Exclude
    private List<Stop> stopList;

    // Required empty constructor for Firebase
    public Route() {
        days = new ArrayList<>();
        stops = new HashMap<>();
        stopList = new ArrayList<>();
    }

    public Route(String id, String name, String description, String schoolId, String busId, 
                 String startTime, String endTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.schoolId = schoolId;
        this.busId = busId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.days = new ArrayList<>();
        this.stops = new HashMap<>();
        this.stopList = new ArrayList<>();
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public Map<String, Boolean> getStopsMap() {
        return stops;
    }

    public void setStops(Map<String, Boolean> stops) {
        this.stops = stops;
    }
    
    @Exclude
    public List<Stop> getStops() {
        return stopList;
    }
    
    @Exclude
    public void setStopList(List<Stop> stopList) {
        this.stopList = stopList;
    }
    
    @Exclude
    public void addStop(Stop stop) {
        if (stopList == null) {
            stopList = new ArrayList<>();
        }
        stopList.add(stop);
        
        // Also update the stops map
        if (stops == null) {
            stops = new HashMap<>();
        }
        stops.put(stop.getId(), true);
    }
    
    @IgnoreExtraProperties
    public static class Stop {
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
} 