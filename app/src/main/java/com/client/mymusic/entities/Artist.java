package com.client.mymusic.entities;

public class Artist {

  private int id;
  private String name;
  private int sex;
  private String avatar;
  private int count_songs;
  private String country;
  private String description;

  public Artist(int id, String name, int sex, String avatar, int count_songs, String country, String description) {
    this.id = id;
    this.name = name;
    this.sex = sex;
    this.avatar = avatar;
    this.count_songs = count_songs;
    this.country = country;
    this.description = description;
  }

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

  public int getSex() {
    return sex;
  }

  public void setSex(int sex) {
    this.sex = sex;
  }

  public String getAvatar() {
    return avatar;
  }

  public void setAvatar(String avatar) {
    this.avatar = avatar;
  }

  public int getCount_songs() {
    return count_songs;
  }

  public void setCount_songs(int count_songs) {
    this.count_songs = count_songs;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
