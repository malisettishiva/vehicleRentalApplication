package com.pavan.vehiclerental.mode;

import com.pavan.vehiclerental.commands.CommandExecutorFactory;
import com.pavan.vehiclerental.model.Command;
import com.pavan.vehiclerental.utils.OutputPrinter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class FileMode extends Mode {

    private final String fileName;

    public FileMode(CommandExecutorFactory commandExecutorFactory, OutputPrinter outputPrinter,
                    String fileName) {
        super(commandExecutorFactory, outputPrinter);
        this.fileName = fileName;
    }

    @Override
    public void process() throws IOException {
        final BufferedReader bufferedReader;
        try {
            bufferedReader = new BufferedReader(new FileReader(fileName));
        } catch (Exception e) {
            outputPrinter.invalidFile();
            return;
        }

        String input = bufferedReader.readLine();
        while (input != null) {
            final Command command = new Command(input);
            processCommand(command);
            input = bufferedReader.readLine();
        }
    }
}
