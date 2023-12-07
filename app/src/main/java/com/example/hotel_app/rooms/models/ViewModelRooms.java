package com.example.hotel_app.rooms.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ViewModelRooms extends ViewModel {
    private ArrayList<Room> rooms;
    private final MutableLiveData<ArrayList<Room>> roomsLiveData = new MutableLiveData<>();

    public ViewModelRooms() {
        rooms = new ArrayList<>();
    }

    public ViewModelRooms(ArrayList<Room> list) {
        rooms = list;
    }

    public MutableLiveData<ArrayList<Room>> getAllRooms() {
        return roomsLiveData;
    }

    public void setRooms(ArrayList<Room> users) {
        this.rooms = users;
        roomsLiveData.postValue(users);
    }

    public void addRoom(Room user) {
        rooms.add(user);
        roomsLiveData.postValue(rooms);
    }

    public void clear() {
        rooms.clear();
        roomsLiveData.postValue(this.rooms);
    }

    public void deleteRoom(Room room){
        rooms.remove(room);
        roomsLiveData.postValue(this.rooms);
    }

    public ArrayList<Room> getRooms() {
        return rooms;
    }
}
