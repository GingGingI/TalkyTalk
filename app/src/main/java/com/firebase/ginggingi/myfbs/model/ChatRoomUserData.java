package com.firebase.ginggingi.myfbs.model;

/**
 * Created by GingGingI on 2018-01-14.
 */

public class ChatRoomUserData {
    public String UserName;

    public ChatRoomUserData(){}
    public ChatRoomUserData(String userName){
        UserName = userName;
    }

    public String getUserName() {
        return UserName;
    }
    public void setUserName(String userName) {
        UserName = userName;
    }
}
