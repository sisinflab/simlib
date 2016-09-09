package it.poliba.sisinflab.simlib.exceptions;

/**
 *
 * Exception used by {@link it.poliba.sisinflab.simlib.similarity.SimilarityMetric} objects in case the not support a
 * graph type passed as input
 *
 * @author Giorgio Basile
 * @since 1.0
 */
public class InvalidGraphException extends Exception{
    public InvalidGraphException() { super(); }
    public InvalidGraphException(String message) { super(message); }
    public InvalidGraphException(String message, Throwable cause) { super(message, cause); }
    public InvalidGraphException(Throwable cause) { super(cause); }
}
