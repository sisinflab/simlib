package it.poliba.sisinflab.simlib.exceptions;

/**
 * Exception thrown by {@link it.poliba.sisinflab.simlib.input.StatementFileReader} if the input file contains
 * not valid statements
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class InvalidStatementException extends RuntimeException{
    public InvalidStatementException() { super(); }
    public InvalidStatementException(String message) { super(message); }
    public InvalidStatementException(String message, Throwable cause) { super(message, cause); }
    public InvalidStatementException(Throwable cause) { super(cause); }
}
