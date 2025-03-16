package com.schoolbus.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.schoolbus.app.R;
import com.schoolbus.app.models.Bus;

import java.util.List;

public class BusAdapter extends RecyclerView.Adapter<BusAdapter.BusViewHolder> {
    private final Context context;
    private final List<Bus> busList;
    private final OnBusClickListener listener;

    public interface OnBusClickListener {
        void onBusClick(Bus bus);
    }

    public BusAdapter(List<Bus> busList, OnBusClickListener listener) {
        this.busList = busList;
        this.listener = listener;
        this.context = null;
    }
    
    public BusAdapter(Context context, List<Bus> busList, OnBusClickListener listener) {
        this.context = context;
        this.busList = busList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public BusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_bus, parent, false);
        return new BusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BusViewHolder holder, int position) {
        Bus bus = busList.get(position);
        holder.bind(bus, listener);
    }

    @Override
    public int getItemCount() {
        return busList.size();
    }

    static class BusViewHolder extends RecyclerView.ViewHolder {
        private final CardView cardView;
        private final TextView busNumberText;
        private final TextView driverNameText;
        private final TextView routeNameText;
        private final TextView statusText;
        private final MaterialButton trackButton;

        public BusViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.busCardView);
            busNumberText = itemView.findViewById(R.id.busNumberText);
            driverNameText = itemView.findViewById(R.id.driverNameText);
            routeNameText = itemView.findViewById(R.id.routeNameText);
            statusText = itemView.findViewById(R.id.statusText);
            trackButton = itemView.findViewById(R.id.trackButton);
        }

        public void bind(Bus bus, OnBusClickListener listener) {
            busNumberText.setText(bus.getBusNumber());
            driverNameText.setText(bus.getDriverName());
            routeNameText.setText(bus.getRouteName());
            statusText.setText(bus.getStatus());
            
            // Set status text color based on status
            int statusColor;
            switch (bus.getStatus()) {
                case "active":
                    statusColor = itemView.getContext().getResources().getColor(R.color.status_active);
                    break;
                case "inactive":
                    statusColor = itemView.getContext().getResources().getColor(R.color.status_inactive);
                    break;
                case "maintenance":
                    statusColor = itemView.getContext().getResources().getColor(R.color.status_warning);
                    break;
                default:
                    statusColor = itemView.getContext().getResources().getColor(R.color.text_secondary);
                    break;
            }
            statusText.setTextColor(statusColor);
            
            // Set click listeners
            cardView.setOnClickListener(v -> listener.onBusClick(bus));
            trackButton.setOnClickListener(v -> listener.onBusClick(bus));
        }
    }
} 