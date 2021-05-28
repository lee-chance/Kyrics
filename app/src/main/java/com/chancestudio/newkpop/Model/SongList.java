package com.chancestudio.newkpop.Model;

public class SongList {

    private String id;
    private String title;
    private String singer;
    private String youtube_address;
    private String lyrics;
    private String pronounce;
    private String translate;

    public SongList() {
    }

    public SongList(String id, String title, String singer, String youtube_address, String lyrics, String pronounce, String translate) {
        this.id = id;
        this.title = title;
        this.singer = singer;
        this.youtube_address = youtube_address;
        this.lyrics = lyrics;
        this.pronounce = pronounce;
        this.translate = translate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getYoutube_address() {
        return youtube_address;
    }

    public void setYoutube_address(String youtube_address) {
        this.youtube_address = youtube_address;
    }

    public String getLyrics() {
        return lyrics;
    }

    public void setLyrics(String lyrics) {
        this.lyrics = lyrics;
    }

    public String getPronounce() {
        return pronounce;
    }

    public void setPronounce(String pronounce) {
        this.pronounce = pronounce;
    }

    public String getTranslate() {
        return translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }
}
