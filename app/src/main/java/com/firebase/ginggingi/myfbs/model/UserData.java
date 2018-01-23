package com.firebase.ginggingi.myfbs.model;

/**
 * Created by GingGingI on 2017-07-28.
 */

public class UserData {

    public String useremail;
    public String username;
    public String photofilename;

    public UserData(){}

    public UserData(String useremail, String username, String photofilename) {
        this.useremail = useremail;
        this.username = username;
        this.photofilename = photofilename;
    }

    public String getUseremail() {
        return useremail;
    }
    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPhotofilename() {
        return photofilename;
    }
    public void setPhotofilename(String photofilename) {
        this.photofilename = photofilename;
    }
}
