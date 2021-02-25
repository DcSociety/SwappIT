package com.example.swappit.Model;

public class Post {
    private String postid;
    private String postimage;
    private String description;
    private String title;
    private String location;
    private String publisher;

    public Post(String postid, String postimage, String description, String title, String swap, String location, String publisher) {
        this.postid = postid;
        this.postimage = postimage;
        this.description = description;
        this.title = title;
        this.location = location;
        this.publisher = publisher;
    }

    public Post() {
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPostimage() {
        return postimage;
    }

    public void setPostimage(String postimage) {
        this.postimage = postimage;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }
}
