package com.gmonetix.pedals.model;

/**
 * @author Gmonetix
 */

public class Admin {

    public String email;
    public long phone;

    public Admin() {
    }

    public Admin(String email, long phone) {
        this.email = email;
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "Admin{" +
                "email='" + email + '\'' +
                ", phone='" + String.valueOf(phone) + '\'' +
                '}';
    }

}
