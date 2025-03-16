package com.schoolbus.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.schoolbus.app.R;
import com.schoolbus.app.models.Route;

import java.util.List;

public class RouteStopAdapter extends RecyclerView.Adapter<RouteStopAdapter.StopViewHolder> {
    private List<Route.Stop> stops;

    public RouteStopAdapter(List<Route.Stop> stops) {
        this.stops = stops;
    }

    public void updateStops(List<Route.Stop> stops) {
        this.stops = stops;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_route_stop, parent, false);
        return new StopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StopViewHolder holder, int position) {
        Route.Stop stop = stops.get(position);
        holder.bind(stop, position + 1);
    }

    @Override
    public int getItemCount() {
        return stops.size();
    }

    static class StopViewHolder extends RecyclerView.ViewHolder {
        private final TextView stopNumberText;
        private final TextView stopNameText;
        private final TextView stopAddressText;
        private final TextView arrivalTimeText;
        private final TextView departureTimeText;

        public StopViewHolder(@NonNull View itemView) {
            super(itemView);
            stopNumberText = itemView.findViewById(R.id.stopNumberText);
            stopNameText = itemView.findViewById(R.id.stopNameText);
            stopAddressText = itemView.findViewById(R.id.stopAddressText);
            arrivalTimeText = itemView.findViewById(R.id.arrivalTimeText);
            departureTimeText = itemView.findViewById(R.id.departureTimeText);
        }

        public void bind(Route.Stop stop, int stopNumber) {
            stopNumberText.setText(String.valueOf(stopNumber));
            stopNameText.setText(stop.getName());
            stopAddressText.setText(stop.getAddress());
            arrivalTimeText.setText(stop.getArrivalTime());
            departureTimeText.setText(stop.getDepartureTime());
        }
    }
} 