package com.example.firebasedemo;

public class Upload {
    private String mUserName;
    private String mProfileImageUrl;

    public Upload() {

    }

    public Upload(String userName, String profileImageUrl) {
        if (userName.trim().equals("")) {
            userName = "No Name";
        }
        mUserName = userName;
        mProfileImageUrl = profileImageUrl;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        mUserName = userName;
    }

    public String getProfileImageUrl() {
        return mProfileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        mProfileImageUrl = profileImageUrl;
    }
}
