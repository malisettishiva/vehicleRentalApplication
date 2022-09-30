package com.pavan.vehiclerental.model;

import com.pavan.vehiclerental.exception.InvalidCommandException;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class Command {
    public static final String SPACE = " ";
    private String commandName;
    private List<String> params;

    public Command(String input) {
        List<String> values = new java.util.ArrayList<>(Arrays.stream(input.trim().split(SPACE))
                .map(String::trim)
                .filter(value -> (value.length() > 0)).toList());

        if (values.size() == 0) throw new InvalidCommandException();
        this.commandName = values.get(0);
        values.remove(0);
        this.params = values;
    }

}
