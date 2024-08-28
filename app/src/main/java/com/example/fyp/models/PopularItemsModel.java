package com.example.fyp.models;

public class PopularItemsModel {
    private String carName;
    private int popularCar;

    public PopularItemsModel(String carName, int popularCar) {
        this.carName = carName;
        this.popularCar = popularCar;
    }


    public String getCarName() {
        return carName;
    }

    public int getPopularCar() {
        return popularCar;
    }
}
