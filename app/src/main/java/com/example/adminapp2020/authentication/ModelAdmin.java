package com.example.adminapp2020.authentication;

public class ModelAdmin {
    String name,phn,uid;

    public ModelAdmin() {
    }

    public ModelAdmin(String name, String phn,  String uid) {
        this.name = name;
        this.phn = phn;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhn() {
        return phn;
    }

    public void setPhn(String phn) {
        this.phn = phn;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
