package com.example.android.movies.model;

public class Review {

    private String author;
    private String content;
    private String id;
    private String url;


    // CONSTRUCTORS

    public Review() {};

    public Review(String author, String content, String id, String url) {
        this.author = author;
        this.content = content;
        this.id = id;
        this.url = url;
    }


    // GETTERS

    public String getAuthor() { return this.author; }
    public String getContent() { return this.content; }
    public String getId() { return this.id; }
    public String getUrl() { return this.url; }


    // SETTERS
    public void setAuthor(String author) { this.author = author; }
    public void setContent(String content) { this.content = content; }
    public void setId(String id) { this.id = id; }
    public void setUrl(String url) { this.url = url; }

}
