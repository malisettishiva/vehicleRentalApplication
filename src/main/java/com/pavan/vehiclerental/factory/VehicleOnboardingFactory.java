package com.pavan.vehiclerental.factory;

import com.pavan.vehiclerental.enums.VehicleType;
import com.pavan.vehiclerental.model.*;
import com.pavan.vehiclerental.service.model.*;

public class VehicleOnboardingFactory {
    public static Vehicle onboardVehicle(String vehicleId, String vehicleType, Double price) {
        if (VehicleType.fromString(vehicleType).equals(VehicleType.CAR)) {
            return new Car(vehicleId, price);
        } else if (VehicleType.fromString(vehicleType).equals(VehicleType.BIKE)) {
            return new MotorBike(vehicleId, price);
        } else if (VehicleType.fromString(vehicleType).equals(VehicleType.BUS)) {
            return new Bus(vehicleId, price);
        } else if (VehicleType.fromString(vehicleType).equals(VehicleType.VAN)) {
            return new Van(vehicleId, price);
        }

        return null;
    }
}
