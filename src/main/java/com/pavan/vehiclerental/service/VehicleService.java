package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.enums.VehicleType;
import com.pavan.vehiclerental.exception.InvalidSlotDurationException;
import com.pavan.vehiclerental.factory.VehicleOnboardingFactory;
import com.pavan.vehiclerental.model.Branch;
import com.pavan.vehiclerental.model.Slot;
import com.pavan.vehiclerental.model.Vehicle;
import com.pavan.vehiclerental.model.VehicleAvailability;
import com.pavan.vehiclerental.store.BranchManager;
import com.pavan.vehiclerental.store.SlotsManager;
import com.pavan.vehiclerental.store.VehicleManager;
import com.pavan.vehiclerental.utils.Utils;
import com.pavan.vehiclerental.validator.RangeValidator;
import lombok.NonNull;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.pavan.vehiclerental.constants.SlotIntervalConstants.SLOT_INTERVAL;

public class VehicleService {

    private static final Integer DAY_START = 0;
    private static final Integer DAY_END = 24;
    private final VehicleManager vehicleManager;
    private final BranchManager branchManager;
    private final SlotsManager slotsManager;

    public VehicleService(final VehicleManager vehicleManager, final BranchManager branchManager,
                          final SlotsManager slotsManager) {
        this.vehicleManager = vehicleManager;
        this.branchManager = branchManager;
        this.slotsManager = slotsManager;
    }

    private boolean isVehicleTypeAllowed(final String branchId, final String vehicleType) {
        final Branch branch = branchManager.findById(branchId);
        return branch.getVehicleTypes().contains(VehicleType.fromString(vehicleType));
    }

    public Boolean addVehicle(@NonNull final String branchId, @NonNull final String vehicleType,
                              @NonNull final String vehicleId, @NonNull final Double price) {

        if (!isVehicleTypeAllowed(branchId, vehicleType)) return false;

        Vehicle vehicle = VehicleOnboardingFactory.onboardVehicle(vehicleId, vehicleType, price);
        if (vehicle == null) return false;
        vehicleManager.save(vehicle);

        // Add the vehicle in the respective branch
        final Branch branch = branchManager.findById(branchId);
        final List<String> updatedVehicleIds = Optional.ofNullable(branch.getVehicleIds()).orElse(new ArrayList<>());
        updatedVehicleIds.add(vehicleId);

        branch.setVehicleIds(updatedVehicleIds);
        branchManager.update(branch);

        // Update the vehicle availability for the respective slots
        final List<Slot> slots = slotsManager.findAll(branchId, vehicleType, DAY_START, DAY_END, SLOT_INTERVAL);
        slots.forEach(slot -> {
            slot.setAvailableVehiclesCnt(slot.getAvailableVehiclesCnt() + 1);
            slot.getVehicles().add(VehicleAvailability.builder()
                    .id(vehicleId)
                    .status(VehicleStatus.AVAILABLE)
                    .build());
        });

        slotsManager.updateAll(slots);

        return true;
    }

    public List<Vehicle> getAllVehicles(@NonNull final String branchId, @NonNull final Integer startTime,
                                        @NonNull final Integer endTime,
                                        @NonNull Pageable pageable,
                                        @NonNull final VehicleStatus status) {

        if (!RangeValidator.isValid(startTime, endTime)) {
            throw new InvalidSlotDurationException();
        }

        final List<Vehicle> result = new ArrayList<>();
        final Branch branch = branchManager.findById(branchId);
        for (final VehicleType vehicleType : Optional.ofNullable(branch.getVehicleTypes()).orElse(new ArrayList<>())) {

            final List<String> availableVehicles = slotsManager.getAllVehicles(branchId, vehicleType.toString(),
                    startTime, endTime, SLOT_INTERVAL, status);

            final List<Vehicle> availableVehiclesList = Optional.ofNullable(availableVehicles).orElse(new ArrayList<>())
                    .stream().map(vehicleManager::findById).toList();

            result.addAll(availableVehiclesList);
        }

        return Utils.sortAndPaginateList(result, pageable).stream().toList();
    }
}
