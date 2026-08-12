package com.foodsystem.exception;

/**
 * Thrown when trying to create a record that should be unique but
 * already exists - the main case in this project is registering a new
 * User with an email address that is already in users.csv.
 *
 * Used inside UserFileManager.save(User), which should first search
 * the existing users for a matching email and throw this instead of
 * silently creating a duplicate account:
 *
 *   for (User existing : loadAll()) {
 *       if (existing.getEmail().equalsIgnoreCase(newUser.getEmail())) {
 *           throw new DuplicateRecordException(
 *               "An account with this email already exists.");
 *       }
 *   }
 */
public class DuplicateRecordException extends Exception {

    public DuplicateRecordException(String message) {
        super(message);
    }
}
