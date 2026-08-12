package com.foodsystem.storage;

import com.foodsystem.exception.RecordNotFoundException;
import com.foodsystem.model.Donation;
import com.foodsystem.model.Status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * DonationFileManager is the only class that reads or writes
 * donations.csv. Member 2's DonationFormPanel calls save(...) when the
 * user submits a donation; Member 4's AdminPanel calls loadAll() and
 * updateStatus(...) to review and approve donations. Neither of those
 * two screens needs to know the file even exists - they only see
 * Donation objects and method calls.
 *
 * File format: one Donation per line, comma-separated
 *   id,userId,foodItem,category,quantity,dateSubmitted,status
 */
public class DonationFileManager {

    private static final String FILE_PATH = "data/donations.csv";

    public DonationFileManager() {
        ensureFileExists();
    }

    private void ensureFileExists() {
        File file = new File(FILE_PATH);
        File parentFolder = file.getParentFile();
        try {
            if (parentFolder != null && !parentFolder.exists()) {
                parentFolder.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Could not create donations.csv: " + e.getMessage());
        }
    }

    /**
     * Reads every donation from the CSV file.
     *
     * @return all donations in the system, in file order; an empty list
     *         (never null) if there are none yet.
     */
    public List<Donation> loadAll() {
        List<Donation> donations = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    donations.add(Donation.fromCsvRow(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading donations.csv: " + e.getMessage());
        }

        return donations;
    }

    /**
     * Returns only the donations submitted by one particular user - used
     * by the "my donation history" view inside DonationFormPanel, so a
     * regular user sees their own submissions, not everyone else's.
     *
     * This filters loadAll() in memory rather than doing anything
     * clever with the file itself - CSV files are small and simple
     * enough in this project that "read everything, then filter" is
     * both the easiest and fast-enough approach.
     */
    public List<Donation> findByUserId(String userId) {
        List<Donation> result = new ArrayList<>();
        for (Donation d : loadAll()) {
            if (d.getUserId().equals(userId)) {
                result.add(d);
            }
        }
        return result;
    }

    /**
     * Appends one new Donation to the CSV file - called right after
     * DonationFormPanel validates the form input successfully.
     */
    public void save(Donation donation) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) { // append mode
            writer.write(donation.toCsvRow());
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing to donations.csv: " + e.getMessage());
        }
    }

    /**
     * Changes the status of one existing donation (e.g. PENDING ->
     * APPROVED) - this is the core action of AdminPanel's approve /
     * reject buttons.
     *
     * Because a CSV file can't be edited in one spot in place, this
     * method reads every donation into memory, finds the one with a
     * matching id, changes its status field, and writes the WHOLE list
     * back out to the file, overwriting the old contents.
     *
     * @param id        the Donation's id to update
     * @param newStatus the new Status to set
     * @throws RecordNotFoundException if no donation with that id exists
     */
    public void updateStatus(String id, Status newStatus) throws RecordNotFoundException {
        List<Donation> all = loadAll();
        boolean found = false;

        for (Donation d : all) {
            if (d.getId().equals(id)) {
                d.setStatus(newStatus);
                found = true;
                break; // stop looping once we've found and updated it
            }
        }

        if (!found) {
            throw new RecordNotFoundException("No donation found with id " + id);
        }

        rewriteAll(all);
    }

    /**
     * Removes one donation entirely - reads everything in, keeps only
     * the records that do NOT match the given id, then writes the
     * remaining records back to the file.
     *
     * @throws RecordNotFoundException if no donation with that id exists
     */
    public void delete(String id) throws RecordNotFoundException {
        List<Donation> all = loadAll();
        List<Donation> remaining = new ArrayList<>();
        boolean found = false;

        for (Donation d : all) {
            if (d.getId().equals(id)) {
                found = true; // skip adding this one to "remaining" - this deletes it
            } else {
                remaining.add(d);
            }
        }

        if (!found) {
            throw new RecordNotFoundException("No donation found with id " + id);
        }

        rewriteAll(remaining);
    }

    /**
     * Overwrites donations.csv with exactly the list given. Private
     * helper shared by updateStatus(...) and delete(...) above, so the
     * "rewrite the whole file" logic exists in only one place.
     */
    private void rewriteAll(List<Donation> donations) {
        try (FileWriter writer = new FileWriter(FILE_PATH, false)) { // false = overwrite
            for (Donation d : donations) {
                writer.write(d.toCsvRow());
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Error rewriting donations.csv: " + e.getMessage());
        }
    }
}
