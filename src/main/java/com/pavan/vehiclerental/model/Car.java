package com.pavan.vehiclerental.model;

import com.pavan.vehiclerental.enums.VehicleType;

public class Car extends Vehicle {
    public Car(String id, Double price) {
        super(id, VehicleType.CAR, price);
    }
}
