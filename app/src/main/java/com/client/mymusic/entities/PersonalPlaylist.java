package com.client.mymusic.entities;

public class PersonalPlaylist {

  private int id;
  private int user_id;
  private String name;
  private String image;

  public PersonalPlaylist(int id, int user_id, String name, String image) {
    this.id = id;
    this.user_id = user_id;
    this.name = name;
    this.image = image;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getUser_id() {
    return user_id;
  }

  public void setUser_id(int user_id) {
    this.user_id = user_id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }
}
