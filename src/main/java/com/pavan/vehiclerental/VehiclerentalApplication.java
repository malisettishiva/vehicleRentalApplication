package com.pavan.vehiclerental;

import com.pavan.vehiclerental.commands.CommandExecutorFactory;
import com.pavan.vehiclerental.exception.InvalidModeException;
import com.pavan.vehiclerental.mode.FileMode;
import com.pavan.vehiclerental.mode.InteractiveMode;
import com.pavan.vehiclerental.service.VehicleRentalService;
import com.pavan.vehiclerental.store.BookingManager;
import com.pavan.vehiclerental.store.BranchManager;
import com.pavan.vehiclerental.store.SlotsManager;
import com.pavan.vehiclerental.store.VehicleManager;
import com.pavan.vehiclerental.strategy.DefaultVehicleSelectionStrategy;
import com.pavan.vehiclerental.strategy.VehicleSelectionStrategy;
import com.pavan.vehiclerental.utils.OutputPrinter;

import java.io.IOException;

public class VehiclerentalApplication {

    private static boolean isFileInputMode(final String[] args) {
        return (args.length == 1);
    }

    private static boolean isInteractiveMode(final String[] args) {
        return (args.length == 0);
    }

    public static void main(String[] args) throws IOException {

        final OutputPrinter outputPrinter = new OutputPrinter();

        final BranchManager branchManager = BranchManager.getInstance();
        final VehicleManager vehicleManager = VehicleManager.getInstance();
        final SlotsManager slotsManager = SlotsManager.getInstance();
        final BookingManager bookingManager = BookingManager.getInstance();

        final VehicleSelectionStrategy vehicleSelectionStrategy = new DefaultVehicleSelectionStrategy(
                slotsManager, vehicleManager);

        final VehicleRentalService vehicleRentalService = new VehicleRentalService(branchManager, vehicleManager,
                slotsManager, bookingManager, vehicleSelectionStrategy);

        final CommandExecutorFactory commandExecutorFactory = new CommandExecutorFactory(vehicleRentalService);

        if (isInteractiveMode(args)) {
            new InteractiveMode(commandExecutorFactory, outputPrinter).process();
        } else if (isFileInputMode(args)) {
            new FileMode(commandExecutorFactory, outputPrinter, args[0]).process();
        } else {
            throw new InvalidModeException();
        }

    }

}
