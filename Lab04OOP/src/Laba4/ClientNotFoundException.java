package Laba4;

/**
 * Custom exception for handling situations when clients cannot be found in the system.
 * Controls the following situations:
 * 5. Attempt to delete a client without selection
 * 6. Attempt to edit a client without selection  
 * 7. Search operation returns no results
 * 
 * This is a checked exception that requires explicit handling in methods
 * that may encounter missing client scenarios.
 * 
 * @author Mikhail
 * @version 1.0
 * @see Laba4
 */
public class ClientNotFoundException extends Exception {
    
    /**
     * Constructs a new ClientNotFoundException with the specified detail message.
     * 
     * @param message the detail message describing the missing client scenario
     */
    public ClientNotFoundException(String message) {
        super(message);
    }
    
    /**
     * Constructs a new ClientNotFoundException with the specified detail message and cause.
     * 
     * @param message the detail message describing the missing client scenario
     * @param cause the underlying cause of this exception
     */
    public ClientNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}