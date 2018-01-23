package com.firebase.ginggingi.myfbs.model;

/**
 * Created by GingGingI on 2017-07-28.
 */

public class ChatData {

    public static final int TYPE_TEXT_ME = 0;
    public static final int TYPE_TEXT_OTHER = 1;

    private String UserName;
    private String Messages;
    private String PictureURI;
    private String nowTime;
    private int Type;

    public ChatData(){}

    public ChatData(String UserName, String Messages,  String PictureURI, String nowTime, int Type){
        this.UserName = UserName;
        this.Messages = Messages;
        this.PictureURI = PictureURI;
        this.nowTime = nowTime;
        this.Type = Type;
    }

    public String getUserName() {
        return UserName;
    }
    public void setUserName(String userName) {
        UserName = userName;
    }
    public String getMessages() {
        return Messages;
    }
    public void setMessages(String messages) {
        Messages = messages;
    }
    public String getPictureURI() {
        return PictureURI;
    }
    public void setPictureURI(String pictureURI) {
        PictureURI = pictureURI;
    }
    public String getNowTime() {
        return nowTime;
    }
    public void setNowTime(String nowTime) {
        this.nowTime = nowTime;
    }
    public int getType() {
        return Type;
    }
    public void setType(int type) {
        Type = type;
    }
}
