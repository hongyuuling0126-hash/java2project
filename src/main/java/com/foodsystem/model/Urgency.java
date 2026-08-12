package com.foodsystem.model;

/**
 * Urgency represents how urgently a food assistance Request is needed.
 *
 * This maps directly to the three radio buttons Member 3 places on
 * RequestFormPanel ("Low" / "Medium" / "High"). Keeping this as an enum
 * (rather than reading the button label as a raw String) means the
 * AdminPanel can sort or color-code requests by urgency without doing
 * fragile String comparisons like if (label.equals("High")).
 */
public enum Urgency {
    LOW,
    MEDIUM,
    HIGH
}
