package com.example.firebasedemo;

public class Upload {
    private String mName;
    private String mSurname;
    private String mAge;
    private String mProfileImageUrl;

    public Upload() {

    }

    public Upload(String name, String surname, String age,  String profileImageUrl) {

        mName = name;
        mSurname = surname;
        mAge = age;
        mProfileImageUrl = profileImageUrl;

    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getSurname() {
        return mSurname;
    }

    public void setSurname(String surname) {
        mSurname = surname;
    }

    public String getAge() {
        return mAge;
    }

    public void setAge(String age) {
        mAge = age;
    }

    public String getProfileImageUrl() {
        return mProfileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        mProfileImageUrl = profileImageUrl;
    }

}
