package com.sori.touchsori.data;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ApiAuthorizationData {

    @SerializedName("authorization")
    String authorization;

    @SerializedName("uid")
    String uid;

    @SerializedName("role")
    String role;

    @SerializedName("deviceId")
    String deviceId;

    public String getAuthorization() {return authorization;}

    public String getUid() {
        return uid;
    }

    public String getRole() {
        return role;
    }

    public String getDeviceId() {
        return deviceId;
    }
}
