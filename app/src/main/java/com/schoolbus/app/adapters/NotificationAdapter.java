package com.schoolbus.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.schoolbus.app.R;
import com.schoolbus.app.models.Notification;
import com.schoolbus.app.utils.Constants;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    
    private final Context context;
    private List<Notification> notifications;
    private final OnNotificationClickListener listener;
    private final SimpleDateFormat dateFormat;
    
    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }
    
    public NotificationAdapter(Context context, List<Notification> notifications, OnNotificationClickListener listener) {
        this.context = context;
        this.notifications = notifications;
        this.listener = listener;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
    }
    
    public void updateNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        
        holder.titleTextView.setText(notification.getTitle());
        holder.messageTextView.setText(notification.getMessage());
        
        // Format and set timestamp
        if (notification.getTimestamp() > 0) {
            String formattedDate = dateFormat.format(new Date(notification.getTimestamp()));
            holder.timestampTextView.setText(formattedDate);
        } else {
            holder.timestampTextView.setText("");
        }
        
        // Set notification type indicator
        String type = notification.getType();
        if (type != null) {
            switch (type) {
                case Constants.NOTIFICATION_TYPE_DELAY:
                    holder.typeIndicator.setBackgroundResource(R.drawable.bg_notification_delay);
                    break;
                case Constants.NOTIFICATION_TYPE_ROUTE_CHANGE:
                    holder.typeIndicator.setBackgroundResource(R.drawable.bg_notification_route_change);
                    break;
                case Constants.NOTIFICATION_TYPE_EMERGENCY:
                    holder.typeIndicator.setBackgroundResource(R.drawable.bg_notification_emergency);
                    break;
                default:
                    holder.typeIndicator.setBackgroundResource(R.drawable.bg_notification_general);
                    break;
            }
        } else {
            holder.typeIndicator.setBackgroundResource(R.drawable.bg_notification_general);
        }
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onNotificationClick(notification);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return notifications != null ? notifications.size() : 0;
    }
    
    static class NotificationViewHolder extends RecyclerView.ViewHolder {
        View typeIndicator;
        TextView titleTextView;
        TextView messageTextView;
        TextView timestampTextView;
        
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            typeIndicator = itemView.findViewById(R.id.notificationIconImageView);
            titleTextView = itemView.findViewById(R.id.notificationTitleTextView);
            messageTextView = itemView.findViewById(R.id.notificationMessageTextView);
            timestampTextView = itemView.findViewById(R.id.notificationTimeTextView);
        }
    }
} 