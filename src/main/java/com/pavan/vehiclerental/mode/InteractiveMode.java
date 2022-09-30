package com.pavan.vehiclerental.mode;

import com.pavan.vehiclerental.commands.CommandExecutorFactory;
import com.pavan.vehiclerental.model.Command;
import com.pavan.vehiclerental.utils.OutputPrinter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InteractiveMode extends Mode {

    private static final String EXIT = "EXIT";

    public InteractiveMode(CommandExecutorFactory commandExecutorFactory, OutputPrinter outputPrinter) {
        super(commandExecutorFactory, outputPrinter);
    }

    @Override
    public void process() throws IOException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            final String input = bufferedReader.readLine();
            final Command command = new Command(input);
            if (command.getCommandName().equals(EXIT)) {
                break;
            }
            processCommand(command);
        }
    }
}
