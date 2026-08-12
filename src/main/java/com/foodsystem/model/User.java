package com.foodsystem.model;

/**
 * User represents one registered account - either an ordinary community
 * member (role = "USER") or an administrator (role = "ADMIN").
 *
 * This class is intentionally a "plain data holder": it has fields,
 * getters, setters, and CSV conversion methods, but NO Swing code and
 * NO file-reading code. That separation is what lets Member 1
 * (auth/dashboard) and Member 4 (storage) both depend on this class
 * without depending on each other's code.
 *
 * CSV layout (users.csv), one line per user, comma-separated:
 *   id,name,email,password,role
 *
 * Example row:
 *   U001,Tan Wei Ling,wl.tan@example.com,pass123,USER
 */
public class User {

    private String id;
    private String name;
    private String email;
    private String password;
    private String role;   // "USER" or "ADMIN"

    /**
     * Standard constructor used when creating a brand new User object
     * in memory (e.g. right after someone fills in the registration form).
     */
    public User(String id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // ----- Getters and setters -----
    // Kept simple on purpose - these are just field accessors, no logic.

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    /**
     * Converts this User object into a single CSV line ready to be
     * written to users.csv. Kept on the model class itself (rather than
     * scattered inside the FileManager) so the "how do I serialize a
     * User" logic lives in exactly one place.
     *
     * NOTE: this is a simple/teaching-level implementation. It does not
     * escape commas inside names or emails - for this project, just make
     * sure user input does not contain commas (the Validator class can
     * enforce that).
     */
    public String toCsvRow() {
        return String.join(",", id, name, email, password, role);
    }

    /**
     * Rebuilds a User object from one CSV line read from users.csv.
     * This is the inverse of toCsvRow().
     *
     * @param csvLine one line of text, e.g. "U001,Tan Wei Ling,wl.tan@example.com,pass123,USER"
     * @return a new User object built from that line
     */
    public static User fromCsvRow(String csvLine) {
        String[] parts = csvLine.split(",", -1); // -1 keeps trailing empty fields
        String id = parts[0];
        String name = parts[1];
        String email = parts[2];
        String password = parts[3];
        String role = parts[4];
        return new User(id, name, email, password, role);
    }

    @Override
    public String toString() {
        // Useful when printing a User to the console while debugging -
        // deliberately leaves out the password.
        return "User{id=" + id + ", name=" + name + ", email=" + email + ", role=" + role + "}";
    }
}
