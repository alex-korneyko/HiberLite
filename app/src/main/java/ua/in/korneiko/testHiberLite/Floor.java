package ua.in.korneiko.testHiberLite;

import ua.in.korneiko.hiberlite.annotations.Column;
import ua.in.korneiko.hiberlite.annotations.Entity;
import ua.in.korneiko.hiberlite.annotations.SearchKey;

@Entity
public class Floor {

    @Column
    @SearchKey
    private String floorNumber;

    @Column
    private int rooms;

    @Column
    private int offices;

    public Floor() {
    }

    public Floor(String floorNumber, int rooms, int offices) {
        this.floorNumber = floorNumber;
        this.rooms = rooms;
        this.offices = offices;
    }

    public String getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(String floorNumber) {
        this.floorNumber = floorNumber;
    }

    public int getRooms() {
        return rooms;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public int getOffices() {
        return offices;
    }

    public void setOffices(int offices) {
        this.offices = offices;
    }
}
