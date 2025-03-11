package com.example.peaky.model;

import java.util.Date;

public class User {
    private String uid;
    private String email;
    private String name;
    private String surname;
    private Date date_of_birth;
    private GenderUser gender;

    public User(String uid, String email, String name, String surname, Date date_of_birth, GenderUser gender) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.date_of_birth = date_of_birth;
        this.gender = gender;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Date getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(Date date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public GenderUser getGender() {
        return gender;
    }

    public void setGender(GenderUser gender) {
        this.gender = gender;
    }
}