package com.client.mymusic.entities;

import com.squareup.moshi.Json;

public class Slidershow {

  @Json(name = "name")
  private String title;

  @Json(name = "image")
  private String imageUrl;

  public Slidershow(String title, String imageUrl) {
    this.title = title;
    this.imageUrl = imageUrl;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }
}