package com.pavan.vehiclerental.factory;

import com.pavan.vehiclerental.model.Vehicle;

public abstract class VehicleOnboardingFactory {
    public abstract Vehicle createInstance(final String vehicleId, final String vehicleType, final Double price);
}
