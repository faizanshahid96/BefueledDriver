package com.example.befueleddriver.Models;

public class User {
    private String user_id;
    private long phone;
    private String email;
    private String firstname;
    private String lastname;


    public User(String user_id, long phone, String email, String firstname, String lastname) {
        this.user_id = user_id;
        this.phone = phone;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
    }


    public User() {
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public long getPhone() {
        return phone;
    }

    public void setPhone(long phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", phone=" + phone +
                ", email='" + email + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                '}';
    }
}
