package com.pavan.vehiclerental.model;

import com.pavan.vehiclerental.enums.VehicleType;
import lombok.Getter;

@Getter
public abstract class Vehicle {
    private final String id;
    private final VehicleType type;
    private final Double price;

    public Vehicle(String id, VehicleType type, Double price) {
        this.id = id;
        this.type = type;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Vehicle{" +
                "id='" + id + '\'' +
                ", type=" + type +
                ", price=" + price +
                '}';
    }
}
