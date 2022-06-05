package com.client.mymusic.entities;

import com.squareup.moshi.Json;

public class User {

    @Json(name = "id")
    private int id;
    @Json(name = "name")
    private String name;
    @Json(name = "email")
    private String email;
    @Json(name = "avatar")
    private String avatar;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
