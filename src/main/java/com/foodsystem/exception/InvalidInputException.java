package com.foodsystem.exception;

/**
 * Thrown when a field contains text of the wrong shape - for example:
 *   - a quantity field that isn't a whole number ("abc" instead of "10")
 *   - a quantity that is zero or negative
 *   - a reason/name field containing a comma, which would corrupt the
 *     CSV file format used by the storage layer
 *
 * Kept separate from EmptyFieldException so the two failure modes
 * ("nothing was typed" vs "something was typed, but it's not valid")
 * can be given different, clearer messages to the user - and so a
 * Controller can catch just one type if it ever needs to react to them
 * differently.
 */
public class InvalidInputException extends Exception {

    public InvalidInputException(String message) {
        super(message);
    }
}
