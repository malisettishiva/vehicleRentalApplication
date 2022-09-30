package com.pavan.vehiclerental.commands;

import com.pavan.vehiclerental.model.Command;
import com.pavan.vehiclerental.service.VehicleRentalService;
import com.pavan.vehiclerental.utils.OutputPrinter;

public abstract class CommandExecutor {

    protected final VehicleRentalService vehicleRentalService;
    protected final OutputPrinter outputPrinter;

    public CommandExecutor(final VehicleRentalService vehicleRentalService, final OutputPrinter outputPrinter) {
        this.vehicleRentalService = vehicleRentalService;
        this.outputPrinter = outputPrinter;
    }

    public abstract boolean validate(final Command command);

    public abstract void execute(final Command command);
}
