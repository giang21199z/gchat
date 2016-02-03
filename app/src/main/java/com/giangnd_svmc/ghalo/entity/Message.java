package com.giangnd_svmc.ghalo.entity;

/**
 * Created by GIANGND-SVMC on 22/01/2016.
 */
public class Message {
    private String me;
    private String friend;
    private String content;

    public Message() {
    }

    public Message(String me, String friend, String content) {
        this.me = me;
        this.friend = friend;
        this.content = content;
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
