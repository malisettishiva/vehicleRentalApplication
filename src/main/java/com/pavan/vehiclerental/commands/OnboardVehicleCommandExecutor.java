package com.pavan.vehiclerental.commands;

import com.pavan.vehiclerental.model.Command;
import com.pavan.vehiclerental.service.VehicleRentalService;
import com.pavan.vehiclerental.utils.OutputPrinter;
import com.pavan.vehiclerental.validator.IntegerValidator;

import java.util.List;

import static com.pavan.vehiclerental.constants.BooleanConstants.FALSE;
import static com.pavan.vehiclerental.constants.BooleanConstants.TRUE;

public class OnboardVehicleCommandExecutor extends CommandExecutor {

    public static final String COMMAND_NAME = "ADD_VEHICLE";

    public OnboardVehicleCommandExecutor(VehicleRentalService vehicleRentalService, OutputPrinter outputPrinter) {
        super(vehicleRentalService, outputPrinter);
    }

    @Override
    public boolean validate(Command command) {
        final List<String> params = command.getParams();
        if (params.size() != 4) {
            return false;
        }
        return IntegerValidator.isInteger(params.get(3));
    }

    @Override
    public void execute(Command command) {
        final List<String> params = command.getParams();
        final String branchName = params.get(0);
        final String vehicleType = params.get(1);
        final String vehicleId = params.get(2);
        final Integer price = Integer.parseInt(params.get(3));

        if (vehicleRentalService.onboardVehicle(branchName, vehicleType, vehicleId, Double.valueOf(price))) {
            outputPrinter.printWithNewLine(TRUE);
        } else {
            outputPrinter.printWithNewLine(FALSE);
        }
    }
}
