package com.client.mymusic.entities;

import java.io.Serializable;

public class Audio implements Serializable {

    private int id;
    private String title;
    private String image;
    private String data;
    private int idArtist;
    private String artist;
    private String avatarArtist;
    private int views;
    private int dowloads;
    private int likes;
    private String lyrics;
    private String album;

    public Audio(int id, String title, String image, String data, int idArtist, String artist, String avatarArtist, int views, int dowloads, int likes, String lyrics, String album) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.data = data;
        this.idArtist = idArtist;
        this.artist = artist;
        this.avatarArtist = avatarArtist;
        this.views = views;
        this.dowloads = dowloads;
        this.likes = likes;
        this.lyrics = lyrics;
        this.album = album;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public int getIdArtist() {
        return idArtist;
    }

    public void setIdArtist(int idArtist) {
        this.idArtist = idArtist;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAvatarArtist() {
        return avatarArtist;
    }

    public void setAvatarArtist(String avatarArtist) {
        this.avatarArtist = avatarArtist;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getDowloads() {
        return dowloads;
    }

    public void setDowloads(int dowloads) {
        this.dowloads = dowloads;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }
}
