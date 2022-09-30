package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.enums.BookingStatus;
import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.enums.VehicleType;
import com.pavan.vehiclerental.exception.InvalidSlotDurationException;
import com.pavan.vehiclerental.model.*;
import com.pavan.vehiclerental.store.BookingManager;
import com.pavan.vehiclerental.store.BranchManager;
import com.pavan.vehiclerental.store.SlotsManager;
import com.pavan.vehiclerental.strategy.VehicleSelectionStrategy;
import com.pavan.vehiclerental.validator.RangeValidator;
import lombok.NonNull;

import java.util.List;
import java.util.UUID;

import static com.pavan.vehiclerental.constants.SlotIntervalConstants.SLOT_INTERVAL;

public class BookingService {

    private final VehicleSelectionStrategy vehicleSelectionStrategy;
    private final SlotsManager slotsManager;
    private final BookingManager bookingManager;
    private final BranchManager branchManager;

    public BookingService(final VehicleSelectionStrategy vehicleSelectionStrategy,
                          final SlotsManager slotsManager,
                          final BookingManager bookingManager,
                          final BranchManager branchManager) {
        this.vehicleSelectionStrategy = vehicleSelectionStrategy;
        this.slotsManager = slotsManager;
        this.bookingManager = bookingManager;
        this.branchManager = branchManager;
    }

    public Double bookVehicle(@NonNull final String branchId,
                              @NonNull final String vehicleType,
                              @NonNull final Integer startTime,
                              @NonNull final Integer endTime) {

        if (!RangeValidator.isValid(startTime, endTime)) {
            throw new InvalidSlotDurationException();
        }

        final Branch branch = branchManager.findById(branchId);
        final VehicleType vehicleTypeValue = VehicleType.fromString(vehicleType);

        final VehicleSelectionStrategyResponse vehicleSelectionStrategyResponse = vehicleSelectionStrategy.selectVehicle(
                branch.getId(), vehicleTypeValue.toString(), startTime, endTime, SLOT_INTERVAL);

        if (vehicleSelectionStrategyResponse.getVehicles().size() == 0) return (double) -1;
        final List<Vehicle> selectedVehicles = vehicleSelectionStrategyResponse.getVehicles();
        final List<String> selectedVehicleIds = selectedVehicles.stream().map(Vehicle::getId).toList();

        final List<Slot> filteredSlots = slotsManager.findAll(branchId, vehicleType, startTime, endTime, SLOT_INTERVAL);

        for (final Slot slot : filteredSlots) {
            slot.setAvailableVehiclesCnt(slot.getAvailableVehiclesCnt() - 1);
            slot.getVehicles().forEach(vehicle -> {
                if (selectedVehicleIds.contains(vehicle.getId())) {
                    vehicle.setStatus(VehicleStatus.BOOKED);
                }
            });
        }
        slotsManager.updateAll(filteredSlots);

        final String bookingId = UUID.randomUUID().toString();
        bookingManager.save(Booking.builder()
                .id(bookingId)
                .vehicleIds(selectedVehicleIds)
                .branchId(branch.getId())
                .price(vehicleSelectionStrategyResponse.getTotalAmount())
                .status(BookingStatus.CONFIRMED)
                .build());

        return vehicleSelectionStrategyResponse.getTotalAmount();
    }
}
