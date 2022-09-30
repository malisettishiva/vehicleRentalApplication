package com.pavan.vehiclerental.commands;

import com.pavan.vehiclerental.model.Command;
import com.pavan.vehiclerental.service.VehicleRentalService;
import com.pavan.vehiclerental.utils.OutputPrinter;
import com.pavan.vehiclerental.validator.IntegerValidator;

import java.util.List;

public class BookVehicleCommandExecutor extends CommandExecutor {

    public static final String COMMAND_NAME = "BOOK";

    public BookVehicleCommandExecutor(VehicleRentalService vehicleRentalService, OutputPrinter outputPrinter) {
        super(vehicleRentalService, outputPrinter);
    }

    @Override
    public boolean validate(Command command) {
        final List<String> params = command.getParams();
        if (params.size() != 4) return false;

        return (IntegerValidator.isInteger(params.get(2)) && IntegerValidator.isInteger(params.get(3)));
    }

    @Override
    public void execute(Command command) {
        final List<String> params = command.getParams();
        final String branchId = params.get(0);
        final String vehicleType = params.get(1);
        final Integer startTime = Integer.parseInt(params.get(2));
        final Integer endTime = Integer.parseInt(params.get(3));
        Double totalAmount = vehicleRentalService.getBookingService().bookVehicle(branchId, vehicleType, startTime, endTime);
        outputPrinter.printWithNewLine(String.valueOf(totalAmount.intValue()));
    }
}
