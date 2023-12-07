package com.example.hotel_app.rooms.models;

import java.io.Serializable;

public class Room implements Serializable {
    private int roomNumber;
    private String kind;
    private int numberOfBeds;
    private int pricePerNight;
    private int roomId;

    public Room(int roomNumber, String kind, int numberOfBeds, int pricePerNight) {
        this.roomNumber = roomNumber;
        this.kind = kind;
        this.numberOfBeds = numberOfBeds;
        this.pricePerNight = pricePerNight;
    }

    public Room() {
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public int getNumberOfBeds() {
        return numberOfBeds;
    }

    public void setNumberOfBeds(int numberOfBeds) {
        this.numberOfBeds = numberOfBeds;
    }

    public int getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(int pricePerNight) {
        this.pricePerNight = pricePerNight;
    }
}
