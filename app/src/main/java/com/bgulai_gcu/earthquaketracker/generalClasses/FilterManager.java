package com.bgulai_gcu.earthquaketracker.generalClasses;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Bereketab Gulai | s1827985
 */
public class FilterManager {

    private static final SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");

    public static void initialisePrefs(ArrayList<String> datesArray, ArrayList<String> magArray, ArrayList<String> depthArray, ArrayList<String> locationArray) {
        Preferences.filters.put(Filter.INDEX_DATE, new Filter("Dates", datesArray, new ArrayList<>()));
        Preferences.filters.put(Filter.INDEX_MAGNITUDE, new Filter("Magnitudes", magArray, new ArrayList<>()));
        Preferences.filters.put(Filter.INDEX_DEPTH, new Filter("Depth", depthArray, new ArrayList<>()));
        Preferences.filters.put(Filter.INDEX_LOCATION, new Filter("Location", locationArray, new ArrayList<>()));
    }

    public static void clearAll() {
        if (!FilterManager.Preferences.filters.isEmpty()) {
            if (Preferences.filters.containsKey(Filter.INDEX_DATE)) {
                Preferences.filters.get(Filter.INDEX_DATE).setSelected(new ArrayList<>());
            }
            if (Preferences.filters.containsKey(Filter.INDEX_MAGNITUDE)) {
                Preferences.filters.get(Filter.INDEX_MAGNITUDE).setSelected(new ArrayList<>());
            }
            if (Preferences.filters.containsKey(Filter.INDEX_DEPTH)) {
                Preferences.filters.get(Filter.INDEX_DEPTH).setSelected(new ArrayList<>());
            }
            if (Preferences.filters.containsKey(Filter.INDEX_LOCATION)) {
                Preferences.filters.get(Filter.INDEX_LOCATION).setSelected(new ArrayList<>());
            }
        }
    }

