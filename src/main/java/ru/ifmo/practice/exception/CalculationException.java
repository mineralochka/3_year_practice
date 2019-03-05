package ru.ifmo.practice.exception;

/**
 * Thrown on calculation errors
 *
 * @author r.goroshko
 */
public class CalculationException extends Exception {

    public CalculationException() {
        super();
    }


    public CalculationException(String s, Throwable cause) {
        super(s, cause);
    }
}
