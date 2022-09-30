package com.pavan.vehiclerental.strategy;

import com.pavan.vehiclerental.model.VehicleSelectionStrategyResponse;

public interface PricingStrategy {
    Double getPrice(VehicleSelectionStrategyResponse vehicleSelectionStrategyResponse);
}
