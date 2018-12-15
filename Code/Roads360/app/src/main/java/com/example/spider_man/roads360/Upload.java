package com.example.spider_man.roads360;

import com.google.firebase.database.Exclude;

/**
 * Created by Spider-Man on 12/14/2018.
 */

public class Upload {
    public  String detailsLocation;
    public  String currentDate;
    public  String currentTime;
    public  String trafficHour;
    public  String trafficDay;
    public  String avgTrafficSituation;
    public String location;
    public String url;
    private String mKey;

    // Default constructor required for calls to
    // DataSnapshot.getValue(User.class)
    public Upload() {
    }

    public Upload(String location, String url,String currentDate, String currentTime, String trafficHour,String trafficDay,String avgTrafficSituation, String detailsLocation) {

        this.detailsLocation = detailsLocation;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
        this.trafficDay = trafficDay;
        this.trafficHour = trafficHour;
        this.avgTrafficSituation =avgTrafficSituation;
        this.location = location;
        this.url= url;
    }

    @Exclude
    public String getKey( )
    {
        return mKey;
    }
    @Exclude
    public void setKey(String key)
    {
        mKey = key;

    }

    public String getDetailsLocation() {return detailsLocation;}
    public String getAvgTrafficSituation() {
        return avgTrafficSituation;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public String getTrafficDay() {
        return trafficDay;
    }

    public String getTrafficHour() {
        return trafficHour;
    }

    public String getLocation() {
        return location;
    }

    public String getUrl() {
        return url;
    }
}
