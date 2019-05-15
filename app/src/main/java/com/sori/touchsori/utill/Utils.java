package com.sori.touchsori.utill;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sori.touchsori.api.ApiUtil;
import com.sori.touchsori.base.BaseActivity;
import com.sori.touchsori.data.ApiAuthorizationData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utils {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Context context;

    public Utils() {
    }

    public Utils(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("util", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * 디바이스 정보 or ID 저장 / 가져오기
     */

    public void setDeviceID(String id) {
        editor.putString("deviceID", id);
        editor.commit();

    }

    public String getDeviceID() {

        String deviceID = sharedPreferences.getString("deviceID", "");
        return deviceID;
    }


    public void setDeviceInfo(String deviceInfo) {
        editor.putString("deviceInfo", deviceInfo);
        editor.commit();

    }

    public JsonObject getDeviceInfo() {
        JsonParser jsonParser = new JsonParser();
        JsonElement jsonElement = jsonParser.parse(sharedPreferences.getString("deviceInfo", ""));
        if (jsonElement.isJsonNull())
            return new JsonObject();
        return jsonElement.getAsJsonObject();
    }

    /**
     * 로그인 토큰 저장 / 가져오기
     *
     * @return
     */

    public void saveLoginToken(String loginToken) {
        editor.putString("loginToken", loginToken);
        editor.commit();
    }

    public String getLoginToken() {

        String token = sharedPreferences.getString("loginToken", "");
        return token;
    }

    /**
     * 알람 on / off
     *
     * @return
     */
    public void saveAlaramStatus(String status) {
        editor.putString("alarmStatus", status);
        editor.commit();
    }

    public String getAlarmStatus() {

        String alarmStatus = sharedPreferences.getString("alarmStatus", "");
        return alarmStatus;
    }

    /**
     * Dialog 사이즈 조절
     *
     * @return
     */

    public static void dialogSizeSetting(Activity activity, Dialog dialog) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        Window window = dialog.getWindow();
        int x = (int) (size.x * 0.7f);
        int y = (int) (size.y * 0.6f);
        window.setGravity(Gravity.CENTER);
        window.setLayout(x, y);
    }

    /**
     * 문자열 hash
     *
     * @return
     */

    public String stringHash(String str) {
        String resultStr;
        String hashType = "SHA-256";
        java.security.MessageDigest md;
        StringBuffer result = null;
        try {
            md = java.security.MessageDigest.getInstance(hashType);

            byte[] hashData = md.digest(str.getBytes());

            result = new StringBuffer();

            for (byte b : hashData){
                result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        resultStr = result.toString();

        return resultStr;
    }


}
