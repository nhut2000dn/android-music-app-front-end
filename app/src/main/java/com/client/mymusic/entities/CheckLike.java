package com.client.mymusic.entities;

import com.squareup.moshi.Json;

public class CheckLike {

  @Json(name = "user_id")
  private int userId;
  @Json(name = "song_id")
  private int songId;
  @Json(name = "check")
  private Boolean check;

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getSongId() {
    return songId;
  }

  public void setSongId(int songId) {
    this.songId = songId;
  }

  public Boolean getCheck() {
    return check;
  }

  public void setCheck(Boolean check) {
    this.check = check;
  }
}
