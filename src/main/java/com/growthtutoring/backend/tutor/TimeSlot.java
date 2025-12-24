package com.growthtutoring.backend.tutor;

/**
 * Represents a single time slot with start and end times
 * Used within the weekly schedule
 */
public class TimeSlot {
    private String start;  // Format: "HH:mm" (24-hour, e.g., "14:00")
    private String end;    // Format: "HH:mm" (24-hour, e.g., "18:00")

    public TimeSlot() {}

    public TimeSlot(String start, String end) {
        this.start = start;
        this.end = end;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    @Override
    public String toString() {
        return start + " - " + end;
    }
}