package com.giangnd_svmc.ghalo.entity;

import java.io.Serializable;

/**
 * Created by GIANGND-SVMC on 22/01/2016.
 */
public class Message implements Serializable {
    private String me;
    private String friend;
    private String content;
    private boolean isMe = true;

    public Message() {
    }

    public Message(String me, String friend, String content) {
        this.me = me;
        this.friend = friend;
        this.content = content;
    }

    public void setIsMe(boolean isMe) {
        this.isMe = isMe;
    }

    public boolean getIsMe() {
        return this.isMe;
    }

    public String getMe() {
        return me;
    }

    public void setMe(String me) {
        this.me = me;
    }

    public String getFriend() {
        return friend;
    }

    public void setFriend(String friend) {
        this.friend = friend;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
