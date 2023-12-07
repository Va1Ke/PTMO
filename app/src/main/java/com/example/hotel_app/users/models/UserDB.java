package com.example.hotel_app.users.models;

import java.io.Serializable;

public class UserDB implements Serializable {
    private int id;
    private String name;
    private String password;
    private String email;
    private boolean admin;

    public UserDB(int id, String name, String password, String email, boolean admin) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.admin = admin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
