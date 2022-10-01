package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.enums.VehicleType;
import com.pavan.vehiclerental.factory.VehicleOnboardingFactory;
import com.pavan.vehiclerental.model.Branch;
import com.pavan.vehiclerental.model.Slot;
import com.pavan.vehiclerental.model.Vehicle;
import com.pavan.vehiclerental.model.VehicleAvailability;
import com.pavan.vehiclerental.store.VehicleManager;
import com.pavan.vehiclerental.utils.Utils;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.pavan.vehiclerental.constants.SlotIntervalConstants.DAY_END;
import static com.pavan.vehiclerental.constants.SlotIntervalConstants.DAY_START;

public class VehicleService {

    private final VehicleOnboardingFactory vehicleOnboardingFactory;
    private final VehicleManager vehicleManager;

    private final SlotsService slotsService;

    public VehicleService(final VehicleManager vehicleManager,
                          final SlotsService slotsService,
                          final VehicleOnboardingFactory vehicleOnboardingFactory) {
        this.vehicleManager = vehicleManager;
        this.slotsService = slotsService;
        this.vehicleOnboardingFactory = vehicleOnboardingFactory;
    }

    public boolean addVehicle(@NonNull final String branchId, @NonNull final String vehicleType,
                              @NonNull final String vehicleId, @NonNull final Double price) {

        Vehicle vehicle = vehicleOnboardingFactory.createInstance(vehicleId, vehicleType, price);
        if (vehicle == null) return false;
        vehicleManager.save(vehicle);

        final List<Slot> filteredSlots = slotsService.fetchSlots(branchId, vehicleType, DAY_START, DAY_END);
        final List<VehicleAvailability> vehicleAvailabilities = new ArrayList<>();
        vehicleAvailabilities.add(VehicleAvailability.builder()
                .id(vehicleId)
                .price(price)
                .status(VehicleStatus.AVAILABLE)
                .build());
        slotsService.addVehicleAvailability(filteredSlots, vehicleAvailabilities);

        return true;
    }

    public List<Vehicle> getAllVehicles(final Branch branch, final Integer startTime, final Integer endTime,
                                        final Pageable pageable, final VehicleStatus status) {

        final List<Vehicle> result = new ArrayList<>();
        for (final VehicleType vehicleType : Optional.ofNullable(branch.getVehicleTypes()).orElse(new ArrayList<>())) {

            final List<VehicleAvailability> vehicleAvailabilities = slotsService.fetchVehicles(branch.getId(),
                    vehicleType.toString(), startTime, endTime, status);

            result.addAll(vehicleAvailabilities.stream().map(vehicle -> vehicleManager.findById(vehicle.getId())).toList());
        }

        return Utils.sortAndPaginateList(result, pageable).stream().toList();
    }
}
