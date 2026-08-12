package com.foodsystem.storage;

import com.foodsystem.exception.RecordNotFoundException;
import com.foodsystem.model.Request;
import com.foodsystem.model.Status;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * RequestFileManager is the only class that reads or writes
 * requests.csv. Deliberately mirrors DonationFileManager's structure
 * (loadAll / findByUserId / save / updateStatus / delete) so anyone
 * comfortable with one class already understands the other - the two
 * modules should feel like siblings, not two different designs.
 *
 * File format: one Request per line, comma-separated
 *   id,userId,foodItem,quantity,reason,urgency,dateSubmitted,status
 */
public class RequestFileManager {

    private static final String FILE_PATH = "data/requests.csv";

    public RequestFileManager() {
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
            System.err.println("Could not create requests.csv: " + e.getMessage());
        }
    }

    /**
     * Reads every request from the CSV file.
     *
     * @return all requests in the system, in file order; an empty list
     *         (never null) if there are none yet.
     */
    public List<Request> loadAll() {
        List<Request> requests = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    requests.add(Request.fromCsvRow(line));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading requests.csv: " + e.getMessage());
        }

        return requests;
    }

    /**
     * Returns only the requests submitted by one particular user - this
     * is what feeds the JTable of "my submitted records" that Member 3's
     * WBS entry specifically calls for.
     */
    public List<Request> findByUserId(String userId) {
        List<Request> result = new ArrayList<>();
        for (Request r : loadAll()) {
            if (r.getUserId().equals(userId)) {
                result.add(r);
            }
        }
        return result;
    }

    /**
     * Appends one new Request to the CSV file - called right after
     * RequestFormPanel validates the form input successfully.
     */
    public void save(Request request) {
        try (FileWriter writer = new FileWriter(FILE_PATH, true)) { // append mode
            writer.write(request.toCsvRow());
            writer.write(System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Error writing to requests.csv: " + e.getMessage());
        }
    }

    /**
     * Changes the status of one existing request (e.g. PENDING ->
     * APPROVED), the same "read everything, update in memory, write
     * everything back" pattern used in DonationFileManager - see the
     * comments there for the full explanation of why CSV files need
     * this approach.
     *
     * @throws RecordNotFoundException if no request with that id exists
     */
    public void updateStatus(String id, Status newStatus) throws RecordNotFoundException {
        List<Request> all = loadAll();
        boolean found = false;

        for (Request r : all) {
            if (r.getId().equals(id)) {
                r.setStatus(newStatus);
                found = true;
                break;
            }
        }

        if (!found) {
            throw new RecordNotFoundException("No request found with id " + id);
        }

        rewriteAll(all);
    }

    /**
     * Removes one request entirely.
     *
     * @throws RecordNotFoundException if no request with that id exists
     */
    public void delete(String id) throws RecordNotFoundException {
        List<Request> all = loadAll();
        List<Request> remaining = new ArrayList<>();
        boolean found = false;

        for (Request r : all) {
            if (r.getId().equals(id)) {
                found = true;
            } else {
                remaining.add(r);
            }
        }

        if (!found) {
            throw new RecordNotFoundException("No request found with id " + id);
        }

        rewriteAll(remaining);
    }

    /**
     * Overwrites requests.csv with exactly the list given.
     */
    private void rewriteAll(List<Request> requests) {
        try (FileWriter writer = new FileWriter(FILE_PATH, false)) { // false = overwrite
            for (Request r : requests) {
                writer.write(r.toCsvRow());
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Error rewriting requests.csv: " + e.getMessage());
        }
    }
}
