package com.schoolbus.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.schoolbus.app.R;
import com.schoolbus.app.models.Stop;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StopAdapter extends RecyclerView.Adapter<StopAdapter.StopViewHolder> {
    
    private final Context context;
    private List<Stop> stops;
    private final OnStopClickListener listener;
    
    public interface OnStopClickListener {
        void onStopClick(Stop stop);
        void onViewOnMapClick(Stop stop);
    }
    
    public StopAdapter(Context context, List<Stop> stops, OnStopClickListener listener) {
        this.context = context;
        this.stops = stops;
        this.listener = listener;
        sortStopsByOrder();
    }
    
    public void updateStops(List<Stop> stops) {
        this.stops = stops;
        sortStopsByOrder();
        notifyDataSetChanged();
    }
    
    private void sortStopsByOrder() {
        if (stops != null) {
            Collections.sort(stops, Comparator.comparingInt(Stop::getOrder));
        }
    }
    
    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stop, parent, false);
        return new StopViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull StopViewHolder holder, int position) {
        Stop stop = stops.get(position);
        
        // Set stop name
        holder.stopNameTextView.setText(stop.getName());
        
        // Set stop order number
        holder.stopOrderTextView.setText(String.valueOf(stop.getOrder()));
        
        // Set scheduled time
        if (stop.getScheduledTime() != null && !stop.getScheduledTime().isEmpty()) {
            holder.scheduledTimeTextView.setText(stop.getScheduledTime());
            holder.scheduledTimeTextView.setVisibility(View.VISIBLE);
        } else {
            holder.scheduledTimeTextView.setVisibility(View.GONE);
        }
        
        // Set click listeners
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onStopClick(stop);
            }
        });
        
        holder.viewOnMapButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onViewOnMapClick(stop);
            }
        });
    }
    
    @Override
    public int getItemCount() {
        return stops != null ? stops.size() : 0;
    }
    
    static class StopViewHolder extends RecyclerView.ViewHolder {
        TextView stopOrderTextView;
        TextView stopNameTextView;
        TextView scheduledTimeTextView;
        View viewOnMapButton;
        
        public StopViewHolder(@NonNull View itemView) {
            super(itemView);
            stopOrderTextView = itemView.findViewById(R.id.stopOrderTextView);
            stopNameTextView = itemView.findViewById(R.id.stopNameTextView);
            scheduledTimeTextView = itemView.findViewById(R.id.stopTimeTextView);
            viewOnMapButton = itemView.findViewById(R.id.viewOnMapButton);
        }
    }
} 