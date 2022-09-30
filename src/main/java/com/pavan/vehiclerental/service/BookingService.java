package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.enums.BookingStatus;
import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.model.Booking;
import com.pavan.vehiclerental.model.Slot;
import com.pavan.vehiclerental.model.Vehicle;
import com.pavan.vehiclerental.model.VehicleSelectionStrategyResponse;
import com.pavan.vehiclerental.store.BookingManager;
import com.pavan.vehiclerental.store.SlotsManager;
import com.pavan.vehiclerental.strategy.VehicleSelectionStrategy;

import java.util.List;
import java.util.UUID;

import static com.pavan.vehiclerental.constants.SlotIntervalConstants.SLOT_INTERVAL;

public class BookingService {

    private final VehicleSelectionStrategy vehicleSelectionStrategy;
    private final SlotsManager slotsManager;
    private final BookingManager bookingManager;

    public BookingService(final BookingManager bookingManager,
                          final SlotsManager slotsManager,
                          final VehicleSelectionStrategy vehicleSelectionStrategy) {
        this.vehicleSelectionStrategy = vehicleSelectionStrategy;
        this.slotsManager = slotsManager;
        this.bookingManager = bookingManager;
    }

    public Double bookVehicle(final String branchId, final String vehicleType, final Integer startTime,
                              final Integer endTime) {

        final VehicleSelectionStrategyResponse vehicleSelectionStrategyResponse = vehicleSelectionStrategy.selectVehicle(
                branchId, vehicleType, startTime, endTime, SLOT_INTERVAL);

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
                .branchId(branchId)
                .price(vehicleSelectionStrategyResponse.getTotalAmount())
                .status(BookingStatus.CONFIRMED)
                .build());

        return vehicleSelectionStrategyResponse.getTotalAmount();
    }
}
