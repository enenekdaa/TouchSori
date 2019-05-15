package com.sori.touchsori.data;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ApiContactListData implements Serializable {

    @SerializedName("sortOrder")
    public int sortOrder;
    @SerializedName("name")
    public String name;
    @SerializedName("phone")
    public String phone;


    public ApiContactListData(JsonObject jsonObject){

        sortOrder = jsonObject.has("sortOrder") ? jsonObject.get("sortOrder").getAsInt() : 0;
        name = jsonObject.has("name") ? jsonObject.get("name").getAsString() : "";
        phone = jsonObject.has("phone") ? jsonObject.get("phone").getAsString() : "";
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
