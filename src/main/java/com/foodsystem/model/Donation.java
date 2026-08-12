package com.foodsystem.model;

/**
 * Donation represents one food donation submitted through
 * DonationFormPanel (Member 2's module).
 *
 * Like User, this is a plain data class - Member 2's GUI code builds a
 * Donation object from form input, hands it to DonationFileManager to
 * save, and later reads Donation objects back out to display in a
 * table. Neither Member 2's panel nor Member 4's AdminPanel needs to
 * know how the other one works internally - they only share this class.
 *
 * CSV layout (donations.csv), one line per donation:
 *   id,userId,foodItem,category,quantity,dateSubmitted,status
 *
 * Example row:
 *   D001,U001,Canned Beans,Canned Goods,10,2026-08-11,PENDING
 */
public class Donation {

    private String id;
    private String userId;         // links back to User.id - who donated
    private String foodItem;       // e.g. "Canned Beans"
    private String category;       // from the category combo box, e.g. "Canned Goods"
    private int quantity;          // must be a positive whole number
    private String dateSubmitted;  // simple String date, e.g. "2026-08-11", to keep this teaching example simple
    private Status status;         // PENDING / APPROVED / REJECTED / COMPLETED

    public Donation(String id, String userId, String foodItem, String category,
                     int quantity, String dateSubmitted, Status status) {
        this.id = id;
        this.userId = userId;
        this.foodItem = foodItem;
        this.category = category;
        this.quantity = quantity;
        this.dateSubmitted = dateSubmitted;
        this.status = status;
    }

    // ----- Getters and setters -----

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(String foodItem) {
        this.foodItem = foodItem;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(String dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Converts this Donation into one CSV line for donations.csv.
     * status.name() turns the enum into its plain text form, e.g.
     * Status.PENDING -> "PENDING".
     */
    public String toCsvRow() {
        return String.join(",", id, userId, foodItem, category,
                String.valueOf(quantity), dateSubmitted, status.name());
    }

    /**
     * Rebuilds a Donation object from one CSV line.
     * Integer.parseInt and Status.valueOf are the reverse of
     * String.valueOf(quantity) and status.name() above.
     */
    public static Donation fromCsvRow(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        String id = parts[0];
        String userId = parts[1];
        String foodItem = parts[2];
        String category = parts[3];
        int quantity = Integer.parseInt(parts[4]);
        String dateSubmitted = parts[5];
        Status status = Status.valueOf(parts[6]);
        return new Donation(id, userId, foodItem, category, quantity, dateSubmitted, status);
    }

    @Override
    public String toString() {
        return "Donation{id=" + id + ", item=" + foodItem + ", qty=" + quantity
                + ", status=" + status + "}";
    }
}
