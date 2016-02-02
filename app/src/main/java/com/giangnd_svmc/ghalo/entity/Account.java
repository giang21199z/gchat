package com.giangnd_svmc.ghalo.entity;

import java.io.Serializable;

/**
 * Created by GIANGND-SVMC on 28/01/2016.
 */
public class Account implements Serializable{
    private String id;
    private String name;
    private String gender;
    private String email;

    public Account() {
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Account(String id, String name, String gender, String email) {
        this.id = id;
        this.name = name;
        this.gender = gender;
        this.email = email;
    }
}
