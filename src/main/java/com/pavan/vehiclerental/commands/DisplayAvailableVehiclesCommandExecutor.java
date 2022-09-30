package com.pavan.vehiclerental.commands;

import com.pavan.vehiclerental.service.VehicleRentalService;
import com.pavan.vehiclerental.model.Command;
import com.pavan.vehiclerental.model.Vehicle;
import com.pavan.vehiclerental.utils.OutputPrinter;
import com.pavan.vehiclerental.validator.IntegerValidator;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.StringJoiner;

public class DisplayAvailableVehiclesCommandExecutor extends CommandExecutor {

    public static final String COMMAND_NAME = "DISPLAY_VEHICLES";

    public DisplayAvailableVehiclesCommandExecutor(VehicleRentalService vehicleRentalService, OutputPrinter outputPrinter) {
        super(vehicleRentalService, outputPrinter);
    }

    @Override
    public boolean validate(Command command) {
        final List<String> params = command.getParams();
        if (params.size() != 3) return false;

        return (IntegerValidator.isInteger(params.get(1)) && IntegerValidator.isInteger(params.get(2)));
    }

    @Override
    public void execute(Command command) {
        final List<String> params = command.getParams();
        final String branchId = params.get(0);
        final Integer startTime = Integer.parseInt(params.get(1));
        final Integer endTime = Integer.parseInt(params.get(2));

        final List<Vehicle> vehicles = vehicleRentalService.getVehicleService().getAllAvailableVehicles(branchId, startTime, endTime,
                PageRequest.of(0, 1000, Sort.Direction.ASC, "price"));

        final List<String> vehicleIds = vehicles.stream().map(Vehicle::getId).toList();
        StringJoiner result = new StringJoiner(",");
        for (final String id : vehicleIds) result.add(id);

        outputPrinter.printWithNewLine(String.valueOf(result));
    }
}
