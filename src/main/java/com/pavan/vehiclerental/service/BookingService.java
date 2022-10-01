package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.enums.BookingStatus;
import com.pavan.vehiclerental.enums.VehicleStatus;
import com.pavan.vehiclerental.model.Booking;
import com.pavan.vehiclerental.model.Slot;
import com.pavan.vehiclerental.model.VehicleSelectionStrategyResponse;
import com.pavan.vehiclerental.store.BookingManager;
import com.pavan.vehiclerental.strategy.PricingStrategy;
import com.pavan.vehiclerental.strategy.VehicleSelectionStrategy;
import com.pavan.vehiclerental.utils.UUIDGeneratorUtils;

import java.util.List;

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

        final List<Slot> filteredSlots = slotsService.fetchSlots(branchId, vehicleType, startTime, endTime);
        slotsService.updateVehicleAvailability(filteredSlots, selectedVehicleIds, VehicleStatus.BOOKED);

        Double bookingAmount = pricingStrategy.getPrice(vehicleSelectionStrategyResponse);

        final String bookingId = UUIDGeneratorUtils.getUUID();
        bookingManager.save(Booking.builder()
                .id(bookingId)
                .vehicleIds(selectedVehicleIds)
                .startTime(startTime)
                .endTime(endTime)
                .price(bookingAmount)
                .status(BookingStatus.CONFIRMED)
                .build());

        return vehicleSelectionStrategyResponse.getTotalAmount();
    }
}
