package com.pavan.vehiclerental.strategy;

import com.pavan.vehiclerental.model.VehicleSelectionStrategyResponse;

public class DefaultPricingStrategy implements PricingStrategy {
    @Override
    public Double getPrice(VehicleSelectionStrategyResponse vehicleSelectionStrategyResponse) {
        return vehicleSelectionStrategyResponse.getTotalAmount();
    }
}
