package com.pavan.vehiclerental.model;

import com.pavan.vehiclerental.enums.VehicleType;

public class Bus extends Vehicle {
    public Bus(String id, Double price) {
        super(id, VehicleType.BUS, price);
    }
}
