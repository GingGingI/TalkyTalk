package com.firebase.ginggingi.myfbs.model;

public class ChatList {
    public String roomname;
    public String sub_matt;
    public String cid;
    public String uid;

    public ChatList(){}

    public ChatList(String roomname, String sub_matt, String cid, String uid){
        this.roomname = roomname;
        this.sub_matt = sub_matt;
        this.cid = cid;
        this.uid = uid;
    }

    public void setRoomname(String roomname){
        this.roomname = roomname;
    }
    public String getRoomname(){
        return roomname;
    }
    public void setsub_matt(String Sub_matt){
        this.sub_matt = Sub_matt;
    }
    public String getsub_matt(){
        return sub_matt;
    }
    public void setCid(String cid){
        this.cid = cid;
    }
    public String getCid(){
        return cid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getUid() {
        return uid;
    }
}
