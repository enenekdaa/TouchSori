package com.sori.touchsori.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.Gravity;

import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.utill.Define;
import com.sori.touchsori.utill.LocationInfo;
import com.sori.touchsori.utill.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;

/**
 * Created by innochal on 2017. 10. 20..
 *
 * 친구 찾기 위치정보 전송 서비스 (2017-10-20)
 */

public class SendLocationService extends Service {
    private static final String TAG = SendLocationService.class.getSimpleName();    // 디버그 태그
    private Context mContext;                                                       // 콘텍스트
    private SoriApplication mApp;                                              // 전역 (Application) 변수
    private LocationInfo mLocation;
    private Location location;
    private String receiver_hp;                                                     // 요청자 휴대 전화번호
    private int mType;                                                              // 타입

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtil.i(TAG, "onCreate() -> Start !!!");


        // 콘텍스트
        mContext = getApplicationContext();
        // 전역 (Application) 변수
        mApp = (SoriApplication) mContext;

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG, "onStartCommand() -> Start !!!");


        if (intent == null) return START_REDELIVER_INTENT;

        int type = intent.getIntExtra(Define.KEY_TYPE, 0);
        receiver_hp = intent.getStringExtra(Define.KEY_RECEIVER_HP);
        LogUtil.d(TAG, "onStartCommand() -> receiver_hp : " + receiver_hp);

        if (type == 0) stopSelf();
        if (TextUtils.isEmpty(receiver_hp)) stopSelf();

        mType = type;
        switch (type) {
            // 친구 요청
            case 10: {
                break;
            }
            case 11:
            {
                break;
            }
            case 12: {
                mLocationHandler.removeMessages(LOCATION_EVENT_START);
                mLocationHandler.removeMessages(LOCATION_EVENT_UPDATE);
                mLocationHandler.sendEmptyMessage(LOCATION_EVENT_START);
                break;
            }
            case 13: {
                break;
            }

            case 33: {                                                          // 안심영역 - 위치정보
                mLocationHandler.removeMessages(LOCATION_EVENT_START);
                mLocationHandler.removeMessages(LOCATION_EVENT_UPDATE);
                mLocationHandler.sendEmptyMessage(LOCATION_EVENT_START);
                break;
            }
            default:
                break;
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy()");
        super.onDestroy();
    }

    /**
     * 위치정보 전송
     */
    private class LocationItem {
        boolean result;     // 결과
//        String latitude;    // 위도
//        String longitude;   // 경도

        public boolean getResult() {
            return result;
        }

//        public String getLatitude() { return latitude;}
//        public String getongitude() { return longitude; }
    }

//    /**
//     * 위치정보 전송 API 호출
//     */
//    private interface RequestSendLocation {
//        @GET("friend/RequestSendLocation")
//        Call<LocationItem> getLocation(
//                @QueryMap Map<String, String> params
//        );
//
//        OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                .readTimeout(60, TimeUnit.SECONDS)
//                .connectTimeout(60, TimeUnit.SECONDS)
//                .build();
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(Define.SERVER_URL)
//                .client(okHttpClient)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//    }

    /**
     * 위치정보 전송 AsyncTask
     */
    private void RequestSendLocationTask (){

        // 전역 (Application) 변수
        if (mApp == null) mApp = (SoriApplication) mContext;

        mApp.apiUtil.locationCall3().enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    mApp.showMessage("현재위치를 전송하였습니다. ");
                    mLocationHandler.removeMessages(LOCATION_EVENT_START);
                    mLocationHandler.removeMessages(LOCATION_EVENT_UPDATE);

                    stopSelf();
                }else {
                    try {
                        String message;
                        String code;
                        JSONObject jsonError = new JSONObject(response.errorBody().string());
                        code = jsonError.getString("code");
                        message = jsonError.getString("message");
                        mApp.showMessage(message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                t.printStackTrace();
            }
        });

    }

    private final int LOCATION_EVENT_START = 0;
    private final int LOCATION_EVENT_UPDATE = 1;

    private int time_count = 6;
    private Handler mLocationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOCATION_EVENT_START:
                    mLocation = LocationInfo.getInstance(mContext);
                    mLocation.updateLocationInfo();
                    this.sendEmptyMessageDelayed(LOCATION_EVENT_UPDATE, 1000);
                    break;
                case LOCATION_EVENT_UPDATE:
                    time_count--;
                    if (time_count > 0) {
                        this.sendEmptyMessageDelayed(LOCATION_EVENT_UPDATE, 1000);
                    } else {
                        if (mLocation != null) mLocation.stopLocationInfo();
                        location = mLocation.getCurrentLocation();

                        new Thread() {
                            @Override
                            public void run() {
                                if(12 == mType) {
                                    RequestSendLocationTask();
                                }
                            }
                        }.start();

                    }
                    break;
            }
        }
    };
}
