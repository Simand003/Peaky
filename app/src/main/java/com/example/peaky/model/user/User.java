package com.example.peaky.model.user;

import java.util.Date;

public class User {
    private String uid;
    private String email;
    private String name;
    private String surname;
    private Date birth_date;
    private GenderUser gender;

    public User(String uid, String email, String name, String surname, Date birth_date, GenderUser gender) {
        this.uid = uid;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.birth_date = birth_date;
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
        return birth_date;
    }

    public void setBirth_date(Date birth_date) {
        this.birth_date = birth_date;
    }

    public GenderUser getGender() {
        return gender;
    }

    public void setGender(GenderUser gender) {
        this.gender = gender;
    }
}