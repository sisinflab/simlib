package it.poliba.sisinflab.simlib.exceptions;

/**
 *
 * @author Corrado Magarelli
 *
 */
public class InvalidDatasetException extends Exception{
    public InvalidDatasetException() { super(); }
    public InvalidDatasetException(String message) { super(message); }
    public InvalidDatasetException(String message, Throwable cause) { super(message, cause); }
    public InvalidDatasetException(Throwable cause) { super(cause); }
}