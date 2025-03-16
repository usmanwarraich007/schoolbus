package com.schoolbus.app.utils;

import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.schoolbus.app.models.Bus;
import com.schoolbus.app.models.Notification;
import com.schoolbus.app.models.Route;
import com.schoolbus.app.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to generate and add dummy data to Firebase Realtime Database
 */
public class DummyDataGenerator {
    private static final String TAG = "DummyDataGenerator";
    
    private FirebaseDatabase database;
    private DatabaseReference usersRef;
    private DatabaseReference busesRef;
    private DatabaseReference routesRef;
    private DatabaseReference stopsRef;
    private DatabaseReference notificationsRef;
    
    private String currentUserId;
    
    public interface DataGenerationCallback {
        void onSuccess();
        void onError(String errorMessage);
    }
    
    public DummyDataGenerator(String currentUserId) {
        this.currentUserId = currentUserId;
        database = FirebaseDatabase.getInstance();
        usersRef = database.getReference(Constants.USERS_PATH);
        busesRef = database.getReference(Constants.BUSES_PATH);
        routesRef = database.getReference(Constants.ROUTES_PATH);
        stopsRef = database.getReference(Constants.STOPS_PATH);
        notificationsRef = database.getReference(Constants.NOTIFICATIONS_PATH);
    }
    
    /**
     * Generate and add all dummy data to Firebase
     */
    public void generateAllData(DataGenerationCallback callback) {
        try {
            // Generate dummy drivers
            Map<String, User> drivers = generateDrivers();
            
            // Generate dummy buses
            Map<String, Bus> buses = generateBuses(drivers);
            
            // Generate dummy routes and stops
            Map<String, Route> routes = generateRoutes(buses);
            Map<String, Route.Stop> stops = generateStops(routes);
            
            // Generate dummy notifications
            Map<String, Notification> notifications = generateNotifications(buses, routes);
            
            // Add all data to Firebase
            addAllDataToFirebase(drivers, buses, routes, stops, notifications, callback);
        } catch (Exception e) {
            Log.e(TAG, "Error generating dummy data: " + e.getMessage(), e);
            callback.onError("Error generating dummy data: " + e.getMessage());
        }
    }
    
    /**
     * Generate dummy driver users
     */
    private Map<String, User> generateDrivers() {
        Map<String, User> drivers = new HashMap<>();
        
        // Driver 1
        User driver1 = new User();
        driver1.setId("driver1");
        driver1.setName("John Driver");
        driver1.setEmail("john.driver@example.com");
        driver1.setPhone("555-1234");
        driver1.setType(Constants.USER_TYPE_DRIVER);
        drivers.put(driver1.getId(), driver1);
        
        // Driver 2
        User driver2 = new User();
        driver2.setId("driver2");
        driver2.setName("Sarah Driver");
        driver2.setEmail("sarah.driver@example.com");
        driver2.setPhone("555-5678");
        driver2.setType(Constants.USER_TYPE_DRIVER);
        drivers.put(driver2.getId(), driver2);
        
        return drivers;
    }
    
    /**
     * Generate dummy buses
     */
    private Map<String, Bus> generateBuses(Map<String, User> drivers) {
        Map<String, Bus> buses = new HashMap<>();
        
        // Bus 1
        Bus bus1 = new Bus();
        bus1.setId("bus1");
        bus1.setBusNumber("SB-001");
        bus1.setCapacity(40);
        bus1.setDriverId("driver1");
        bus1.setDriverName(drivers.get("driver1").getName());
        bus1.setRouteId("route1");
        bus1.setRouteName("Morning Route");
        bus1.setStatus(Constants.BUS_STATUS_ACTIVE);
        
        // Set current location for Bus 1
        Bus.Location location1 = new Bus.Location();
        location1.setLatitude(37.7749);
        location1.setLongitude(-122.4194);
        location1.setLastUpdated(System.currentTimeMillis());
        bus1.setCurrentLocation(location1);
        
        buses.put(bus1.getId(), bus1);
        
        // Bus 2
        Bus bus2 = new Bus();
        bus2.setId("bus2");
        bus2.setBusNumber("SB-002");
        bus2.setCapacity(35);
        bus2.setDriverId("driver2");
        bus2.setDriverName(drivers.get("driver2").getName());
        bus2.setRouteId("route2");
        bus2.setRouteName("Afternoon Route");
        bus2.setStatus(Constants.BUS_STATUS_ACTIVE);
        
        // Set current location for Bus 2
        Bus.Location location2 = new Bus.Location();
        location2.setLatitude(37.3382);
        location2.setLongitude(-121.8863);
        location2.setLastUpdated(System.currentTimeMillis());
        bus2.setCurrentLocation(location2);
        
        buses.put(bus2.getId(), bus2);
        
        return buses;
    }
    
