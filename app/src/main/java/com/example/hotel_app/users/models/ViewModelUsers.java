package com.example.hotel_app.users.models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class ViewModelUsers extends ViewModel {
    private ArrayList<UserDB> users;
    private final MutableLiveData<ArrayList<UserDB>> usersLiveData = new MutableLiveData<>();

    public ViewModelUsers() {
        users = new ArrayList<>();
    }

    public ViewModelUsers(ArrayList<UserDB> list) {
        users = list;
    }

    public MutableLiveData<ArrayList<UserDB>> getAllUsers() {
        return usersLiveData;
    }

    public void setUsers(ArrayList<UserDB> users) {
        this.users = users;
        usersLiveData.postValue(users);
    }

    public void addUser(UserDB user) {
        users.add(user);
        usersLiveData.postValue(users);
    }

    public void clear() {
        users.clear();
        usersLiveData.postValue(this.users);
    }

    public void deleteUser(UserDB user){
        users.remove(user);
        usersLiveData.postValue(this.users);
    }

    public ArrayList<UserDB> getUsers() {
        return users;
    }
}
