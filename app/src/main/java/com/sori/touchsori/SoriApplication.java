package com.sori.touchsori;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.sori.touchsori.api.ApiUtil;
import com.sori.touchsori.utill.TypefaceUtil;

import java.lang.reflect.Field;

import kr.co.innochal.touchsorilibrary.classes.TouchSori;

public class SoriApplication extends Application {

    public static SoriApplication soriApplication;
    public ApiUtil apiUtil;
    Context mContext;
    int PERMISSION_REQUEST_ALL = 1000;

    private final String[] permissions = new String[]{                           // 퍼미션
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.SEND_SMS,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_CONTACTS,
    };


    public static SoriApplication getInstance() {return soriApplication;}

    @Override
    public void onCreate() {
        super.onCreate();
        soriApplication = this;
        mContext = getApplicationContext();
        apiUtil = new ApiUtil(this);
        TypefaceUtil.overrideFont(this , "SERIF" , "fonts/NanumSquare_acB.ttf");

    }

    /**
     * 퍼미션 체크
     *
     * @param activity
     * @return
     */
    public boolean checkPermissionAll(Activity activity) {
        boolean req_permission = false;

        // 퍼미션 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 (Ver.6.0)
            req_permission = checkPermission(activity, permissions);
        }
        return !req_permission;
    }

    /**
     * 퍼미션 체크
     * @param activity
     * @param permission
     * @return
     */
    private boolean checkPermission(Activity activity, String[] permission) {
        boolean req_permission = false;

        // 퍼미션 체크
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 마시멜로우 (Ver.6.0)
            for (int i = 0; i < permission.length; i++) {
                if (ContextCompat.checkSelfPermission(mContext, permission[i]) != PackageManager.PERMISSION_GRANTED) {
                    req_permission = true;
                    break;
                }
            }
            if (req_permission && activity != null) {
                ActivityCompat.requestPermissions(activity, permission, PERMISSION_REQUEST_ALL);
            }
        }
        return req_permission;
    }

    /**
     * 퍼미션 체크
     *
     * @param activity
     * @param permission
     * @param res_type
     * @return
     */
    public boolean checkPermission(Activity activity, String permission, int res_type) {
        if (ContextCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, res_type);
            return false;
        } else {
            return true;
        }
    }


}
