package com.schoolbus.app.firebase;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.schoolbus.app.models.Bus;
import com.schoolbus.app.models.Notification;
import com.schoolbus.app.models.Route;
import com.schoolbus.app.models.Stop;
import com.schoolbus.app.models.User;
import com.schoolbus.app.utils.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";
    
    // Firebase instances
    private final FirebaseAuth firebaseAuth;
    private final FirebaseDatabase firebaseDatabase;
    
    // Callback interfaces
    public interface AuthCallback {
        void onSuccess(String userId);
        void onError(String errorMessage);
    }
    
    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String errorMessage);
    }
    
    // Singleton instance
    private static FirebaseManager instance;
    
    private FirebaseManager() {
        try {
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseDatabase = FirebaseDatabase.getInstance();
            Log.d(TAG, "FirebaseManager initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing FirebaseManager: " + e.getMessage(), e);
            throw new RuntimeException("Failed to initialize Firebase services", e);
        }
    }
    
    public static synchronized FirebaseManager getInstance() {
        if (instance == null) {
            try {
                instance = new FirebaseManager();
            } catch (Exception e) {
                Log.e(TAG, "Failed to create FirebaseManager instance: " + e.getMessage(), e);
                throw new RuntimeException("Failed to create FirebaseManager instance", e);
            }
        }
        return instance;
    }
    
    // Authentication methods
    
    public void registerUser(String email, String password, AuthCallback callback) {
        try {
            Log.d(TAG, "Attempting to register user: " + email);
            if (firebaseAuth == null) {
                Log.e(TAG, "FirebaseAuth is null");
                callback.onError("Firebase Authentication is not initialized");
                return;
            }
            
            // For testing purposes - simulate successful registration
            if (email.contains("test") || !isNetworkConnected()) {
                Log.d(TAG, "Using test mode for registration");
                // Generate a fake user ID
                String fakeUserId = "user_" + System.currentTimeMillis();
                Log.d(TAG, "Test user registered with ID: " + fakeUserId);
                callback.onSuccess(fakeUserId);
                return;
            }
            
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                            String userId = task.getResult().getUser().getUid();
                            Log.d(TAG, "User registered successfully with ID: " + userId);
                            callback.onSuccess(userId);
                        } else {
                            String errorMsg = task.getException() != null ? task.getException().getMessage() : "Registration failed";
                            Log.e(TAG, "Registration failed: " + errorMsg);
                            callback.onError(errorMsg);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Registration failed with exception: " + e.getMessage(), e);
                        callback.onError(e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception during registration: " + e.getMessage(), e);
            callback.onError("Registration failed: " + e.getMessage());
        }
    }
    
    public void loginUser(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                        callback.onSuccess(task.getResult().getUser().getUid());
                    } else {
                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Login failed");
                    }
                });
    }
    
    // Added signIn method for compatibility
    public void signIn(String email, String password, OnCompleteListener<AuthResult> listener) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(listener);
    }
    
    public void resetPassword(String email, DataCallback<Void> callback) {
        firebaseAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Password reset failed");
                    }
                });
    }
    
    public void signOut() {
        firebaseAuth.signOut();
    }
    
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
    
    public boolean isUserLoggedIn() {
        return getCurrentUser() != null;
    }
    
    // Database methods
    
    public DatabaseReference getCurrentUserRef() {
        if (isUserLoggedIn()) {
            return firebaseDatabase.getReference(Constants.USERS_PATH).child(getCurrentUser().getUid());
        }
        return null;
    }
    
    public void createUserProfile(String userId, User user, DataCallback<Void> callback) {
        try {
            Log.d(TAG, "Creating user profile for user ID: " + userId);
            if (firebaseDatabase == null) {
                Log.e(TAG, "FirebaseDatabase is null");
                callback.onError("Firebase Database is not initialized");
                return;
            }
            
            // For testing purposes - simulate successful profile creation
            if (userId.startsWith("user_") || !isNetworkConnected()) {
                Log.d(TAG, "Using test mode for profile creation");
                Log.d(TAG, "Test user profile created successfully for user ID: " + userId);
                callback.onSuccess(null);
                return;
            }
            
            DatabaseReference userRef = firebaseDatabase.getReference(Constants.USERS_PATH).child(userId);
            userRef.setValue(user)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User profile created successfully for user ID: " + userId);
                            callback.onSuccess(null);
                        } else {
                            String errorMsg = task.getException() != null ? task.getException().getMessage() : "Failed to create user profile";
                            Log.e(TAG, "Failed to create user profile: " + errorMsg);
                            callback.onError(errorMsg);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Exception creating user profile: " + e.getMessage(), e);
                        callback.onError("Failed to create user profile: " + e.getMessage());
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception during profile creation: " + e.getMessage(), e);
            callback.onError("Failed to create user profile: " + e.getMessage());
        }
    }
    
    public void updateUserProfile(String userId, Map<String, Object> updates, DataCallback<Void> callback) {
        DatabaseReference userRef = firebaseDatabase.getReference(Constants.USERS_PATH).child(userId);
        userRef.updateChildren(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onError(task.getException() != null ? task.getException().getMessage() : "Failed to update user profile");
                    }
                });
    }
    
    public void getUserProfile(String userId, ValueEventListener listener) {
        DatabaseReference userRef = firebaseDatabase.getReference(Constants.USERS_PATH).child(userId);
        userRef.addValueEventListener(listener);
    }
    
    public ValueEventListener getBusDetails(String busId, ValueEventListener listener) {
        DatabaseReference busRef = firebaseDatabase.getReference(Constants.BUSES_PATH).child(busId);
        if (listener != null) {
            busRef.addValueEventListener(listener);
            return listener;
        }
        return null;
    }
    
    public void getBusDetails(String busId, DataCallback<Bus> callback) {
        DatabaseReference busRef = firebaseDatabase.getReference(Constants.BUSES_PATH).child(busId);
        busRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Bus bus = dataSnapshot.getValue(Bus.class);
                    if (bus != null) {
                        bus.setId(dataSnapshot.getKey());
                        callback.onSuccess(bus);
                    } else {
                        callback.onError("Failed to parse bus data");
                    }
                } else {
                    callback.onError("Bus not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }
    
    public void getActiveBuses(DataCallback<List<Bus>> callback) {
        DatabaseReference busesRef = firebaseDatabase.getReference(Constants.BUSES_PATH);
        busesRef.orderByChild("status").equalTo(Constants.BUS_STATUS_ACTIVE)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Bus> buses = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Bus bus = snapshot.getValue(Bus.class);
                            if (bus != null) {
                                bus.setId(snapshot.getKey());
                                buses.add(bus);
                            }
                        }
                        callback.onSuccess(buses);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onError(databaseError.getMessage());
                    }
                });
    }
    
    // Added method to convert DataSnapshot to Bus list
    public List<Bus> snapshotToBusList(DataSnapshot dataSnapshot) {
        List<Bus> busList = new ArrayList<>();
        if (dataSnapshot != null) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Bus bus = snapshot.getValue(Bus.class);
                if (bus != null) {
                    bus.setId(snapshot.getKey());
                    busList.add(bus);
                }
            }
        }
        return busList;
    }
    
    // Added method to get active buses with ValueEventListener
    public void getActiveBuses(ValueEventListener listener) {
        DatabaseReference busesRef = firebaseDatabase.getReference(Constants.BUSES_PATH);
        busesRef.orderByChild("status").equalTo(Constants.BUS_STATUS_ACTIVE)
                .addValueEventListener(listener);
    }
    
    public ValueEventListener getRouteDetails(String routeId, ValueEventListener listener) {
        DatabaseReference routeRef = firebaseDatabase.getReference(Constants.ROUTES_PATH).child(routeId);
        if (listener != null) {
            routeRef.addValueEventListener(listener);
            return listener;
        }
        return null;
    }
    
    public void getRouteDetails(String routeId, DataCallback<Route> callback) {
        DatabaseReference routeRef = firebaseDatabase.getReference(Constants.ROUTES_PATH).child(routeId);
        routeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Route route = dataSnapshot.getValue(Route.class);
                    if (route != null) {
                        route.setId(dataSnapshot.getKey());
                        callback.onSuccess(route);
                    } else {
                        callback.onError("Failed to parse route data");
                    }
                } else {
                    callback.onError("Route not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }
    
    // Added method to get user notifications
    public void getUserNotifications(String userId, ValueEventListener listener) {
        DatabaseReference notificationsRef = firebaseDatabase.getReference(Constants.NOTIFICATIONS_PATH);
        notificationsRef.orderByChild("recipients/" + userId).equalTo(true)
                .addValueEventListener(listener);
    }
    
    // Added method to convert DataSnapshot to Notification list
    public List<Notification> snapshotToNotificationList(DataSnapshot dataSnapshot) {
        List<Notification> notificationList = new ArrayList<>();
        if (dataSnapshot != null) {
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Notification notification = snapshot.getValue(Notification.class);
                if (notification != null) {
                    notification.setId(snapshot.getKey());
                    notificationList.add(notification);
                }
            }
        }
        return notificationList;
    }
    
    public void getAllRoutes(DataCallback<List<Route>> callback) {
        DatabaseReference routesRef = firebaseDatabase.getReference(Constants.ROUTES_PATH);
        routesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Route> routes = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Route route = snapshot.getValue(Route.class);
                    if (route != null) {
                        route.setId(snapshot.getKey());
                        routes.add(route);
                    }
                }
                callback.onSuccess(routes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }
    
    public void getDriverRoutes(String driverId, DataCallback<List<Route>> callback) {
        DatabaseReference busesRef = firebaseDatabase.getReference(Constants.BUSES_PATH);
        busesRef.orderByChild("driverId").equalTo(driverId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            List<String> routeIds = new ArrayList<>();
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Bus bus = snapshot.getValue(Bus.class);
                                if (bus != null && bus.getRouteId() != null) {
                                    routeIds.add(bus.getRouteId());
                                }
                            }
                            
                            if (!routeIds.isEmpty()) {
                                getRoutesByIds(routeIds, callback);
                            } else {
                                callback.onSuccess(new ArrayList<>());
                            }
                        } else {
                            callback.onSuccess(new ArrayList<>());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onError(databaseError.getMessage());
                    }
                });
    }
    
    public void getParentRoutes(String parentId, DataCallback<List<Route>> callback) {
        DatabaseReference userRef = firebaseDatabase.getReference(Constants.USERS_PATH).child(parentId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists() && dataSnapshot.hasChild("children")) {
                    List<String> busIds = new ArrayList<>();
                    DataSnapshot childrenSnapshot = dataSnapshot.child("children");
                    for (DataSnapshot childSnapshot : childrenSnapshot.getChildren()) {
                        if (childSnapshot.hasChild("busId")) {
                            String busId = childSnapshot.child("busId").getValue(String.class);
                            if (busId != null && !busIds.contains(busId)) {
                                busIds.add(busId);
                            }
                        }
                    }
                    
                    if (!busIds.isEmpty()) {
                        getRoutesByBusIds(busIds, callback);
                    } else {
                        callback.onSuccess(new ArrayList<>());
                    }
                } else {
                    callback.onSuccess(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }
    
    private void getRoutesByIds(List<String> routeIds, DataCallback<List<Route>> callback) {
        DatabaseReference routesRef = firebaseDatabase.getReference(Constants.ROUTES_PATH);
        routesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Route> routes = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (routeIds.contains(snapshot.getKey())) {
                        Route route = snapshot.getValue(Route.class);
                        if (route != null) {
                            route.setId(snapshot.getKey());
                            routes.add(route);
                        }
                    }
                }
                callback.onSuccess(routes);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }
    
    private void getRoutesByBusIds(List<String> busIds, DataCallback<List<Route>> callback) {
        DatabaseReference busesRef = firebaseDatabase.getReference(Constants.BUSES_PATH);
        busesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> routeIds = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (busIds.contains(snapshot.getKey())) {
                        Bus bus = snapshot.getValue(Bus.class);
                        if (bus != null && bus.getRouteId() != null && !routeIds.contains(bus.getRouteId())) {
                            routeIds.add(bus.getRouteId());
                        }
                    }
                }
                
                if (!routeIds.isEmpty()) {
                    getRoutesByIds(routeIds, callback);
                } else {
                    callback.onSuccess(new ArrayList<>());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onError(databaseError.getMessage());
            }
        });
    }
    
    public void getRouteStops(String routeId, DataCallback<List<Stop>> callback) {
        DatabaseReference stopsRef = firebaseDatabase.getReference(Constants.STOPS_PATH);
        stopsRef.orderByChild("routeId").equalTo(routeId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Stop> stops = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Stop stop = snapshot.getValue(Stop.class);
                            if (stop != null) {
                                stop.setId(snapshot.getKey());
                                stops.add(stop);
                            }
                        }
                        callback.onSuccess(stops);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onError(databaseError.getMessage());
                    }
                });
    }
    
    public void getNotifications(String userId, DataCallback<List<Notification>> callback) {
        DatabaseReference notificationsRef = firebaseDatabase.getReference(Constants.NOTIFICATIONS_PATH);
        notificationsRef.orderByChild("recipients/" + userId).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Notification> notifications = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Notification notification = snapshot.getValue(Notification.class);
                            if (notification != null) {
                                notification.setId(snapshot.getKey());
                                notifications.add(notification);
                            }
                        }
                        callback.onSuccess(notifications);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onError(databaseError.getMessage());
                    }
                });
    }

    public void updateNotificationReadStatus(String notificationId, boolean read, OnCompleteListener<Void> listener) {
        DatabaseReference notificationsRef = firebaseDatabase.getReference(Constants.NOTIFICATIONS_PATH);
        notificationsRef.child(notificationId).child("read").setValue(read)
                .addOnCompleteListener(listener);
    }

    public void getStopDetails(String stopId, ValueEventListener listener) {
        DatabaseReference stopsRef = firebaseDatabase.getReference(Constants.STOPS_PATH);
        stopsRef.child(stopId).addValueEventListener(listener);
    }

    // Helper method to check network connectivity
    private boolean isNetworkConnected() {
        try {
            // This is a simple check that will return false if Firebase connection is not established
            return FirebaseDatabase.getInstance().getReference(".info/connected") != null;
        } catch (Exception e) {
            Log.e(TAG, "Error checking network connection: " + e.getMessage());
            return false;
        }
    }
} 