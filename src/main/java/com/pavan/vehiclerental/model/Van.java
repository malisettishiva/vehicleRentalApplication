package com.pavan.vehiclerental.model;

import com.pavan.vehiclerental.enums.VehicleType;

public class Van extends Vehicle {
    public Van(String id, Double price) {
        super(id, VehicleType.VAN, price);
    }
}
