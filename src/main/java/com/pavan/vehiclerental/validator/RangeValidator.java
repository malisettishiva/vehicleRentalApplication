package com.pavan.vehiclerental.validator;

public class RangeValidator {
    public static boolean isValid(final Integer start, final Integer end) {
        return (start < end);
    }
}
