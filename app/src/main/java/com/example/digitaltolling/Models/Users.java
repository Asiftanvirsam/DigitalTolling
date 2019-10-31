package com.example.digitaltolling.Models;

public class Users {
    private String name;
    private String email;
    private String nid;

    public Users() {
    }

    public Users(String name, String email, String nid) {
        this.name = name;
        this.email = email;
        this.nid = nid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

}
