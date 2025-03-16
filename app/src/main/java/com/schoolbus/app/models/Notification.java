package com.schoolbus.app.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class Notification {
    private String id;
    private String title;
    private String message;
    private String type;
    private String busId;
    private String routeId;
    private long timestamp;
    private Map<String, Boolean> recipients;
    private boolean read;

    // Required empty constructor for Firebase
    public Notification() {
        recipients = new HashMap<>();
        read = false;
    }

    public Notification(String id, String title, String message, String type, String busId, String routeId, long timestamp) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.type = type;
        this.busId = busId;
        this.routeId = routeId;
        this.timestamp = timestamp;
        this.recipients = new HashMap<>();
        this.read = false;
    }

    @Exclude
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBusId() {
        return busId;
    }

    public void setBusId(String busId) {
        this.busId = busId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, Boolean> getRecipients() {
        return recipients;
    }

    public void setRecipients(Map<String, Boolean> recipients) {
        this.recipients = recipients;
    }

    public void addRecipient(String userId) {
        if (recipients == null) {
            recipients = new HashMap<>();
        }
        recipients.put(userId, true);
    }

    public void removeRecipient(String userId) {
        if (recipients != null) {
            recipients.remove(userId);
        }
    }
    
    @Exclude
    public boolean isRead() {
        return read;
    }
    
    @Exclude
    public void setRead(boolean read) {
        this.read = read;
    }
} 