package com.example.fyp.models;

public class LocationSliderModel {
    public String  country, city;

    public LocationSliderModel(String country, String city) {
        this.country = country;
        this.city = city;

    }

    public String getCountry() {
        return country;
    }

    public String getCity() {
        return city;
    }
}
