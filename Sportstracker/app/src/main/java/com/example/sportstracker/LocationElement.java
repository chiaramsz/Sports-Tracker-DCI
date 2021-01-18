package com.example.sportstracker;

import com.google.android.gms.maps.model.LatLng;
import java.util.Date;

public class LocationElement {
    private Date time;
    private LatLng location;

    public LocationElement(Date time, LatLng location) {
        this.time = time;
        this.location = location;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getUnixTimestamp() {
        return String.valueOf(time.getTime()/1000);
    }

    public String getAsJsonKeyValuePair() {
        return "\"" + time.getTime()/1000 + "\": [" +
                "{\"longitude\": " + location.longitude + "}," +
                "{\"latitude\": " + location.latitude + "}]";
    }
}
