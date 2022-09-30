package com.pavan.vehiclerental.strategy;

import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.model.Vehicle;
import com.pavan.vehiclerental.model.VehicleSelectionStrategyResponse;
import com.pavan.vehiclerental.service.SlotsService;

import java.util.ArrayList;
import java.util.List;

import static com.pavan.vehiclerental.constants.SlotIntervalConstants.SLOT_INTERVAL;

public class DefaultVehicleSelectionStrategy implements VehicleSelectionStrategy {

    private final SlotsService slotsService;

    public DefaultVehicleSelectionStrategy(final SlotsService slotsService) {
        this.slotsService = slotsService;
    }

    @Override
    public VehicleSelectionStrategyResponse selectVehicle(final String branchId, final String vehicleType,
                                                          final Integer startTime, final Integer endTime) {

        List<Vehicle> availableVehicles = slotsService.fetchVehicles(branchId, vehicleType,
                startTime, endTime, SLOT_INTERVAL, VehicleStatus.AVAILABLE);

        Double lowestPrice = Double.MAX_VALUE;
        Vehicle selectedVehicle = null;
        for (final Vehicle vehicle : availableVehicles) {
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
                .totalAmount(lowestPrice * ((endTime - startTime) / SLOT_INTERVAL))
                .build();
    }
}
