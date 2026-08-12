package com.foodsystem.exception;

/**
 * Thrown when code tries to look up, update, or delete a record by id
 * (a Donation, a Request, or a User) and no record with that id exists
 * in the CSV file - for example, if AdminPanel tries to update the
 * status of a donation that was already deleted.
 *
 * This mainly protects the *FileManager update/delete methods from
 * silently doing nothing when given a bad id - instead they throw,
 * so the Controller can show the user a clear message such as
 * "This record no longer exists - please refresh the list."
 */
public class RecordNotFoundException extends Exception {

    public RecordNotFoundException(String message) {
        super(message);
    }
}
