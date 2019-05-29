package com.sori.touchsori.utill;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
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
import com.sori.touchsori.service.TouchService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sori.touchsori.utill.Define.PREFER_TIME_END;

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
     * 버튼 타입 가져오기
     */
    public int getButtonType() {
        String serialNumber = sharedPreferences.getString("serialNumber", null);
        if (StringUtil.isEmpty(serialNumber)) {
            serialNumber = "-1";
            return Integer.parseInt(serialNumber);
        } else {
            serialNumber.trim();
            return Integer.parseInt(serialNumber.substring(serialNumber.length() - 1, serialNumber.length()));
        }
    }


    /**
     * 긴급 수신자 리스트
     *
     * @return
     */

    public void saveSosList(ArrayList<String> arrayList) {
        JSONArray a = new JSONArray();
        for (int i = 0; i < arrayList.size(); i++) {
            a.put(arrayList.get(i));
        }
        if (!arrayList.isEmpty()) {
            editor.putString("sosList", a.toString());
        } else {
            editor.putString("sosList", null);
        }
        editor.apply();
    }

    public ArrayList<String> getSosList () {
        String json = sharedPreferences.getString("sosList" ,null);
        ArrayList<String> list = new ArrayList<String>();
        if (json != null) {
            try {
                JSONArray a = new JSONArray(json);
                for (int i = 0; i < a.length(); i++) {
                    String data = a.optString(i);
                    list.add(data);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
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
     * siren on / off
     *
     * @return
     */
    public void saveSiren(String status) {
        editor.putString("sirenStatus", status);
        editor.commit();
    }

    public String getSiren() {

        String alarmStatus = sharedPreferences.getString("sirenStatus", "");
        return alarmStatus;
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
     * 시리얼 넘버 등록 여부
     * @return
     */
    public void setSerialNumber(String serialNumber) {
        editor.putString("serialNumber" , serialNumber);
        editor.commit();
    }

    public String getSerialNumber() {
        String serialNumber = sharedPreferences.getString("serialNumber" , "");
        return serialNumber;
    }

    public void setRegistrated(boolean serial) {
        editor.putBoolean("serial" , serial);
        editor.commit();
    }
    public boolean isRegistrated() {
        return sharedPreferences.getBoolean("serial", false);
    }


    /**
     * 안심귀가 종료 시간
     */
    public long getTimeEnd() {
        return sharedPreferences.getLong(PREFER_TIME_END, -1);
    }


    public void startTouchSoriService(Context context, String action, String extraName, int extraValue) {
        Intent intent = new Intent(context, TouchService.class);
        intent.setAction(action);
        intent.putExtra(extraName, extraValue);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
//        context.startService(intent);
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
