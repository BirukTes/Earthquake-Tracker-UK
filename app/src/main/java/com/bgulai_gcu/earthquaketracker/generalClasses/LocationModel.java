package com.bgulai_gcu.earthquaketracker.generalClasses;


import android.icu.text.SimpleDateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bgulai_gcu.earthquaketracker.R;

import java.sql.Struct;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Bereketab Gulai | s1827985
 */
public class LocationModel implements ClusterItem {

    // Model Class
    private String location;
    private Date dateTime;
    private double magnitude;
    private double depth;
    private String link;
    private double geoLatitude;
    private double geoLongitude;
    private String title;
    private String snippet;
    private LatLng position;

    public static Comparator<LocationModel> dateComparator = new Comparator<LocationModel>() {
        @Override
        public int compare(LocationModel o1, LocationModel o2) {
            Date o1Mag = o1.getDateTime();
            Date o2Mag = o2.getDateTime();
            return o2Mag.compareTo(o1Mag); // Asc
        }
    };
    public static Comparator<LocationModel> magnitudeComparator = new Comparator<LocationModel>() {
        @Override
        public int compare(LocationModel o1, LocationModel o2) {
            Double o1Mag = o1.getMagnitude();
            Double o2Mag = o2.getMagnitude();
            return o1Mag.compareTo(o2Mag);
        }
    };
    public static Comparator<LocationModel> depthComparator = new Comparator<LocationModel>() {
        @Override
        public int compare(LocationModel o1, LocationModel o2) {
            Double o1Depth = o1.getDepth();
            Double o2Depth = o2.getDepth();

            return o1Depth.compareTo(o2Depth);
        }
    };

    public LocationModel(String location, Date dateTime, double magnitude, double depth, String link, double geoLatitude, double geoLongitude) {
        this.location = location;
        this.dateTime = dateTime;
        this.magnitude = magnitude;
        this.depth = depth;
        this.link = link;
        this.geoLatitude = geoLatitude;
        this.geoLongitude = geoLongitude;

        this.title = location;
        this.snippet = "Magnitude: " + String.valueOf(magnitude) + " | Date: " + (new SimpleDateFormat("dd/MM/yy HH:mm")).format(dateTime);
        this.position = new LatLng(geoLatitude, geoLongitude);
    }

    public int magnitudeColour(double magnitude) {
        int colorCode = 0;

        // Ref: https://cdn3.vectorstock.com/i/1000x1000/41/82/earthquake-magnitude-scale-vector-20714182.jpg
        if (magnitude >= 0 && magnitude <= 1.9) {
            colorCode = R.color.miro_green; // Miro green
        }
        if (magnitude >= 2 && magnitude <= 2.9) {
            colorCode = R.color.minor_lim; // Minor lim
        }
        if (magnitude >= 3 && magnitude <= 3.9) {
            colorCode = R.color.minor_yellow; // Minor yellow
        }
        if (magnitude >= 4 && magnitude <= 4.9) {
            colorCode = R.color.light_orange; // Light orange
        }
        if (magnitude >= 5 && magnitude <= 5.9) {
            colorCode = R.color.moderate_slight_dark_orange; // Moderate slight dark orange
        }
        if (magnitude >= 6 && magnitude <= 6.9) {
            colorCode = R.color.strong_dark_orange; // Strong dark orange
        }
        if (magnitude >= 7 && magnitude <= 7.9) {
            colorCode = R.color.major_red; // Major red
        }
        if (magnitude >= 8 && magnitude <= 8.9) {
            colorCode = R.color.great_hot_pink; // Great hot pink
        }
        if (magnitude >= 9) {
            colorCode = R.color.greater_dark_red; // Greater dark red
        }
        return colorCode;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public double getGeoLatitude() {
        return geoLatitude;
    }

    public void setGeoLatitude(double geoLatitude) {
        this.geoLatitude = geoLatitude;
    }

    public double getGeoLongitude() {
        return geoLongitude;
    }

    public void setGeoLongitude(double geoLongitude) {
        this.geoLongitude = geoLongitude;
    }

    public static Comparator<LocationModel> getMagnitudeComparator() {
        return magnitudeComparator;
    }

    public static void setMagnitudeComparator(Comparator<LocationModel> magnitudeComparator) {
        LocationModel.magnitudeComparator = magnitudeComparator;
    }

    public static Comparator<LocationModel> getDepthComparator() {
        return depthComparator;
    }

    public static void setDepthComparator(Comparator<LocationModel> depthComparator) {
        LocationModel.depthComparator = depthComparator;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return position;
    }

    @Nullable
    @Override
    public String getTitle() {
        return title;
    }

    @Nullable
    @Override
    public String getSnippet() {
        return snippet;
    }
}
