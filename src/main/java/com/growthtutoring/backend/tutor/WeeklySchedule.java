package com.growthtutoring.backend.tutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a tutor's weekly availability schedule
 * Maps each day of the week to a list of available time slots
 */
public class WeeklySchedule {
    private List<TimeSlot> sunday = new ArrayList<>();
    private List<TimeSlot> monday = new ArrayList<>();
    private List<TimeSlot> tuesday = new ArrayList<>();
    private List<TimeSlot> wednesday = new ArrayList<>();
    private List<TimeSlot> thursday = new ArrayList<>();
    private List<TimeSlot> friday = new ArrayList<>();
    private List<TimeSlot> saturday = new ArrayList<>();

    public WeeklySchedule() {}

    // Getters and Setters
    public List<TimeSlot> getSunday() {
        return sunday;
    }

    public void setSunday(List<TimeSlot> sunday) {
        this.sunday = sunday != null ? sunday : new ArrayList<>();
    }

    public List<TimeSlot> getMonday() {
        return monday;
    }

    public void setMonday(List<TimeSlot> monday) {
        this.monday = monday != null ? monday : new ArrayList<>();
    }

    public List<TimeSlot> getTuesday() {
        return tuesday;
    }

    public void setTuesday(List<TimeSlot> tuesday) {
        this.tuesday = tuesday != null ? tuesday : new ArrayList<>();
    }

    public List<TimeSlot> getWednesday() {
        return wednesday;
    }

    public void setWednesday(List<TimeSlot> wednesday) {
        this.wednesday = wednesday != null ? wednesday : new ArrayList<>();
    }

    public List<TimeSlot> getThursday() {
        return thursday;
    }

    public void setThursday(List<TimeSlot> thursday) {
        this.thursday = thursday != null ? thursday : new ArrayList<>();
    }

    public List<TimeSlot> getFriday() {
        return friday;
    }

    public void setFriday(List<TimeSlot> friday) {
        this.friday = friday != null ? friday : new ArrayList<>();
    }

    public List<TimeSlot> getSaturday() {
        return saturday;
    }

    public void setSaturday(List<TimeSlot> saturday) {
        this.saturday = saturday != null ? saturday : new ArrayList<>();
    }

    /**
     * Get schedule for a specific day
     */
    public List<TimeSlot> getScheduleForDay(String day) {
        switch (day.toLowerCase()) {
            case "sunday": return sunday;
            case "monday": return monday;
            case "tuesday": return tuesday;
            case "wednesday": return wednesday;
            case "thursday": return thursday;
            case "friday": return friday;
            case "saturday": return saturday;
            default: return new ArrayList<>();
        }
    }

    /**
     * Set schedule for a specific day
     */
    public void setScheduleForDay(String day, List<TimeSlot> slots) {
        switch (day.toLowerCase()) {
            case "sunday": setSunday(slots); break;
            case "monday": setMonday(slots); break;
            case "tuesday": setTuesday(slots); break;
            case "wednesday": setWednesday(slots); break;
            case "thursday": setThursday(slots); break;
            case "friday": setFriday(slots); break;
            case "saturday": setSaturday(slots); break;
        }
    }

    /**
     * Convert to Map for JSON serialization
     */
    public Map<String, List<TimeSlot>> toMap() {
        Map<String, List<TimeSlot>> map = new HashMap<>();
        map.put("sunday", sunday);
        map.put("monday", monday);
        map.put("tuesday", tuesday);
        map.put("wednesday", wednesday);
        map.put("thursday", thursday);
        map.put("friday", friday);
        map.put("saturday", saturday);
        return map;
    }
}