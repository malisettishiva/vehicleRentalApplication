package com.pavan.vehiclerental.strategy;

import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.exception.VehicleNotAvailableException;
import com.pavan.vehiclerental.model.VehicleAvailability;
import com.pavan.vehiclerental.model.VehicleSelectionStrategyResponse;
import com.pavan.vehiclerental.service.SlotsService;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import static com.pavan.vehiclerental.constants.SlotIntervalConstants.SLOT_INTERVAL;

public class DefaultVehicleSelectionStrategy implements VehicleSelectionStrategy {

    private final SlotsService slotsService;

    public DefaultVehicleSelectionStrategy(final SlotsService slotsService) {
        this.slotsService = slotsService;
    }

    @Override
    public VehicleSelectionStrategyResponse selectVehicle(final String branchId, final String vehicleType,
                                                          final Integer startTime, final Integer endTime) {

        List<VehicleAvailability> availableVehicles = slotsService.fetchVehicles(branchId, vehicleType,
                startTime, endTime, SLOT_INTERVAL, VehicleStatus.AVAILABLE);

        if(availableVehicles.size()==0){
            return VehicleSelectionStrategyResponse.builder()
                    .selectedVehicles(new ArrayList<>())
                    .totalAmount(0.0)
                    .build();
        }

        PriorityQueue<VehicleAvailability> vehicleAvailabilities = new PriorityQueue<>(
                availableVehicles.size(), new PricingComparator());
        vehicleAvailabilities.addAll(availableVehicles);

        final VehicleAvailability selectedVehicle = vehicleAvailabilities.poll();

        assert selectedVehicle != null;
        return VehicleSelectionStrategyResponse.builder()
                .selectedVehicles(List.of(selectedVehicle.getId()))
                .totalAmount(selectedVehicle.getPrice() * ((endTime - startTime) / SLOT_INTERVAL))
                .build();
    }
}
