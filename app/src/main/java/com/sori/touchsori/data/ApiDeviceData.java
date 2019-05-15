package com.sori.touchsori.data;

import com.google.gson.annotations.SerializedName;

public class ApiDeviceData {

    @SerializedName("deviceId")
    String deviceId;

    public String getDeviceId() {return deviceId;}
}
