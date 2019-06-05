package com.sori.touchsori.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.text.TextUtils;

import com.sori.touchsori.MainActivity;
import com.sori.touchsori.R;
import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.intro.IntroActivity;
import com.sori.touchsori.receiver.EmergencyRecevier;
import com.sori.touchsori.utill.Define;
import com.sori.touchsori.utill.EtcUtil;
import com.sori.touchsori.utill.LocationInfo;
import com.sori.touchsori.utill.LocationUtil;
import com.sori.touchsori.utill.LogUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import static com.sori.touchsori.utill.Define.ALARM_ID_SEND_LOCATION;
import static com.sori.touchsori.utill.Define.MSG_SERVICE_ACTION_EMERGENCY;
import static com.sori.touchsori.utill.Define.MSG_SERVICE_ACTION_LOCATION;


public class TouchMessageService extends Service {
    private static final String TAG = TouchMessageService.class.getSimpleName();   // 디버그 태그
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    private Context mContext;
    private SoriApplication mApp;
    MediaPlayer mediaPlayer;
    public TouchMessageService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action = intent.getAction();
        LogUtil.d(TAG, "onStartCommand() -> action :" + action);

        // 콘텍스트
        mContext = getApplicationContext();
        // 전역 (Application) 변수
        mApp = (SoriApplication) mContext;

        startForegroundNotificationOn();
        // 터치소리 서비스 중지
        mApp.setIsServiceStop(true);


