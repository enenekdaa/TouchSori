package com.sori.touchsori.intro;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.sori.touchsori.MainActivity;
import com.sori.touchsori.R;
import com.sori.touchsori.base.BaseActivity;
import com.sori.touchsori.data.ApiAuthorizationData;
import com.sori.touchsori.signIn.DeviceInfo;
import com.sori.touchsori.signIn.SignInActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntroActivity extends BaseActivity {


    ImageView introImg;

    String imei;
    String phoneNumber;
    TelephonyManager tm;
    Context mContext;
    String loginTp = "";
    String deviceId = "";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_view);

        mContext = this;

        introImg = findViewById(R.id.intro_img);
        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        checkPermission();
        loginTp = utils.getLoginToken();
        deviceId = utils.getDeviceID();
    }

    private void initView() {

        if (loginTp.matches("") || deviceId.matches("")){
            userStateApi();
        }else {
            Intent intent = new Intent(mContext , MainActivity.class);
            goMainAct(intent);
        }


    }

    PermissionListener permissionlistener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한 허가", Toast.LENGTH_SHORT).show();
            try {
//                    phoneNumber = tm.getLine1Number();
//                    if(phoneNumber.startsWith("+82")){
//                        phoneNumber = phoneNumber.replace("+82", "0");
//                    }
                deviceObj.addProperty("phoneNo" , "01066758566");
                //  deviceObj.addProperty("phoneNo" , phoneNumber);
                deviceObj.addProperty("deviceName" , Build.DISPLAY);
                deviceObj.addProperty("maker" , Build.MANUFACTURER);
                deviceObj.addProperty("imei" ,"354869090589003");
                //  deviceObj.addProperty("imei" , tm.getDeviceId());
                deviceObj.addProperty("version" , tm.getDeviceSoftwareVersion());
                deviceObj.addProperty("iccid" , "iccid" );
                //+ Build.ID
                deviceObj.addProperty("model" , Build.MODEL);

                deviceObj.addProperty("username" , deviceObj.get("phoneNo").getAsString() + deviceObj.get("imei").getAsString());
                deviceObj.addProperty("password" , deviceObj.get("username").getAsString());

                utils.setDeviceInfo(deviceObj.toString());
                deviceInfo.setObject(utils.getDeviceInfo());
            } catch (Exception e) {
                e.printStackTrace();
            }
            initView();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    @SuppressLint("NewApi")
    private void checkPermission() {
        try {
            new TedPermission(mContext)
                    .setPermissionListener(permissionlistener)
                    .setRationaleMessage("앱을 시작하기 위해 권한이 필요합니다")
                    .setDeniedMessage("[설정] > [권한] 에서 권한을 허용할 수 있어요.")
                    .setPermissions(new String[]{Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.READ_CONTACTS})
                    .check();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void userStateApi() {
        JSONObject jsonObject = new JSONObject();
        try {
        //    jsonObject.put("username", utils.stringHash(deviceObj.get("username").getAsString()));
            jsonObject.put("username", deviceObj.get("username").getAsString());
            jsonObject.put("password",  deviceObj.get("password").getAsString());


            apiUtil.loginPost(jsonObject).enqueue(new Callback<ApiAuthorizationData>() {
                @Override
                public void onResponse(Call<ApiAuthorizationData> call, Response<ApiAuthorizationData> response) {
                    String message;
                    if (response.isSuccessful()) {
                        String result =  response.body().getAuthorization();
                        deviceObj.addProperty("deviceId" , response.body().getDeviceId());
                        deviceObj.addProperty("role" , response.body().getRole());
                        deviceObj.addProperty("uid" , response.body().getUid());

                        utils.setDeviceInfo(deviceObj.toString());
                        utils.setDeviceID(deviceObj.get("deviceId").getAsString());
                        utils.saveLoginToken(result);

                        Intent intent = new Intent(IntroActivity.this , MainActivity.class);
                        goMainAct(intent);

                    }else{

                        Intent intent = new Intent(IntroActivity.this , SignInActivity.class);
                        goMainAct(intent);
                        try {
                            JSONObject jsonError = new JSONObject(response.errorBody().string());
                            message = jsonError.getString("message");
                            showMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<ApiAuthorizationData> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        }catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void goMainAct(Intent intent) {
       intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


}
