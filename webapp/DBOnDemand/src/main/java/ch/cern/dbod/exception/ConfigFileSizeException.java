package ch.cern.dbod.exception;

/**
 * Exception thrown when config file is bigger than allowed.
 * @author Daniel Gomez Blanco
 */

public class ConfigFileSizeException extends Exception{

    /**
     * Empty constructor.
     */
    public ConfigFileSizeException() {}

    /**
     * Constructor with a message.
     * @param message message for the exception.
     */
    public ConfigFileSizeException(String message) {
        super(message);
    }
    
    /**
     * Constructor with a throwable cause.
     * @param cause cause for the exception.
     */
    public ConfigFileSizeException(Throwable cause) {
        super(cause);
    }
}
