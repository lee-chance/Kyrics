package com.chancestudio.newkpop.Model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class ManagerModelList {
    String nickname, email, content;
    @ServerTimestamp
    private Date date;

    public ManagerModelList() {
    }

    public ManagerModelList(String nickname, String content) {
        this.nickname = nickname;
        this.content = content;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
