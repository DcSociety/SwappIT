package com.example.swappit.Model;

public class User {
    private String id;
    private String fullname;
    private String email;
    private String contact;
    private String bio;
    private String imageurl;

    public User(String id, String fullname, String email, String contact, String bio, String imageurl) {
        this.id = id;
        this.fullname = fullname;
        this.email = email;
        this.contact = contact;
        this.bio = bio;
        this.imageurl = imageurl;
    }

    public User() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
}
