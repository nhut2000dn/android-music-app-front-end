package com.client.mymusic.entities;

public class Playlist {

  private int id;
  private int theme_id;
  private String name;
  private String image;
  private int views;

  public Playlist(int id, int theme_id, String name, String image, int views) {
    this.id = id;
    this.theme_id = theme_id;
    this.name = name;
    this.image = image;
    this.views = views;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public int getTheme_id() {
    return theme_id;
  }

  public void setTheme_id(int theme_id) {
    this.theme_id = theme_id;
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

  public int getViews() {
    return views;
  }

  public void setViews(int views) {
    this.views = views;
  }
}
