package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.enums.BookingStatus;
import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.model.*;
import com.pavan.vehiclerental.store.BookingManager;
import com.pavan.vehiclerental.strategy.PricingStrategy;
import com.pavan.vehiclerental.strategy.VehicleSelectionStrategy;

import java.util.List;
import java.util.UUID;

import static com.pavan.vehiclerental.constants.SlotIntervalConstants.SLOT_INTERVAL;

public class BookingService {

    private final PricingStrategy pricingStrategy;
    private final VehicleSelectionStrategy vehicleSelectionStrategy;
    private final BookingManager bookingManager;

    private final SlotsService slotsService;

    public BookingService(final BookingManager bookingManager,
                          final SlotsService slotsService,
                          final VehicleSelectionStrategy vehicleSelectionStrategy,
                          final PricingStrategy pricingStrategy) {
        this.vehicleSelectionStrategy = vehicleSelectionStrategy;
        this.bookingManager = bookingManager;
        this.slotsService = slotsService;
        this.pricingStrategy = pricingStrategy;
    }

    public Double bookVehicle(final String branchId, final String vehicleType, final Integer startTime,
                              final Integer endTime) {

        final VehicleSelectionStrategyResponse vehicleSelectionStrategyResponse = vehicleSelectionStrategy.selectVehicle(
                branchId, vehicleType, startTime, endTime);

        if (vehicleSelectionStrategyResponse.getSelectedVehicles().size() == 0) return (double) -1;
        final List<String> selectedVehicleIds = vehicleSelectionStrategyResponse.getSelectedVehicles();

        Double bookingAmount = pricingStrategy.getPrice(vehicleSelectionStrategyResponse);

        final List<Slot> filteredSlots = slotsService.fetchSlots(branchId, vehicleType, startTime, endTime, SLOT_INTERVAL);
        slotsService.updateVehicleAvailability(filteredSlots, selectedVehicleIds, VehicleStatus.BOOKED);

        final String bookingId = UUID.randomUUID().toString();
        bookingManager.save(Booking.builder()
                .id(bookingId)
                .vehicleIds(selectedVehicleIds)
                .branchId(branchId)
                .price(bookingAmount)
                .status(BookingStatus.CONFIRMED)
                .build());

        return vehicleSelectionStrategyResponse.getTotalAmount();
    }
}
