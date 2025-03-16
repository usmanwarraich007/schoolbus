package com.schoolbus.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.schoolbus.app.R;
import com.schoolbus.app.models.Route;

import java.util.List;

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.RouteViewHolder> {
    
    private final Context context;
    private List<Route> routes;
    private final OnRouteClickListener listener;
    
    public interface OnRouteClickListener {
        void onRouteClick(Route route);
    }
    
    public RouteAdapter(Context context, List<Route> routes, OnRouteClickListener listener) {
        this.context = context;
        this.routes = routes;
        this.listener = listener;
    }
    
    public void updateRoutes(List<Route> routes) {
        this.routes = routes;
        notifyDataSetChanged();
    }
    
    @NonNull
    @Override
    public RouteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_route, parent, false);
        return new RouteViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull RouteViewHolder holder, int position) {
        Route route = routes.get(position);
        
        holder.routeNameTextView.setText(route.getName());
        
        // Set description
        if (route.getDescription() != null && !route.getDescription().isEmpty()) {
            holder.routeDescriptionTextView.setText(route.getDescription());
            holder.routeDescriptionTextView.setVisibility(View.VISIBLE);
        } else {
            holder.routeDescriptionTextView.setVisibility(View.GONE);
        }
        
        // Set schedule info
        StringBuilder scheduleInfo = new StringBuilder();
        if (route.getStartTime() != null && route.getEndTime() != null) {
            scheduleInfo.append(route.getStartTime()).append(" - ").append(route.getEndTime());
        }
        
        if (route.getDays() != null && !route.getDays().isEmpty()) {
            if (scheduleInfo.length() > 0) {
                scheduleInfo.append(" | ");
            }
            scheduleInfo.append(formatDays(route.getDays()));
        }
        
        if (scheduleInfo.length() > 0) {
            holder.routeScheduleTextView.setText(scheduleInfo.toString());
            holder.routeScheduleTextView.setVisibility(View.VISIBLE);
        } else {
            holder.routeScheduleTextView.setVisibility(View.GONE);
        }
        
        // Set stops count
        int stopsCount = route.getStops() != null ? route.getStops().size() : 0;
        holder.routeStopsTextView.setText(context.getResources().getQuantityString(
                R.plurals.stops_count, stopsCount, stopsCount));
        
        // Set click listener
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRouteClick(route);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return routes != null ? routes.size() : 0;
    }
    
    private String formatDays(List<String> days) {
        if (days == null || days.isEmpty()) {
            return "";
        }
        
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < days.size(); i++) {
            if (i > 0) {
                result.append(", ");
            }
            // Abbreviate day names to first 3 letters
            String day = days.get(i);
            if (day.length() > 3) {
                result.append(day.substring(0, 3));
            } else {
                result.append(day);
            }
        }
        return result.toString();
    }
    
    static class RouteViewHolder extends RecyclerView.ViewHolder {
        TextView routeNameTextView;
        TextView routeDescriptionTextView;
        TextView routeScheduleTextView;
        TextView routeStopsTextView;
        
        public RouteViewHolder(@NonNull View itemView) {
            super(itemView);
            routeNameTextView = itemView.findViewById(R.id.routeNameTextView);
            routeDescriptionTextView = itemView.findViewById(R.id.busNumberTextView);
            routeScheduleTextView = itemView.findViewById(R.id.timeTextView);
            routeStopsTextView = itemView.findViewById(R.id.stopsCountTextView);
        }
    }
} 