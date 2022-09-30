package com.pavan.vehiclerental.commands;

import com.pavan.vehiclerental.model.Command;
import com.pavan.vehiclerental.service.VehicleRentalService;
import com.pavan.vehiclerental.utils.OutputPrinter;

import java.util.Arrays;
import java.util.List;

import static com.pavan.vehiclerental.constants.BooleanConstants.FALSE;
import static com.pavan.vehiclerental.constants.BooleanConstants.TRUE;

public class OnboardBranchCommandExecutor extends CommandExecutor {

    public static final String COMMAND_NAME = "ADD_BRANCH";

    public OnboardBranchCommandExecutor(VehicleRentalService vehicleRentalService, OutputPrinter outputPrinter) {
        super(vehicleRentalService, outputPrinter);
    }


    @Override
    public boolean validate(Command command) {
        final List<String> params = command.getParams();
        return (params.size() == 2);
    }

    @Override
    public void execute(Command command) {
        final List<String> params = command.getParams();
        final String branchName = params.get(0);
        final String vehicleTypes = params.get(1);
        final List<String> vehicleTypesList = Arrays.stream(vehicleTypes.split(",")).toList();

        if (vehicleRentalService.getBranchService().addBranch(branchName, vehicleTypesList)) {
            outputPrinter.printWithNewLine(TRUE);
        } else {
            outputPrinter.printWithNewLine(FALSE);
        }
    }
}
