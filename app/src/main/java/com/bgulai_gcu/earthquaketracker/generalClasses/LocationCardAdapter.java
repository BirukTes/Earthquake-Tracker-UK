package com.bgulai_gcu.earthquaketracker.generalClasses;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bgulai_gcu.earthquaketracker.R;

import java.util.ArrayList;

/**
 * Created by Bereketab Gulai | s1827985
 */
public class LocationCardAdapter extends RecyclerView.Adapter<LocationCardAdapter.LocationHolder> {


    // Adapter class
    private Context context;
    private ArrayList<LocationModel> locationModelList;

    // Constructor
    public LocationCardAdapter(Context context, ArrayList<LocationModel> locationModelList) {
        this.context = context;
        this.locationModelList = locationModelList;
    }

    @NonNull
    @Override
    public LocationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.location_cardview, parent, false);

        return new LocationHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationHolder holder, int position) {
        LocationModel locationModel = locationModelList.get((position));
        holder.setDetails(locationModel);
    }

    @Override
    public int getItemCount() {
        return locationModelList.size();
    }


    class LocationHolder extends RecyclerView.ViewHolder {
        private TextView location, dateTime, magnitude, depth, colourBackgroundTextView;
        private Context locationViewContext;

        LocationHolder(View locationCardView) {
            super(locationCardView);

//            locationCardView.addli

            locationViewContext = locationCardView.getContext();

            location = locationCardView.findViewById(R.id.locationTextView);
            dateTime = locationCardView.findViewById(R.id.dateTimeTextView);
            magnitude = locationCardView.findViewById(R.id.magnitudeTextView);
            depth = locationCardView.findViewById(R.id.depthTextView);
            colourBackgroundTextView = locationCardView.findViewById(R.id.colourBackgroundTextView);
        }

        void setDetails(LocationModel locationModel) {
            location.setText(locationModel.getLocation().trim());
            dateTime.setText(locationModel.getDateTime().trim());
            magnitude.setText(locationModel.getMagnitude().trim());
            depth.setText(locationModel.getDepth().trim());

            try {
                int magColourId = locationModel.magnitudeColour(Double.valueOf(locationModel.getMagnitude().trim()));
                colourBackgroundTextView.setBackgroundColor(ContextCompat.getColor(locationViewContext, magColourId));
            } catch (Exception e) {
                Log.e("______________________________________________ back colour ______________________________________________", e.getMessage());
            }
        }
    }
}
