package com.bgulai_gcu.earthquaketracker.generalClasses;


import androidx.core.content.ContextCompat;

import com.bgulai_gcu.earthquaketracker.R;

import java.sql.Struct;

/**
 * Created by Bereketab Gulai | s1827985
 */
public class LocationModel {

    // Model Class
    private String location;
    private String dateTime;
    private String magnitude;
    private String depth;
    private String link;
    private String geoLatitude;
    private String geoLongitude;


    public LocationModel(String location, String dateTime, String magnitude, String depth, String link, String geoLatitude, String geoLongitude) {
        this.location = location;
        this.dateTime = dateTime;
        this.magnitude = magnitude;
        this.depth = depth;
        this.link = link;
        this.geoLatitude = geoLatitude;
        this.geoLongitude = geoLongitude;
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

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(String magnitude) {
        this.magnitude = magnitude;
    }

    public String getDepth() {
        return depth;
    }

    public void setDepth(String depth) {
        this.depth = depth;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getGeoLatitude() {
        return geoLatitude;
    }

    public void setGeoLatitude(String geoLatitude) {
        this.geoLatitude = geoLatitude;
    }

    public String getGeoLongitude() {
        return geoLongitude;
    }

    public void setGeoLongitude(String geoLongitude) {
        this.geoLongitude = geoLongitude;
    }
}
