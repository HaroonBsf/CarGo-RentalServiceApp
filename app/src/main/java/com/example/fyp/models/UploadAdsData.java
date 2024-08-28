package com.example.fyp.models;

public class UploadAdsData {
    String make, model, year, transmission, variant, color, engine_type, engine_capacity, seating_capacity, car_type, pickup_city, insurance, registeration, driver_available, mileage, rent, desc, ownerName, ownerCompany, ownerPhone, date;

    public UploadAdsData(String make, String model, String year, String transmission, String variant, String color, String engine_type, String engine_capacity, String seating_capacity, String car_type, String pickup_city, String insurance, String registeration, String driver_available, String mileage, String rent, String desc, String ownerName, String ownerCompany, String ownerPhone, String date) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.transmission = transmission;
        this.variant = variant;
        this.color = color;
        this.engine_type = engine_type;
        this.engine_capacity = engine_capacity;
        this.seating_capacity = seating_capacity;
        this.car_type = car_type;
        this.pickup_city = pickup_city;
        this.insurance = insurance;
        this.registeration = registeration;
        this.driver_available = driver_available;
        this.mileage = mileage;
        this.rent = rent;
        this.desc = desc;
        this.ownerName = ownerName;
        this.ownerCompany = ownerCompany;
        this.ownerPhone = ownerPhone;
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOwnerPhone() {
        return ownerPhone;
    }

    public void setOwnerPhone(String ownerPhone) {
        this.ownerPhone = ownerPhone;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerCompany() {
        return ownerCompany;
    }

    public void setOwnerCompany(String ownerCompany) {
        this.ownerCompany = ownerCompany;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getTransmission() {
        return transmission;
    }

    public void setTransmission(String transmission) {
        this.transmission = transmission;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEngine_type() {
        return engine_type;
    }

    public void setEngine_type(String engine_type) {
        this.engine_type = engine_type;
    }

    public String getEngine_capacity() {
        return engine_capacity;
    }

    public void setEngine_capacity(String engine_capacity) {
        this.engine_capacity = engine_capacity;
    }

    public String getSeating_capacity() {
        return seating_capacity;
    }

    public void setSeating_capacity(String seating_capacity) {
        this.seating_capacity = seating_capacity;
    }

    public String getCar_type() {
        return car_type;
    }

    public void setCar_type(String car_type) {
        this.car_type = car_type;
    }

    public String getPickup_city() {
        return pickup_city;
    }

    public void setPickup_city(String pickup_city) {
        this.pickup_city = pickup_city;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getRegisteration() {
        return registeration;
    }

    public void setRegisteration(String registeration) {
        this.registeration = registeration;
    }

    public String getDriver_available() {
        return driver_available;
    }

    public void setDriver_available(String driver_available) {
        this.driver_available = driver_available;
    }

    public String getMileage() {
        return mileage;
    }

    public void setMileage(String mileage) {
        this.mileage = mileage;
    }

    public String getRent() {
        return rent;
    }

    public void setRent(String rent) {
        this.rent = rent;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
