package garflop;

import org.jdom.Attribute;
import java.util.*;


// Point instances are created from the list returned by calling getAttributes on an element.
// Each point adds itself to the Routepoints object that is identified in the initializer call.
public class Point {
    private final double lat;
    private final double lon;
    private final Boolean valid;
    private double elevation;
    private long epochTimeStamp;
    private int power;
    private int heartRate;
    private int cadence;

    // Point initializer
    public Point (List<Attribute> attr, RoutePoints route){

        Boolean hasLat = false;
        Boolean hasLon = false;
        double lat = 0, lon = 0;

        //Assign point lat/lon coordinates
        for ( int i=0 ; i<attr.size() ; i++ ) {
            String attrName = attr.get(i).getName();
            if (attrName.equals("lat")) { hasLat=true; lat=Double.parseDouble(attr.get(i).getValue()); }
            if (attrName.equals("lon")) { hasLon=true; lon=Double.parseDouble(attr.get(i).getValue()); }
        } // end for loop

        // check that both a lat and lon exist to ensure a valid point
        if (hasLat && hasLon)   this.valid = true;
        else                    this.valid = false;

        this.lat = lat;
        this.lon = lon;
        route.addPoint(this);

    } // end initializer


    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public double getElevation() {
        try {
            return elevation;
        } catch (NoSuchElementException e) {
            return 0;
        }
    }

    public void setElevation(double e) {
        this.elevation = e;
    }

    public long getEpochTimeStamp() {
        try {
            return epochTimeStamp;
        } catch (NoSuchElementException e) {
            return 0;
        }
    }

    public void setEpochTimeStamp(long t) {
        this.epochTimeStamp = t;
    }

    public boolean isValid() {
        return this.valid;
    }

    public int getPower() {
        try {
            return power;
        } catch (NoSuchElementException e) {
            return 0;
        }
    }

    public void setPower(int p) {
        this.power = p;
    }

    public int getHeartRate() {
        try {
            return heartRate;

        } catch (NoSuchElementException e) {
            return 0;
        }
    }

    public void setHeartRate(int hr) {
        this.heartRate = hr;
    }

    public int getCadence() {
        try {
            return cadence;
        } catch (NoSuchElementException e) {
            return 0;
        }
    }

    public void setCadence(int c) {
        this.cadence = c;
    }


}
