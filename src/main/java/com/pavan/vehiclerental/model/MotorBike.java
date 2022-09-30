package com.pavan.vehiclerental.model;

import com.pavan.vehiclerental.enums.VehicleType;

public class MotorBike extends Vehicle {
    public MotorBike(String id, Double price) {
        super(id, VehicleType.BIKE, price);
    }
}
