package com.foodsystem.model;

/**
 * Status represents the current state of a Donation or a Request as it
 * moves through the admin approval workflow.
 *
 * Why an enum instead of a plain String?
 *   - The compiler stops anyone from accidentally saving a typo like
 *     "Aproved" into a record - only these four values are legal.
 *   - Switch statements on a Status are exhaustive and easy to read,
 *     which matters in AdminPanel.java where every action branches on
 *     the current status.
 *
 * Workflow:
 *   PENDING   -> record just submitted by a user, awaiting admin action
 *   APPROVED  -> admin has accepted the donation / request
 *   REJECTED  -> admin has declined it
 *   COMPLETED -> the donation has been collected, or the request has
 *                been fulfilled (optional final state, use if your
 *                group wants a full lifecycle rather than just
 *                approve/reject)
 */
public enum Status {
    PENDING,
    APPROVED,
    REJECTED,
    COMPLETED
}
