package com.foodsystem.storage;

import com.foodsystem.exception.DuplicateRecordException;
import com.foodsystem.exception.RecordNotFoundException;
import com.foodsystem.model.User;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * UserFileManager is the ONLY class in the project that reads or writes
 * users.csv. Every other class - LoginFrame, RegisterFrame, AdminPanel,
 * anything - goes through the methods below instead of opening the
 * file itself.
 *
 * Why funnel everything through one class?
 *   - If the group later decides to switch from CSV files to a real
 *     database, only this one class needs to change; every screen that
 *     calls loadAll()/save()/etc keeps working unmodified.
 *   - It also directly satisfies the "Storage & admin: unified reading
 *     and saving of all data" requirement from the WBS - there is
 *     exactly one place data enters and leaves the system.
 *
 * File format: one User per line, comma-separated
 *   id,name,email,password,role
 */
public class UserFileManager {

    // Relative path to the CSV file. Using a relative path means the
    // file appears inside a "data" folder next to wherever the project
    // is run from - make sure this folder exists (or let the
    // ensureFileExists() method below create it).
    private static final String FILE_PATH = "data/users.csv";

    /**
     * Constructor - makes sure the data folder and the CSV file both
     * exist before anything tries to read from or write to them, so
     * the very first run of the program doesn't crash with a
     * FileNotFoundException.
     */
    public UserFileManager() {
        ensureFileExists();
    }

    private void ensureFileExists() {
        File file = new File(FILE_PATH);
        File parentFolder = file.getParentFile();
        try {
            if (parentFolder != null && !parentFolder.exists()) {
                parentFolder.mkdirs(); // creates the "data" folder if missing
            }
            if (!file.exists()) {
                file.createNewFile(); // creates an empty users.csv
            }
        } catch (IOException e) {
            // At startup there is no GUI window yet to show a dialog on,
            // so we print to the console. Whoever wires up Main.java
            // should wrap application startup in a try/catch that shows
            // a JOptionPane if this ever happens on a teammate's machine.
            System.err.println("Could not create users.csv: " + e.getMessage());
        }
    }

    /**
     * Reads every line of users.csv and converts it into a User object,
     * using User.fromCsvRow(...) (see User.java) to do the actual
     * parsing of each line.
     *
     * @return a list of every registered User, in file order.
     *         Returns an empty list (not null) if the file has no
     *         users yet - this means calling code never needs a null
     *         check, only an isEmpty() check if it cares.
     */
    public List<User> loadAll() {
        List<User> users = new ArrayList<>();

        // try-with-resources: BufferedReader is automatically closed
        // when the try block finishes, even if an exception happens -
        // this avoids leaving the file "locked" by our own program.
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    users.add(User.fromCsvRow(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users.csv: " + e.getMessage());
        }

        return users;
    }

    /**
     * Appends a brand new User to users.csv - used by RegisterFrame.
     *
     * Checks for a duplicate email FIRST, before writing anything, so
     * we never end up with two accounts sharing one email address.
     *
     * @param newUser the User to save
     * @throws DuplicateRecordException if a user with this email already exists
     */
    public void save(User newUser) throws DuplicateRecordException {
        for (User existing : loadAll()) {
            if (existing.getEmail().equalsIgnoreCase(newUser.getEmail())) {
                throw new DuplicateRecordException(
                        "An account with the email \"" + newUser.getEmail() + "\" already exists.");
            }
        }

        // "true" here means "append mode" - new lines are added to the
        // end of the file instead of overwriting everything that's
        // already there.
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
            writer.write(newUser.toCsvRow());
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing to users.csv: " + e.getMessage());
        }
    }

    /**
     * Looks up a single user by their email + password, used by
     * LoginFrame to check credentials.
     *
     * @return the matching User, or null if no user matches both the
     *         email and password given. Returning null (rather than
     *         throwing) is intentional here - "wrong password" is an
     *         expected, everyday outcome of a login attempt, not an
     *         exceptional situation, so LoginFrame just checks
     *         "if (user == null)" and shows "Invalid email or password."
     */
    public User findByCredentials(String email, String password) {
        for (User user : loadAll()) {
            if (user.getEmail().equalsIgnoreCase(email) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Looks up a single user by id. Unlike findByCredentials, a missing
     * id here usually means something has gone wrong elsewhere in the
     * program (e.g. a Donation references a userId that no longer
     * exists) - so this throws a checked exception instead of quietly
     * returning null, forcing the caller to notice and handle it.
     *
     * @throws RecordNotFoundException if no user with this id exists
     */
    public User findById(String id) throws RecordNotFoundException {
        for (User user : loadAll()) {
            if (user.getId().equals(id)) {
                return user;
            }
        }
        throw new RecordNotFoundException("No user found with id " + id);
    }

    /**
     * Rewrites the entire users.csv file from a given list of users.
     * This is a small private helper used internally whenever a method
     * needs to update or delete a user, since CSV files don't support
     * editing a single line "in place" the way a database table would -
     * the simplest correct approach is: read everything, change the
     * one record we care about in memory, then write everything back.
     */
    private void rewriteAll(List<User> users) {
        try (FileWriter writer = new FileWriter(FILE_PATH, false)) { // false = overwrite
            for (User user : users) {
                writer.write(user.toCsvRow());
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Error rewriting users.csv: " + e.getMessage());
        }
    }
}
