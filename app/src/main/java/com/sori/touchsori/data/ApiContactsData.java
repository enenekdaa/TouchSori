package com.sori.touchsori.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ApiContactsData {

    @SerializedName("contacts")
    JsonObject contacts;

    public JsonObject getContacts() {
        return contacts;
    }

    public void setContacts(JsonObject contacts) {
        this.contacts = contacts;
    }

    public JsonArray generalList(){
        return contacts.getAsJsonArray("general");
    }
}
