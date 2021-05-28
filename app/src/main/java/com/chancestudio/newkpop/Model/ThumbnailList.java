package com.chancestudio.newkpop.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ThumbnailList {

    private String id;
    private String title;
    private String singer;
    @ServerTimestamp
    private Date date;

    public ThumbnailList() {
    }

    public ThumbnailList(String id, String title, String singer) {
        this.id = id;
        this.title = title;
        this.singer = singer;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
