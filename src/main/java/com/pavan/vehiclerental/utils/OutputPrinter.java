package com.pavan.vehiclerental.utils;

public class OutputPrinter {

    public void welcome() {
        printWithNewLine("Welcome to vehicle booking service");
    }

    public void invalidFile() {
        printWithNewLine("Given file is invalid");
    }

    public void printWithNewLine(final String message) {
        System.out.println(message);
    }
}
