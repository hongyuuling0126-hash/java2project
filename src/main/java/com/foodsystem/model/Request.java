package com.foodsystem.model;

/**
 * Request represents one food assistance request submitted through
 * RequestFormPanel (Member 3's module).
 *
 * Structurally this mirrors Donation - both are "records a user
 * submits that an admin later reviews" - but Request has its own class
 * rather than reusing Donation, because a request has different fields
 * (urgency, reason) and a different meaning. Keeping them separate
 * classes also means Member 3 can change Request's fields without
 * risking breaking Member 2's Donation code.
 *
 * CSV layout (requests.csv), one line per request:
 *   id,userId,foodItem,quantity,reason,urgency,dateSubmitted,status
 *
 * Example row:
 *   R001,U002,Rice,5,Lost job this month,HIGH,2026-08-11,PENDING
 */
public class Request {

    private String id;
    private String userId;         // links back to User.id - who is requesting
    private String foodItem;       // what they need, e.g. "Rice"
    private int quantity;          // must be a positive whole number
    private String reason;         // free-text reason for the request
    private Urgency urgency;       // from the LOW/MEDIUM/HIGH radio buttons
    private String dateSubmitted;  // simple String date, e.g. "2026-08-11"
    private Status status;         // PENDING / APPROVED / REJECTED / COMPLETED

    public Request(String id, String userId, String foodItem, int quantity,
                    String reason, Urgency urgency, String dateSubmitted, Status status) {
        this.id = id;
        this.userId = userId;
        this.foodItem = foodItem;
        this.quantity = quantity;
        this.reason = reason;
        this.urgency = urgency;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Urgency getUrgency() {
        return urgency;
    }

    public void setUrgency(Urgency urgency) {
        this.urgency = urgency;
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
     * Converts this Request into one CSV line for requests.csv.
     *
     * IMPORTANT: "reason" is free text typed by the user, so it could
     * contain a comma, which would break this simple CSV format. The
     * Validator class should reject commas in the reason field at
     * input time (see Validator.validateNoCommas) so this stays safe.
     */
    public String toCsvRow() {
        return String.join(",", id, userId, foodItem, String.valueOf(quantity),
                reason, urgency.name(), dateSubmitted, status.name());
    }

    /**
     * Rebuilds a Request object from one CSV line.
     */
    public static Request fromCsvRow(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        String id = parts[0];
        String userId = parts[1];
        String foodItem = parts[2];
        int quantity = Integer.parseInt(parts[3]);
        String reason = parts[4];
        Urgency urgency = Urgency.valueOf(parts[5]);
        String dateSubmitted = parts[6];
        Status status = Status.valueOf(parts[7]);
        return new Request(id, userId, foodItem, quantity, reason, urgency, dateSubmitted, status);
    }

    @Override
    public String toString() {
        return "Request{id=" + id + ", item=" + foodItem + ", urgency=" + urgency
                + ", status=" + status + "}";
    }
}
