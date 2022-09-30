package com.pavan.vehiclerental.service;

import com.pavan.vehiclerental.store.BookingManager;
import com.pavan.vehiclerental.store.BranchManager;
import com.pavan.vehiclerental.store.SlotsManager;
import com.pavan.vehiclerental.store.VehicleManager;
import com.pavan.vehiclerental.strategy.VehicleSelectionStrategy;
import lombok.Data;

@Data
public class VehicleRentalService {

    private final BranchService branchService;
    private final VehicleService vehicleService;
    private final BookingService bookingService;

    public VehicleRentalService(final BranchManager branchManager, final VehicleManager vehicleManager,
                                final SlotsManager slotsManager, final BookingManager bookingManager,
                                final VehicleSelectionStrategy vehicleSelectionStrategy) {

        this.branchService = new BranchService(branchManager, slotsManager);
        this.vehicleService = new VehicleService(vehicleManager, branchManager, slotsManager);
        this.bookingService = new BookingService(vehicleSelectionStrategy, slotsManager,
                bookingManager, branchManager);
    }
}
