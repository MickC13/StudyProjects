package Laba4;

/**
 * Custom exception for handling data validation errors in the application.
 * Controls the following situations:
 * 1. Empty fields when adding/editing clients
 * 2. Invalid client name format (contains digits or special characters)
 * 3. Invalid phone number format (not matching XXX-XX-XX pattern)
 * 4. Empty search query
 * 
 * This is a checked exception that must be either handled or declared in method signatures.
 * 
 * @author Mikhail
 * @version 1.0
 * @see Laba4
 */
public class MyException extends Exception {
    
    /**
     * Constructs a new MyException with the specified detail message.
     * 
     * @param message the detail message describing the validation error
     */
    public MyException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new MyException with the specified detail message and cause.
     * 
     * @param message the detail message describing the validation error
     * @param cause the underlying cause of this exception
     */
    public MyException(String message, Throwable cause) {
        super(message, cause);
    }
}