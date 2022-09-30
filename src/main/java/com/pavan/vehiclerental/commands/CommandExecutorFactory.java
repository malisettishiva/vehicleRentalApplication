package com.pavan.vehiclerental.commands;

import com.pavan.vehiclerental.exception.InvalidCommandException;
import com.pavan.vehiclerental.service.VehicleRentalService;
import com.pavan.vehiclerental.model.Command;
import com.pavan.vehiclerental.utils.OutputPrinter;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CommandExecutorFactory {
    private final Map<String, CommandExecutor> commands = new HashMap<>();

    public CommandExecutorFactory(final VehicleRentalService vehicleRentalService) {
        final OutputPrinter outputPrinter = new OutputPrinter();
        commands.put(OnboardBranchCommandExecutor.COMMAND_NAME, new OnboardBranchCommandExecutor(
                vehicleRentalService, outputPrinter));
        commands.put(OnboardVehicleCommandExecutor.COMMAND_NAME, new OnboardVehicleCommandExecutor(
                vehicleRentalService, outputPrinter));
        commands.put(BookVehicleCommandExecutor.COMMAND_NAME, new BookVehicleCommandExecutor(
                vehicleRentalService, outputPrinter));
        commands.put(DisplayAvailableVehiclesCommandExecutor.COMMAND_NAME, new DisplayAvailableVehiclesCommandExecutor(
                vehicleRentalService, outputPrinter));
    }

    public CommandExecutor getCommandExecutor(final Command command) {
        return Optional.ofNullable(commands.get(command.getCommandName()))
                .orElseThrow(InvalidCommandException::new);
    }
}
