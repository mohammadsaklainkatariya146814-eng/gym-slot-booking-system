package com.gymbooking.model;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;

public class GymSlot implements Serializable {
    private static final long serialVersionUID = 1L;

    private int slotId;
    private Date slotDate;
    private Time startTime;
    private Time endTime;
    private int capacity;
    private int availableSlots;

    public GymSlot() {
    }

    public GymSlot(int slotId, Date slotDate, Time startTime, Time endTime, int capacity, int availableSlots) {
        this.slotId = slotId;
        this.slotDate = slotDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.capacity = capacity;
        this.availableSlots = availableSlots;
    }

    public int getSlotId() { return slotId; }
    public void setSlotId(int slotId) { this.slotId = slotId; }

    public Date getSlotDate() { return slotDate; }
    public void setSlotDate(Date slotDate) { this.slotDate = slotDate; }

    public Time getStartTime() { return startTime; }
    public void setStartTime(Time startTime) { this.startTime = startTime; }

    public Time getEndTime() { return endTime; }
    public void setEndTime(Time endTime) { this.endTime = endTime; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getAvailableSlots() { return availableSlots; }
    public void setAvailableSlots(int availableSlots) { this.availableSlots = availableSlots; }

    public int getBookedCount() {
        return Math.max(0, capacity - availableSlots);
    }
}