    /**
     * Generate dummy routes
     */
    private Map<String, Route> generateRoutes(Map<String, Bus> buses) {
        Map<String, Route> routes = new HashMap<>();
        
        // Route 1
        Route route1 = new Route();
        route1.setId("route1");
        route1.setName("Morning Route");
        route1.setDescription("Morning pickup route for Elementary School");
        route1.setSchoolId("school1");
        route1.setBusId("bus1");
        route1.setStartTime("07:30 AM");
        route1.setEndTime("08:30 AM");
        route1.setDays(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"));
        
        routes.put(route1.getId(), route1);
        
        // Route 2
        Route route2 = new Route();
        route2.setId("route2");
        route2.setName("Afternoon Route");
        route2.setDescription("Afternoon drop-off route for Elementary School");
        route2.setSchoolId("school1");
        route2.setBusId("bus2");
        route2.setStartTime("03:00 PM");
        route2.setEndTime("04:00 PM");
        route2.setDays(Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday"));
        
        routes.put(route2.getId(), route2);
        
        return routes;
    }
    
    /**
     * Generate dummy stops
     */
    private Map<String, Route.Stop> generateStops(Map<String, Route> routes) {
        Map<String, Route.Stop> stops = new HashMap<>();
        Map<String, Boolean> stopsMap1 = new HashMap<>();
        Map<String, Boolean> stopsMap2 = new HashMap<>();
        
        // Stops for Route 1
        Route.Stop stop1 = new Route.Stop();
        stop1.setId("stop1");
        stop1.setName("Main Street & 1st Avenue");
        stop1.setOrder(1);
        stop1.setScheduledTime("07:35 AM");
        stop1.setLatitude(37.7749);
        stop1.setLongitude(-122.4194);
        stop1.setRouteId("route1");
        stop1.setAddress("123 Main Street");
        stop1.setArrivalTime("07:35 AM");
        stop1.setDepartureTime("07:37 AM");
        stops.put(stop1.getId(), stop1);
        stopsMap1.put(stop1.getId(), true);
        
        Route.Stop stop2 = new Route.Stop();
        stop2.setId("stop2");
        stop2.setName("Oak Street & 5th Avenue");
        stop2.setOrder(2);
        stop2.setScheduledTime("07:45 AM");
        stop2.setLatitude(37.7746);
        stop2.setLongitude(-122.4172);
        stop2.setRouteId("route1");
        stop2.setAddress("456 Oak Street");
        stop2.setArrivalTime("07:45 AM");
        stop2.setDepartureTime("07:47 AM");
        stops.put(stop2.getId(), stop2);
        stopsMap1.put(stop2.getId(), true);
        
        Route.Stop stop3 = new Route.Stop();
        stop3.setId("stop3");
        stop3.setName("Pine Street & 10th Avenue");
        stop3.setOrder(3);
        stop3.setScheduledTime("07:55 AM");
        stop3.setLatitude(37.7742);
        stop3.setLongitude(-122.4150);
        stop3.setRouteId("route1");
        stop3.setAddress("789 Pine Street");
        stop3.setArrivalTime("07:55 AM");
        stop3.setDepartureTime("07:57 AM");
        stops.put(stop3.getId(), stop3);
        stopsMap1.put(stop3.getId(), true);
        
        // Stops for Route 2
        Route.Stop stop4 = new Route.Stop();
        stop4.setId("stop4");
        stop4.setName("School Entrance");
        stop4.setOrder(1);
        stop4.setScheduledTime("03:05 PM");
        stop4.setLatitude(37.3382);
        stop4.setLongitude(-121.8863);
        stop4.setRouteId("route2");
        stop4.setAddress("100 School Avenue");
        stop4.setArrivalTime("03:05 PM");
        stop4.setDepartureTime("03:10 PM");
        stops.put(stop4.getId(), stop4);
        stopsMap2.put(stop4.getId(), true);
        
        Route.Stop stop5 = new Route.Stop();
        stop5.setId("stop5");
        stop5.setName("Maple Street & 3rd Avenue");
        stop5.setOrder(2);
        stop5.setScheduledTime("03:20 PM");
        stop5.setLatitude(37.3375);
        stop5.setLongitude(-121.8850);
        stop5.setRouteId("route2");
        stop5.setAddress("321 Maple Street");
        stop5.setArrivalTime("03:20 PM");
        stop5.setDepartureTime("03:22 PM");
        stops.put(stop5.getId(), stop5);
        stopsMap2.put(stop5.getId(), true);
        
        Route.Stop stop6 = new Route.Stop();
        stop6.setId("stop6");
        stop6.setName("Cedar Street & 8th Avenue");
        stop6.setOrder(3);
        stop6.setScheduledTime("03:35 PM");
        stop6.setLatitude(37.3368);
        stop6.setLongitude(-121.8840);
        stop6.setRouteId("route2");
        stop6.setAddress("654 Cedar Street");
        stop6.setArrivalTime("03:35 PM");
        stop6.setDepartureTime("03:37 PM");
        stops.put(stop6.getId(), stop6);
        stopsMap2.put(stop6.getId(), true);
        
        // Update routes with stops
        routes.get("route1").setStops(stopsMap1);
        routes.get("route2").setStops(stopsMap2);
        
        return stops;
    }
    
    /**
     * Generate dummy notifications
     */
    private Map<String, Notification> generateNotifications(Map<String, Bus> buses, Map<String, Route> routes) {
        Map<String, Notification> notifications = new HashMap<>();
        
        // Notification 1 - Delay
        Notification notification1 = new Notification();
        notification1.setId("notification1");
        notification1.setTitle("Bus Delay");
        notification1.setMessage("Bus SB-001 is running 10 minutes late due to traffic.");
        notification1.setType(Constants.NOTIFICATION_TYPE_DELAY);
        notification1.setBusId("bus1");
        notification1.setRouteId("route1");
        notification1.setTimestamp(System.currentTimeMillis() - 3600000); // 1 hour ago
        notification1.addRecipient(currentUserId);
        notifications.put(notification1.getId(), notification1);
        
        // Notification 2 - Route Change
        Notification notification2 = new Notification();
        notification2.setId("notification2");
        notification2.setTitle("Route Change");
        notification2.setMessage("Bus SB-002 route has been modified due to road construction.");
        notification2.setType(Constants.NOTIFICATION_TYPE_ROUTE_CHANGE);
        notification2.setBusId("bus2");
        notification2.setRouteId("route2");
        notification2.setTimestamp(System.currentTimeMillis() - 7200000); // 2 hours ago
        notification2.addRecipient(currentUserId);
        notifications.put(notification2.getId(), notification2);
        
        // Notification 3 - General
        Notification notification3 = new Notification();
        notification3.setId("notification3");
        notification3.setTitle("School Announcement");
        notification3.setMessage("Early dismissal tomorrow at 1:30 PM.");
        notification3.setType(Constants.NOTIFICATION_TYPE_GENERAL);
        notification3.setTimestamp(System.currentTimeMillis() - 86400000); // 1 day ago
        notification3.addRecipient(currentUserId);
        notifications.put(notification3.getId(), notification3);
        
        return notifications;
    }
    
    /**
     * Add all generated data to Firebase
     */
    private void addAllDataToFirebase(
            Map<String, User> drivers,
            Map<String, Bus> buses,
            Map<String, Route> routes,
            Map<String, Route.Stop> stops,
            Map<String, Notification> notifications,
            DataGenerationCallback callback) {
        
        Map<String, Object> updates = new HashMap<>();
        
        // Add drivers to updates
        for (Map.Entry<String, User> entry : drivers.entrySet()) {
            updates.put("/" + Constants.USERS_PATH + "/" + entry.getKey(), entry.getValue());
        }
        
        // Add buses to updates
        for (Map.Entry<String, Bus> entry : buses.entrySet()) {
            updates.put("/" + Constants.BUSES_PATH + "/" + entry.getKey(), entry.getValue());
        }
        
        // Add routes to updates
        for (Map.Entry<String, Route> entry : routes.entrySet()) {
            updates.put("/" + Constants.ROUTES_PATH + "/" + entry.getKey(), entry.getValue());
        }
        
        // Add stops to updates
        for (Map.Entry<String, Route.Stop> entry : stops.entrySet()) {
            updates.put("/" + Constants.STOPS_PATH + "/" + entry.getKey(), entry.getValue());
        }
        
        // Add notifications to updates
        for (Map.Entry<String, Notification> entry : notifications.entrySet()) {
            updates.put("/" + Constants.NOTIFICATIONS_PATH + "/" + entry.getKey(), entry.getValue());
        }
        
        // Perform the update
        database.getReference().updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Dummy data added successfully");
                        callback.onSuccess();
                    } else {
                        Log.e(TAG, "Error adding dummy data: " + task.getException().getMessage());
                        callback.onError("Error adding dummy data: " + task.getException().getMessage());
                    }
                });
    }
} 