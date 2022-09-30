package com.pavan.vehiclerental.strategy;

import com.pavan.vehiclerental.model.VehicleSelectionStrategyResponse;

public interface VehicleSelectionStrategy {
    VehicleSelectionStrategyResponse selectVehicle(final String branchId, final String vehicleType,
                                                   final Integer startTime, final Integer endTime);
}
