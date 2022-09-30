package com.pavan.vehiclerental.strategy;

import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.model.Vehicle;
import com.pavan.vehiclerental.model.VehicleSelectionStrategyResponse;
import com.pavan.vehiclerental.store.SlotsManager;
import com.pavan.vehiclerental.store.VehicleManager;

import java.util.ArrayList;
import java.util.List;

public class DefaultVehicleSelectionStrategy implements VehicleSelectionStrategy {

    private final VehicleManager vehicleManager;
    private final SlotsManager slotsManager;

    public DefaultVehicleSelectionStrategy(final SlotsManager slotsManager, final VehicleManager vehicleManager) {
        this.slotsManager = slotsManager;
        this.vehicleManager = vehicleManager;
    }

    @Override
    public VehicleSelectionStrategyResponse selectVehicle(final String branchId, final String vehicleType,
                                                          final Integer startTime, final Integer endTime,
                                                          final Integer interval) {

        List<String> availableVehicles = slotsManager.getAllVehicles(branchId, vehicleType,
                startTime, endTime, interval, VehicleStatus.AVAILABLE);

        Double lowestPrice = Double.MAX_VALUE;
        Vehicle selectedVehicle = null;
        for (final String vehicleId : availableVehicles) {
            final Vehicle vehicle = vehicleManager.findById(vehicleId);
            if (lowestPrice > vehicle.getPrice()) {
                lowestPrice = vehicle.getPrice();
                selectedVehicle = vehicle;
            }
        }

        if (selectedVehicle == null) {
            return VehicleSelectionStrategyResponse.builder()
                    .vehicles(new ArrayList<>())
                    .totalAmount((double) 0)
                    .build();
        }

        return VehicleSelectionStrategyResponse.builder()
                .vehicles(List.of(selectedVehicle))
                .totalAmount(lowestPrice * (endTime - startTime))
                .build();
    }
}
