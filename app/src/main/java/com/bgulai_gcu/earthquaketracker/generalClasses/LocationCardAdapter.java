package com.bgulai_gcu.earthquaketracker.generalClasses;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

        LocationHolder(View locationCardView) {
            super(locationCardView);

            locationCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDialog();
                }
            });

            location = locationCardView.findViewById(R.id.locationTextView);
            dateTime = locationCardView.findViewById(R.id.dateTimeTextView);
            magnitude = locationCardView.findViewById(R.id.magnitudeTextView);
            depth = locationCardView.findViewById(R.id.depthTextView);
            colourBackgroundTextView = locationCardView.findViewById(R.id.colourBackgroundTextView);
        }

        void setDetails(LocationModel locationModel) {
            location.setText(locationModel.getLocation());
            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");
            try {
                String date = format.format(locationModel.getDateTime());
                dateTime.setText(date);
            } catch (Exception e) {
                e.printStackTrace();
            }
            magnitude.setText(String.valueOf(locationModel.getMagnitude()));
            depth.setText(String.valueOf(locationModel.getDepth()) + " km");

            try {
                int magColourId = locationModel.magnitudeColour(locationModel.getMagnitude());
                colourBackgroundTextView.setBackgroundColor(ContextCompat.getColor(context, magColourId));
            } catch (Exception e) {
                Log.e("______________________________________________ back colour ______________________________________________", e.getMessage());
            }
        }

        private void showDialog() {
            // Custom dialog setup
            final Dialog dialog = new Dialog(context);

            dialog.setContentView(R.layout.location_card_dialog);
            dialog.setTitle("Options");

            Button dialogOKButton = dialog.findViewById(R.id.dialogOKButton);
            Button dialogCancelButton = dialog.findViewById(R.id.dialogCancelButton);

            dialogOKButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    showLocationDetails();
                }
            });

            dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }


        private void showLocationDetails() {
            LocationModel locationModel = locationModelList.get(getAdapterPosition());

            /**
             * Open a web page of a specified URL
             *
             * @param url URL to open
             */
            try {
                String link = locationModel.getLink().replace("http", "https");

                Uri webpage = Uri.parse(link);
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);

                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
