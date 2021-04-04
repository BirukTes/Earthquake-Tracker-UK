package com.bgulai_gcu.earthquaketracker.generalClasses;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Bereketab Gulai | s1827985
 */
public class LoadXmlFromNetwork {

    public LoadXmlFromNetwork() {

    }

    // Uploads XML from stackoverflow.com, parses it, and combines it with
    // HTML markup. Returns HTML string.
    public ArrayList<LocationModel> loadXml(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        // Instantiate the parser
        LocationXmlParser locationXmlParser = new LocationXmlParser();
        ArrayList<LocationModel> locationModelArrayList;

        try {
            stream = downloadUrl(urlString);
            locationModelArrayList = locationXmlParser.parse(stream);
            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }

        return locationModelArrayList;
    }

    // Given a string representation of a URL, sets up a connection and gets
// an input stream.
    private InputStream downloadUrl(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//        conn.setReadTimeout(10000 /* milliseconds */);
//        conn.setConnectTimeout(15000 /* milliseconds */);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        // Starts the query
        conn.connect();
        return conn.getInputStream();
    }
}
