package com.sori.touchsori.service;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;

import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.intro.IntroActivity;
import com.sori.touchsori.utill.EtcUtil;
import com.sori.touchsori.utill.LocationInfo;
import com.sori.touchsori.utill.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LocationService extends Service {
    private static final String TAG = LocationService.class.getSimpleName();
    private SoriApplication mApp;                                      // 전역 (Application) 변수
    private Context mContext;                                               // 콘텍스트

    private LocationInfo mLocation;
    private Location location;
    private String receiver_hp;                                                     // 요청자 휴대 전화번호
    private int mType;                                                              // 타입

    private int count  = 0;                                                               //3번호출...

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        LogUtil.i(TAG, "onCreate() -> Start !!!");
     //  setSendLocationNotification();

        // 콘텍스트
        mContext = getApplicationContext();
        // 전역 (Application) 변수
        mApp = (SoriApplication) mContext;
        count = 0;
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtil.i(TAG, "onStartCommand() -> Start !!!");

        if (intent == null) return START_REDELIVER_INTENT;

        RequestSendLocationTask(count);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        LogUtil.d(TAG, "onDestroy()");
        stopForeground(true);

        mApp.setLocationCount(-1);

        //위치 전송 메시지 플래그 해지
   //     mApp.setMessageSending(false);

        PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
//        if(EtcUtil.isGyroTouchServiceStopDevice() && (false == pm.isInteractive())) {
//            GyroService.getInstance(mContext).startGyroInfo();
//        }

        // 터치소리 서비스 시작
        mApp.startTouchsoriService(TAG);
        super.onDestroy();
    }

    /**
     * 위치정보 전송 AsyncTask
     */
    private void RequestSendLocationTask (final int flag3){

        // 전역 (Application) 변수
        if (mApp == null) mApp = (SoriApplication) mContext;


        mApp.apiUtil.locationCall3().enqueue(new Callback<Void>() {
            int flagCount = flag3;
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    flagCount++;
                    LogUtil.d(TAG , "################### flagCount : : : " + flagCount + "////" + count);
                    if (flagCount > 2) {
                        mApp.showMessage("현재위치를 전송하였습니다. ");
                        mLocationHandler.removeMessages(LOCATION_EVENT_START);
                        mLocationHandler.removeMessages(LOCATION_EVENT_UPDATE);
                        stopForeground(true);
                    }else {
                        RequestSendLocationTask(flagCount);
                        return;
                    }



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
                                    RequestSendLocationTask(count);
                                }
                            }
                        }.start();

                    }
                    break;
            }
        }
    };


//    /**
//     * 로컬 알람 등록
//     */
//    public  void setSendLocationNotification() {
//        Intent notificationIntent = new Intent(this, IntroActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder builder;
//        if (Build.VERSION.SDK_INT >= 26) {
//            String CHANNEL_ID = "snwodeer_service_channel";
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
//                    "SnowDeer Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//
//            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
//                    .createNotificationChannel(channel);
//
//            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
//        } else {
//            builder = new NotificationCompat.Builder(this);
//        }
//
//        builder.setSmallIcon(R.drawable.ic_launcher)
//                .setContentIntent(pendingIntent)
//                .setContentTitle(getString(R.string.app_name)).setPriority(Notification.PRIORITY_MIN)
//                .setGroup(Define.NOTIFICATION_ID_FOREGROUND_GROUP_KEY)
//                .setContentText(getString(R.string.notification_send_location_cancel))
//                .setContentIntent(pendingIntent).build();
//
//        startForeground(Define.ALARM_ID_STOP_LOCATION, builder.build());
//    }
}