package com.sori.touchsori.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.api.ApiUtil;
import com.sori.touchsori.data.ApiAuthorizationData;
import com.sori.touchsori.signIn.DeviceInfo;
import com.sori.touchsori.utill.TypefaceUtil;
import com.sori.touchsori.utill.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import kr.co.innochal.touchsorilibrary.classes.OnTouchSoriListener;
import kr.co.innochal.touchsorilibrary.classes.TouchSori;
import kr.co.innochal.touchsorilibrary.common.App;
import kr.co.innochal.touchsorilibrary.common.Config;
import kr.co.innochal.touchsorilibrary.common.Error;
import kr.co.innochal.touchsorilibrary.common.RequestCode;
import kr.co.innochal.touchsorilibrary.common.Status;

import kr.co.innochal.touchsorilibrary.communication.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BaseActivity extends AppCompatActivity {

    public ApiUtil apiUtil;
    public SoriApplication soriApplication;
    public Utils utils;
    Context mContext;
    public DeviceInfo deviceInfo;

    public JsonObject deviceObj;

    public TouchSori touchSori;
    public String loginToken;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } catch (IllegalStateException ignore) {

        }

        mContext = this;
        soriApplication = SoriApplication.getInstance();

        apiUtil = new ApiUtil(this);
        utils = new Utils(this);
        deviceInfo = new DeviceInfo(this);
        deviceInfo.setObject(utils.getDeviceInfo());
        deviceObj = deviceInfo.getObject();
        loginToken = utils.getLoginToken();
        if (loginToken == null && loginToken.matches("") && deviceObj != null) {
            refreshToken();
        }
//테스트 test
//        deviceObj.addProperty("serialNo" , "A03697R17");
//        String test = "A03697R63";



//        touchSori.setOnTouchSoriListener(new OnTouchSoriListener() {
//            @Override
//            public void onServiceStatus(Status status, String s) {
//                Log.d("onServiceStatus :::" , status + "//" + s);
//            }
//
//            @Override
//            public void onRegistrSerialNumber(Error error, String s) {
//                Log.d("onServiceStatus :::" , error + "//" + s);
//            }
//
//            @Override
//            public void onPressedButton(boolean b, String s) {
//                Log.d("onServiceStatus :::" , b + "//" + s);
//            }
//        });


    }


    public void showMessage(String message) {
        Toast.makeText(mContext , message , Toast.LENGTH_SHORT).show();
    }
    String refreshToken;

    /**
     * 토큰갱신 ...
     * @return
     */
    public String refreshToken() {

            apiUtil.refreshToken(deviceObj.get("uid").getAsString()).enqueue(new Callback<ApiAuthorizationData>() {
                @Override
                public void onResponse(Call<ApiAuthorizationData> call, Response<ApiAuthorizationData> response) {
                    if (response.isSuccessful()) {
                        refreshToken = response.body().getAuthorization();
                        utils.saveLoginToken(refreshToken);
                    }else {
                        try {
                            String message;
                            JSONObject jsonError = new JSONObject(response.errorBody().string());
                            message = jsonError.getString("message");
                            Toast.makeText(mContext , message, Toast.LENGTH_SHORT);
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
            return refreshToken;
        }


}
