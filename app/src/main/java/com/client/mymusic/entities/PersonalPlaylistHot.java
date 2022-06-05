package com.client.mymusic.entities;

public class PersonalPlaylistHot {
  private int id;
  private String idUser;
  private String emailUser;
  private String nameUser;
  private String name;
  private String image;
  private String views;

  public PersonalPlaylistHot(int id, String idUser, String emailUser, String nameUser, String name, String image, String views) {
    this.id = id;
    this.idUser = idUser;
    this.emailUser = emailUser;
    this.nameUser = nameUser;
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

  public String getIdUser() {
    return idUser;
  }

  public void setIdUser(String idUser) {
    this.idUser = idUser;
  }

  public String getEmailUser() {
    return emailUser;
  }

  public void setEmailUser(String emailUser) {
    this.emailUser = emailUser;
  }

  public String getNameUser() {
    return nameUser;
  }

  public void setNameUser(String nameUser) {
    this.nameUser = nameUser;
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

  public String getViews() {
    return views;
  }

  public void setViews(String views) {
    this.views = views;
  }
}