    public static ArrayList<LocationModel> filterList(ArrayList<LocationModel> locationModelList) {
        if (!FilterManager.Preferences.filters.isEmpty()) {
            ArrayList<LocationModel> filteredLocationModels = new ArrayList<>();
            List<String> dates = Preferences.filters.get(Filter.INDEX_DATE).getSelected();
            List<String> magnitudes = Preferences.filters.get(Filter.INDEX_MAGNITUDE).getSelected();
            List<String> depthList = Preferences.filters.get(Filter.INDEX_DEPTH).getSelected();
            List<String> locations = Preferences.filters.get(Filter.INDEX_LOCATION).getSelected();

            Date minDate = new Date();
            Date maxDate = new Date();
            if (dates.size() > 0) {
                try {
                    minDate = format.parse(dates.get(0));
                    maxDate = format.parse(dates.get(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            for (LocationModel locationModel : locationModelList) {
                boolean datesMatched = true;
                try {
                    if (dates.size() > 0) {
                        Date dateToComp = getBaseDate(locationModel.getDateTime());
                        minDate = getBaseDate(minDate);
                        maxDate = getBaseDate(maxDate);

                        //  0 comes when two dateToCompTime are same,
                        //  1 comes when date1 is higher then date2
                        // -1 comes when date1 is lower then date2
                        int comp1 = dateToComp.compareTo(minDate);
                        int comp2 = dateToComp.compareTo(maxDate);

                        // For Truthy: comp1 must be >= 0 and comp2 <= 0
                        //      flip it to Falsey
                        if (!(comp1 >= 0 && comp2 <= 0)) {
                            datesMatched = false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                boolean magnitudesMatched = true;
                if (magnitudes.size() > 0) {
                    if (!magnitudeContains(magnitudes, locationModel.getMagnitude())) {
                        magnitudesMatched = false;
                    }
                }
                boolean depthMatched = true;
                if (depthList.size() > 0) {
                    if (!depthContains(depthList, locationModel.getDepth())) {
                        depthMatched = false;
                    }
                }
                boolean locationMatched = true;
                if (locations.size() > 0) {
                    String[] directions = getFormattedLocationInDegreeDirections(locationModel.getGeoLatitude(), locationModel.getGeoLongitude());
                    if (!locationContains(locations, directions)) {
                        locationMatched = false;
                    }
                }
                if (datesMatched && magnitudesMatched && depthMatched && locationMatched) {
                    filteredLocationModels.add(locationModel);
                }
            }
            return filteredLocationModels;
        } else {
            return locationModelList;
        }
    }

    private static Date getBaseDate(Date date) {
        Calendar dateToComp = Calendar.getInstance();
        dateToComp.setTime(date);
        dateToComp.set(Calendar.HOUR_OF_DAY, 0);
        dateToComp.set(Calendar.MINUTE, 0);
        dateToComp.set(Calendar.SECOND, 0);
        dateToComp.set(Calendar.MILLISECOND, 0);
        return dateToComp.getTime();
    }


    private static boolean magnitudeContains(List<String> magnitudes, Double magnitude) {
        boolean flag = false;
        String[] tmpMag;
        if (magnitudes.get(0).contains("Plus")) {
            tmpMag = magnitudes.get(0).split(" Plus");
        } else {
            tmpMag = magnitudes.get(0).split(" to ");
        }
        switch (tmpMag[0]) {
            case "0.0":
                if (magnitude >= 0.0 && magnitude <= 2.9) {
                    flag = true;
                }
                break;
            case "3.0":
                if (magnitude >= 3.0 && magnitude <= 5.9) {
                    flag = true;
                }
                break;
            case "6.0":
                if (magnitude >= 6.0 && magnitude <= 8.9) {
                    flag = true;
                }
                break;
            case "9.0":
                if (magnitude >= 9.0) {
                    flag = true;
                }
                break;
        }
        return flag;
    }

    private static boolean depthContains(List<String> depths, Double depth) {
        boolean flag = false;
        switch (depths.get(0)) {
            case "Shallowest (0–70)":
                if (depth <= 70.0) {
                    flag = true;
                }
                break;
            case "Intermediate (70–300)":
                if (depth > 70.0 && depth <= 300) {
                    flag = true;
                }
                break;
            case "Deepest (300–700)":
                if (depth > 300.0 && depth <= 700.0) {
                    flag = true;
                }
                break;
        }
        return flag;
    }

    private static boolean locationContains(List<String> locations, String[] locationDirections) {
        boolean flag = false;

        switch (locations.get(0)) {
            case "Northerly":
                // Use LatDegree for this
                if (locationDirections[0].contains("North")) {
                    flag = true;
                }
                break;
            case "Southerly":
                // Use LatDegree for this
                if (locationDirections[0].contains("South")) {
                    flag = true;
                }
                break;
            case "Westerly":
                // Use LongDegree for this
                if (locationDirections[1].contains("West")) {
                    flag = true;
                }
                break;
            case "Easterly":
                // Use LongDegree for this
                if (locationDirections[1].contains("East")) {
                    flag = true;
                }
                break;
        }
        return flag;
    }

    // Ref: https://stackoverflow.com/a/47236287/5124710
    public static String[] getFormattedLocationInDegreeDirections(double latitude, double longitude) {
        try {
            int latSeconds = (int) Math.round(latitude * 3600);
            int latDegrees = latSeconds / 3600;
            latSeconds = Math.abs(latSeconds % 3600);
            int latMinutes = latSeconds / 60;
            latSeconds %= 60;

            int longSeconds = (int) Math.round(longitude * 3600);
            int longDegrees = longSeconds / 3600;
            longSeconds = Math.abs(longSeconds % 3600);
            int longMinutes = longSeconds / 60;
            longSeconds %= 60;
            String latDegree = latDegrees >= 0 ? "North" : "South";
            String lonDegrees = longDegrees >= 0 ? "East" : "West";

            return new String[]{latDegree, lonDegrees};
        } catch (Exception e) {
            return null;
        }
    }

    public abstract static class Preferences {
        public static HashMap<Integer, Filter> filters = new HashMap<>();
    }
}

