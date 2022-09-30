package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.enums.BookingStatus;
import com.pavan.vehiclerental.enums.VehicleType;
import com.pavan.vehiclerental.model.*;
import com.pavan.vehiclerental.service.model.*;
import com.pavan.vehiclerental.store.BookingManager;
import com.pavan.vehiclerental.store.BranchManager;
import com.pavan.vehiclerental.store.SlotsManager;
import com.pavan.vehiclerental.strategy.VehicleSelectionStrategy;

import java.util.List;
import java.util.UUID;

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

    public Double bookVehicle(final String branchId, final String vehicleType, final Integer startTime, final Integer endTime) {
        final String bookingId = UUID.randomUUID().toString();

        final Branch branch = branchManager.findById(branchId);
        final VehicleType vehicleTypeValue = VehicleType.fromString(vehicleType);

        final VehicleSelectionStrategyResponse vehicleSelectionStrategyResponse = vehicleSelectionStrategy.selectVehicle(
                branch.getId(), vehicleTypeValue.toString(), startTime, endTime);

        if (vehicleSelectionStrategyResponse.getVehicles().size() == 0) return (double) -1;
        final List<Vehicle> selectedVehicles = vehicleSelectionStrategyResponse.getVehicles();

        final List<Slot> filteredSlots = slotsManager.findAll(branchId, vehicleType, startTime, endTime);
        List<String> selectedVehicleIds = selectedVehicles.stream().map(Vehicle::getId).toList();
        for (final Slot slot : filteredSlots) {
            slot.setAvailableVehiclesCnt(slot.getAvailableVehiclesCnt() - 1);
            slot.getVehicleIds().removeAll(selectedVehicleIds);
        }
        slotsManager.updateAll(filteredSlots);

        bookingManager.save(Booking.builder()
                .id(bookingId)
                .vehicleIds(selectedVehicleIds)
                .branchId(branchId)
                .price(vehicleSelectionStrategyResponse.getTotalAmount())
                .status(BookingStatus.CONFIRMED)
                .build());

        return vehicleSelectionStrategyResponse.getTotalAmount();
    }
}
