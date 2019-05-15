package com.sori.touchsori.search;

import java.io.Serializable;

public class ContactItem implements Serializable {

    private String user_Number , user_Name;
    private int id;

    public ContactItem() { }

    public String getUser_Number() {
        return user_Number;
    }

    public void setUser_Number(String user_Number) {
        this.user_Number = user_Number;
    }

    public String getUser_Name() {
        return user_Name;
    }

    public void setUser_Name(String user_Name) {
        this.user_Name = user_Name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