        // 긴급상황 수신자 리스트
     //   int cnt = eInfo.members.size();
        ArrayList<String> numList = mApp.utils.getSosList();
        int cnt = numList.size();
        LogUtil.d(TAG, "onStartCommand() -> cnt : " + cnt);
        mediaPlayer = MediaPlayer.create(mContext, R.raw.siren);
        if (cnt > 0) {

            if (mApp.utils.getSiren().matches("on")) { // 사이렌 사용

                mediaPlayer.setLooping(false);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
//                        TouchMessage touchMsg = new TouchMessage(mContext, 1, "", eInfo.members);
//                        touchMsg.makeMessage();
                        mApp.showMessage("media completion");
                    }
                });
                mediaPlayer.start();
            } else {
                mApp.showMessage("media completion");
            }
            // 팝업 ...
        }else {
            mApp.showMessage("긴급 수신자 설정이 되있지않습니다 .");
        }
        if (MSG_SERVICE_ACTION_LOCATION.equals(action)) {
            if (mApp.getLocationCount() == -1) {
                mApp.setLocationCount(-1);

                // 위치정보 전송 알람 해제
                unregisterLocationAlarm();

                //위치 전송 메시지 플래그 해지
                mApp.setMessageSending(false);

                // 터치소리 서비스 시작
//                startTouchsoriService();
                mApp.startTouchsoriService(TAG);
                PowerManager pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
//                if(EtcUtil.isGyroTouchServiceStopDevice() && (false == pm.isInteractive())) {
//                    GyroService.getInstance(mContext).startGyroInfo();
//                }
            } else {
                if (cnt > 0) {
                    mLocationHandler.sendEmptyMessage(LOCATION_UPDATE_START);
                } else {
                    mApp.setLocationCount(-1);

                    // 위치정보 전송 알람 해제
                    unregisterLocationAlarm();

                    //위치 전송 메시지 플래그 해지
                    mApp.setMessageSending(false);

                    // 터치소리 서비스 시작
//                    startTouchsoriService();
                    mApp.startTouchsoriService(TAG);
                    PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//                    if(EtcUtil.isGyroTouchServiceStopDevice() && (false == pm.isInteractive())) {
//                        GyroService.getInstance(mContext).startGyroInfo();
//                    }
                }
            }
        }else if (MSG_SERVICE_ACTION_EMERGENCY.equals(action)) {
            mLocationHandler.sendEmptyMessage(LOCATION_SEND_START);
        }
        stopSelf();

        return START_NOT_STICKY;
    }

    /**
     * 로케이션정보 전송 타스크
     */
    private class SendLocationTask extends AsyncTask<ArrayList<String>, Boolean, Boolean> {
        @Override
        protected Boolean doInBackground(ArrayList<String>... params) {
            boolean isSuccess = false;
   //         Looper.prepare();
            try {
                LocationInfo mLocation = LocationInfo.getInstance(mContext);
                Location location = mLocation.getCurrentLocation();
                String address = getAddress(location);
                String mapUrl = "";
                mapUrl = LocationUtil.getMapUrl(mContext, location, address);

                StringBuilder msgBuilder = new StringBuilder();
                msgBuilder.append("Location");
                msgBuilder.append("\n");
                msgBuilder.append(mapUrl);
                String msg = msgBuilder.toString();

                LogUtil.d(TAG, "SendLocationTask() -> msg : " + msg);
                for (int i = 0; i < params[0].size(); i++) {
                    isSuccess = sendMMS(mContext, params[0].get(i), msg);
                    LogUtil.d(TAG, "toNumbers : " + params[0].get(i));
                }
            } catch (Exception e) {
                LogUtil.e(TAG, e.getMessage());
            }
            return isSuccess;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            LogUtil.d(TAG, "onPostExecute() result : " + result);
            mApp.setLocationCount(mApp.getLocationCount() + 1);
            LogUtil.d(TAG, "onPostExecute() getLocationCount : " + mApp.getLocationCount());
            //     LogUtil.d(TAG, "onPostExecute() getEmergencyLocationCount : " + mApp.getConfig().getEmergencyLocationCount());
            if (mApp.getLocationCount() != -1) {
                if (result.equals(true)) {
                    mApp.onAlertDialog("위치 전송이 발송되었습니다.");
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }else {
                    mApp.onAlertDialog("위치 전송이 실패하였습니다.");
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.stop();
                    }
                }
                Intent locationIntent = new Intent(mContext, LocationService.class);
                mContext.startService(locationIntent);

                // 터치소리 서비스 시작
                mApp.startTouchsoriService(TAG);
             //   registerLocationAlarm();
            } else {
                mApp.setLocationCount(-1);
                // 노티피케이션 해제
//                NotificationUtil.cancelLocalNotification(mContext, Define.ALARM_ID_STOP_LOCATION);

                Intent locationIntent = new Intent(mContext, LocationService.class);
                mContext.stopService(locationIntent);

                // 터치소리 서비스 시작
                mApp.startTouchsoriService(TAG);

                PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//                if(EtcUtil.isGyroTouchServiceStopDevice() && (false == pm.isInteractive())) {
//                    GyroService.getInstance(mContext).startGyroInfo();
//                }
            }
        }
    }

    /**
     * 주소 가져오기
     *
     * @param location
     * @return
     */
    private String getAddress(Location location) {
        String ret_val = "";
        if (location == null) {
            return "";
        }

        double lat = location.getLatitude();
        double lon = location.getLongitude();

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            List<Address> addrs = geocoder.getFromLocation(lat, lon, 1);
            String addr = addrs.get(0).getAddressLine(0);
            if (!TextUtils.isEmpty(addr)) {
                String rAddr = addr.replace("남한", "");
                ret_val = rAddr.replace("대한민국", "");
            }
        } catch (Exception e) {
            ret_val = "";
        }
        return ret_val.trim();
    }


    /**
     * SMS 전송
     *
     * @param toNumber
     * @param msg
     */
    public static boolean sendMMS(Context cxt, String toNumber, String msg) {
        if (TextUtils.isEmpty(msg) || TextUtils.isEmpty(toNumber)) {
            return false;
        }
        SmsManager smsManager = SmsManager.getDefault();

        SoriApplication app = (SoriApplication) cxt;
//        HashMap<String, String> hashMap = app.getConfig().getEmergencyCountry();
//        if (hashMap == null) app.getConfig().initEmergencyCountry();

        //      String code = hashMap.get(Define.KEY_COUNTRY_CODE);
//        String code = "082";
//        if (toNumber.startsWith("0")) {
//            toNumber = toNumber.substring(1);
//        }
//        toNumber = "+" + code + toNumber;

        ArrayList<String> parts = smsManager.divideMessage(msg);
        smsManager.sendMultipartTextMessage(toNumber, null, parts, null, null);

        return true;
    }

    /**
     * 위치전송 알람 등록
     */
    private void registerLocationAlarm() {
        LogUtil.i(TAG, "registerLocationAlarm() -> Start !!!");

        Intent intent = new Intent(mContext, EmergencyRecevier.class);
        intent.setAction(Define.ACTION_SEND_LOCATION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext,
                ALARM_ID_SEND_LOCATION,
                intent,
                0);
        long time = System.currentTimeMillis();
        time += Define.LOCATION_SEND_TIME;

        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {               // 마시멜로우 (Ver.6.0 이상)
            long triggerTime = System.currentTimeMillis() + Define.LOCATION_SEND_TIME;
            AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(triggerTime, pendingIntent);
            alarmManager.setAlarmClock(info, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {   // 키켓 (Ver.4.4 이상)
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        } else {                                                            // 기타
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        }
    }

    /**
     * 위치정보 전송 알람 해제
     */
    private void unregisterLocationAlarm() {
        LogUtil.i(TAG, "unregisterLocationAlarm() -> Start !!!");

        Intent intent = new Intent(mContext, EmergencyRecevier.class);
        intent.setAction(Define.ACTION_SEND_LOCATION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                mContext,
                ALARM_ID_SEND_LOCATION,
                intent,
                0);
        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * 위치 정보 핸들러
     */
    private final int LOCATION_UPDATE_START = 1;   // LOCATION UPDATE START
    private final int LOCATION_SEND_START = 2;     // LOCATION SEND START

    private Handler mLocationHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case LOCATION_UPDATE_START:
                    LocationInfo.getInstance(mContext).updateLocationInfo();
                    sendEmptyMessageDelayed(LOCATION_SEND_START, Define.LOCATION_UPATE_TIME);
                    break;

                case LOCATION_SEND_START:
                //    TouchConfig.EmergencyInfo emergencyInfo = mApp.getConfig().getEmergencyInfo();

                    SendLocationTask sendLocationTask = new SendLocationTask();
                    sendLocationTask.execute(mApp.utils.getSosList());
                 //   mApp.startTouchsoriService(TAG);
                    break;
            }

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

    }



    /**
     * 서비스 시작 Notification
     */
    public void startForegroundNotificationOn() {

        // 모니터링 서비스  노티피케이션 취소
//        ServiceUtil serviceUtil = new ServiceUtil();
//        serviceUtil.stopMonitorService(mContext);

        Intent notificationIntent = new Intent(this, IntroActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);


        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "_service_channel",
                    NotificationManager.IMPORTANCE_MIN);

            channel.setSound(null, null);
            channel.setShowBadge(false);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setSmallIcon(R.mipmap.ic_launcher)

                .setContentTitle(getString(R.string.app_name)).setPriority(Notification.PRIORITY_MIN)
                .setGroup(Define.NOTIFICATION_ID_FOREGROUND_GROUP_KEY)
                .setContentText("터치소리 실행중입니다.")
                .setContentIntent(notificationPendingIntent).build();

        startForeground(Define.NOTIFICATION_ID_FOREGROUND_SERVICE, builder.build());
    }

    /**
     * 서비스 중지 Notification
     */
    public void startForegroundNotificationOff() {
        // 모니터링 서비스  노티피케이션 취소
//        ServiceUtil serviceUtil = new ServiceUtil();
//        serviceUtil.stopMonitorService(mContext);

        Intent notificationIntent = new Intent(this, IntroActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "service_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "_service_channel",
                    NotificationManager.IMPORTANCE_MIN);

            channel.setSound(null, null);
            channel.setShowBadge(false);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }

        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name)).setPriority(Notification.PRIORITY_MIN)
                .setGroup(Define.NOTIFICATION_ID_FOREGROUND_GROUP_KEY)
                .setContentText("stop..")
                .setContentIntent(notificationPendingIntent).build();

        startForeground(Define.NOTIFICATION_ID_FOREGROUND_SERVICE, builder.build());
    }

}
