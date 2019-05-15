package com.sori.touchsori.signIn;

import android.content.Context;

import com.google.gson.JsonObject;

public class DeviceInfo  {


    private Context context;
    private JsonObject object = new JsonObject();

    public DeviceInfo(Context context) {
        this.context = context;
    }

    public JsonObject getObject() {
        return object;
    }

    public void setObject(JsonObject object) {
        this.object = object;
    }
}
