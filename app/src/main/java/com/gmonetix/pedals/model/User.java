package com.gmonetix.pedals.model;

/**
 * @author Gmonetix
 */

public class User {

    public String name, email, phone, nit_registration, image;

    public User() {
    }

    public User(String name, String email, String phone, String nit_registration, String image) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.nit_registration = nit_registration;
        this.image = image;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", nit_registration='" + nit_registration + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
