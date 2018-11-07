package com.example.android.movies.model;

public class Trailer {

    private String id;
    private String name;
    private String key;
    private String type; // Teaser or Trailer
    private String site; // optional


    // CONSTRUCTORS

    public Trailer() {}

    public Trailer(String id, String name, String key, String type, String site) {
        this.id = id;
        this.name = name;
        this.key = key;
        this.type = type;
        this.site = site;
    }


    // GETTERS
    public String getId() { return id; }
    public String getName() { return name; }
    public String getKey() { return  key; }
    public String getType() { return type; }
    public String getSite() { return site; }


    // SETTERS
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setKey(String key) { this.key = key; }
    public void setType(String type) { this.type = type; }
    public void setSite(String site) { this.site = site; }
}
