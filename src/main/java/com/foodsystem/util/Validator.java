package com.foodsystem.util;

import com.foodsystem.exception.EmptyFieldException;
import com.foodsystem.exception.InvalidInputException;

/**
 * Validator centralizes all input-validation rules so that every
 * screen's submit button calls the same small set of methods instead
 * of each member reinventing (and slightly-differently-implementing)
 * "is this field empty" checks.
 *
 * All methods are "static" - Validator is never instantiated
 * (new Validator() is never called). Think of this class as a toolbox
 * of reusable functions, grouped together under one name.
 *
 * Every method here either:
 *   (a) returns quietly / returns a parsed value, meaning the input is OK, or
 *   (b) throws a checked exception describing exactly what was wrong.
 *
 * This is the "exception handling and input validation" rubric
 * criterion (CLO2) made concrete: the exceptions here are the ones the
 * View/Controller code in each screen will catch and turn into
 * JOptionPane pop-up warnings.
 */
public final class Validator {

    // Private constructor: prevents anyone from accidentally writing
    // "new Validator()" - this class is only ever used via its static
    // methods, e.g. Validator.validateNotEmpty(...).
    private Validator() {
    }

    /**
     * Checks that a text field is not empty and not just whitespace.
     *
     * @param value     the raw text from a JTextField, e.g. nameField.getText()
     * @param fieldName a human-readable name used in the error message,
     *                  e.g. "Food item" - this is what the user actually
     *                  sees in the pop-up, so keep it friendly
     * @throws EmptyFieldException if value is null, "", or only spaces
     */
    public static void validateNotEmpty(String value, String fieldName) throws EmptyFieldException {
        if (value == null || value.trim().isEmpty()) {
            throw new EmptyFieldException(fieldName + " cannot be empty.");
        }
    }

    /**
     * Parses a text field into a positive whole number (e.g. a donation
     * or request quantity). Handles two separate failure cases with two
     * different clear messages:
     *   1. The text isn't a number at all (user typed "ten" or "-").
     *   2. The text IS a number, but it's zero or negative, which makes
     *      no sense for a quantity of food items.
     *
     * @param value     raw text from the quantity field
     * @param fieldName human-readable field name for the error message
     * @return the parsed positive integer, ready to store on the model object
     * @throws InvalidInputException if the text is not a valid positive integer
     */
    public static int parsePositiveInt(String value, String fieldName) throws InvalidInputException {
        int parsed;
        try {
            parsed = Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            // Integer.parseInt throws its own unchecked exception on bad
            // input (e.g. "abc") - we catch that low-level exception and
            // re-throw our own project-specific, checked exception with
            // a message meant for the end user, not a Java stack trace.
            throw new InvalidInputException(fieldName + " must be a whole number.");
        }

        if (parsed <= 0) {
            throw new InvalidInputException(fieldName + " must be greater than zero.");
        }

        return parsed;
    }

    /**
     * Rejects commas inside free-text fields (like a Request's "reason"
     * field). This project's CSV storage uses a comma as the column
     * separator, so a stray comma typed by a user would silently shift
     * every column after it when the file is read back - this check
     * stops that corruption at the source instead of debugging it later
     * in the storage layer.
     *
     * @param value     raw text from a free-text field
     * @param fieldName human-readable field name for the error message
     * @throws InvalidInputException if value contains a comma
     */
    public static void validateNoCommas(String value, String fieldName) throws InvalidInputException {
        if (value != null && value.contains(",")) {
            throw new InvalidInputException(fieldName + " cannot contain a comma (,).");
        }
    }

    /**
     * Very simple email format check - looks for one "@" with at least
     * one character before and after it. This is intentionally basic
     * (not a full RFC-compliant email regex) since the goal is catching
     * obvious typos during registration, not perfect validation.
     *
     * @param email raw text from the email field
     * @throws InvalidInputException if the text doesn't look like an email
     */
    public static void validateEmailFormat(String email) throws InvalidInputException {
        if (email == null || !email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            throw new InvalidInputException("Please enter a valid email address.");
        }
    }
}
