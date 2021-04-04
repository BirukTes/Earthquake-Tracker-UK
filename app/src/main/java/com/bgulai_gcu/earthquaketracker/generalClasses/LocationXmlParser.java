package com.bgulai_gcu.earthquaketracker.generalClasses;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bereketab Gulai | s1827985
 * <p>
 * Ref:https://developer.android.com/training/basics/network-ops/xml#java
 */
public class LocationXmlParser {
    // We don't use namespaces
    private static final String ns = null;

    public ArrayList<LocationModel> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private ArrayList<LocationModel> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<LocationModel> locationModelArrayList = new ArrayList<>();

        parser.require(XmlPullParser.START_TAG, ns, "rss");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            // Starts by looking for the entry tag
            if (name.equals("channel")) {
                parser.require(XmlPullParser.START_TAG, ns, "channel");
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    name = parser.getName();
                    // Starts by looking for the entry tag
                    if (name.equals("item")) {
                        locationModelArrayList.add(readItem(parser));
                    } else {
                        skip(parser);
                    }
                }
            } else {
                skip(parser);
            }
        }
        return locationModelArrayList;
    }

    // Parses the contents of an entry. If it encounters a title, summary, or link tag, hands them off
// to their respective "read" methods for processing. Otherwise, skips the tag.
    private LocationModel readItem(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "item");
        String location = null;
        String dateTime = null;
        String magnitude = null;
        String depth = null;
        String link = null;
        String geoLatitude = null;
        String geoLongitude = null;

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            switch (name) {
                case "description":
                    String[] description = readDescription(parser);
                    location = description[0];
                    depth = description[1];
                    magnitude = description[2];
                    break;
                case "pubDate":
                    dateTime = readDate(parser);
                    break;
                case "link":
                    link = readLink(parser);
                    break;
                case "lat":
                    geoLatitude = readLatitude(parser);
                    break;
                case "long":
                    geoLongitude = readLongitude(parser);
                    break;
                default:
                    skip(parser);
                    break;
            }
        }
        return new LocationModel(location, dateTime, magnitude, depth, link, geoLatitude, geoLongitude);
    }

    // Processes description tags in the feed.
    private String[] readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String[] description = readText(parser).split(";");
        String location = description[1].split(":")[1];
        String depth = description[3].split(":")[1];
        String magnitude = description[4].split(":")[1];

        parser.require(XmlPullParser.END_TAG, ns, "description");
        return new String[]{location, depth, magnitude};
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "link");
        String link = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "link");
        return link;
    }

    // Processes pubDate tags in the feed.
    private String readDate(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "pubDate");
        String dateTime = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "pubDate");
        return dateTime;
    }

    // Processes geo:lat tags in the feed.
    private String readLatitude(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "lat");
        String latitude = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "lat");
        return latitude;
    }

    // Processes geo:long tags in the feed.
    private String readLongitude(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "long");
        String longitude = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "long");
        return longitude;
    }

    // For the tags title and summary, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }


    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}