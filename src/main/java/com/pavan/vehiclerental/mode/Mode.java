package com.pavan.vehiclerental.mode;

import com.pavan.vehiclerental.commands.CommandExecutor;
import com.pavan.vehiclerental.commands.CommandExecutorFactory;
import com.pavan.vehiclerental.exception.InvalidCommandException;
import com.pavan.vehiclerental.model.Command;
import com.pavan.vehiclerental.utils.OutputPrinter;

import java.io.IOException;

public abstract class Mode {
    protected final CommandExecutorFactory commandExecutorFactory;
    protected final OutputPrinter outputPrinter;

    public Mode(final CommandExecutorFactory commandExecutorFactory, final OutputPrinter outputPrinter) {
        this.commandExecutorFactory = commandExecutorFactory;
        this.outputPrinter = outputPrinter;
    }

    protected void processCommand(final Command command) {
        final CommandExecutor commandExecutor = commandExecutorFactory.getCommandExecutor(command);

        if (commandExecutor.validate(command)) {
            commandExecutor.execute(command);
        } else {
            throw new InvalidCommandException();
        }

    }

    public abstract void process() throws IOException;
}
