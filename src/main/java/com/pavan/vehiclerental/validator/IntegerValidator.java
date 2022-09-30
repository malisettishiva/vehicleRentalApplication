package com.pavan.vehiclerental.validator;

public class IntegerValidator {

    public static boolean isInteger(final String input) {
        try {
            Integer.parseInt(input);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

}
