package com.sori.touchsori.receiver;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.Gravity;

import com.sori.touchsori.R;
import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.service.GyroService;
import com.sori.touchsori.service.ServiceUtil;
import com.sori.touchsori.service.TouchMessageService;
import com.sori.touchsori.utill.Define;
import com.sori.touchsori.utill.DeviceUtil;
import com.sori.touchsori.utill.EtcUtil;
import com.sori.touchsori.utill.LogUtil;
import com.sori.touchsori.utill.MediaUtil;
import com.sori.touchsori.utill.WakeLockUtil;


import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;
import static com.sori.touchsori.utill.Define.ALARM_ID_SEND_LOCATION;
import static com.sori.touchsori.utill.Define.TOUCH_ACTION_EMERGNECY;


/**
 * Created by Dongnam on 2017. 6. 15..
 */

public class EmergencyRecevier extends BroadcastReceiver {
    private static final String TAG = EmergencyRecevier.class.getSimpleName();  // 디버그 태그
    private SoriApplication mApp;                                          // 전역 (Application) 변수
    private Context mContext;                                                   // 콘텍스트

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i(TAG, "onReceive() -> Start !!!");

        // 콘텍스트
        mContext = context.getApplicationContext();
        LogUtil.i(TAG, "onReceive() -> mContext: " + mContext);



        if (intent == null) return;
        LogUtil.d(TAG, "onReceive() -> action : " + intent.getAction());


        // 전역 (Application) 변수
        LogUtil.d(TAG, "onReceive() -> mApp : " + mApp);
        if (mApp == null) mApp = (SoriApplication) mContext;
        LogUtil.d(TAG, "onReceive() -> mApp : " + mApp);

//        LogUtil.d(TAG, "onReceive() ->  getEmergencyInfo() : " +  mApp.getConfig().getEmergencyInfo());
//        ItemTime item = null;
//        if(null != mApp.getConfig().getEmergencyInfo()) {
//            if (null != mApp.getConfig().getEmergencyInfo().times && !mApp.getConfig().getEmergencyInfo().times.isEmpty()) {
//                item = mApp.getConfig().getEmergencyInfo().times.get(0);
//                if (false == item.getSelected() && false == mApp.getConfig().getEmergencySelectedPopupCheck()) {
//                    mApp.onAlertDefaultDialog(mContext.getString(R.string.emergcncy_selected_time_check_msg));
//                }
//            }
//        }
        if (Define.ACTION_EMERGENCY_TIME_START.equalsIgnoreCase(intent.getAction())) {
            // 화면 깨우기
            WakeLockUtil.wakeLockWithScreenOn(mContext);
            WakeLockUtil.releaseWakeLock();


            Locale systemLocale = mContext.getResources().getConfiguration().locale;
            String strLanguage = systemLocale.getLanguage();
            MediaUtil.getInstance(mContext).soundStartFromFileResource(R.raw.emergencystart_ko, false, null);

//            if(mApp.getConfig().getHeadsetPlug()) {
//                mApp.onAlertDialog(mContext.getString(R.string.earphone_plug_msg), Gravity.LEFT);
//
//                // Media Button Check Service 시작
//                intent = new Intent(mContext, MediaService.class);
//                intent.putExtra("start", true);
//                mContext.startService(intent);
//            }

//            long timeEnd = getTimestampTimeEndNext();
//            mApp.getConfig().setTimeEnd(timeEnd);
//            LogUtil.d(TAG, "onReceive() -> getTimeEnd : " + mApp.getConfig().getTimeEnd());
//            FileUtil.writeLog(mContext, TAG,  "onReceive() -> getTimeEnd : " + mApp.getConfig().getTimeEnd());
//
//            // 안심귀가 종료 시간 알람 등록
//            mApp.registerEmergencyEndAlarm();
//            FileUtil.writeLog(mContext, TAG, "ACTION_EMERGENCY_TIME_START registerEmergencyEndAlarm!!");

            //  터치소리 서비스 시작
            ServiceUtil serviceUtil = new ServiceUtil();
            serviceUtil.startTouchSoriService(mContext, TOUCH_ACTION_EMERGNECY, "start", Define.TOUCH_SERVICE_TYPE_START);


        } else if (Define.ACTION_EMERGENCY_TIME_END.equalsIgnoreCase(intent.getAction())) {

            // 화면 깨우기
            WakeLockUtil.wakeLockWithScreenOn(mContext);
            WakeLockUtil.releaseWakeLock();


//            long timeStart = getTimestampTimeStartNext();
//            mApp.getConfig().setTimeStart(timeStart);
//            LogUtil.d(TAG, "onReceive() -> getTimeStart : " + mApp.getConfig().getTimeStart());
////            FileUtil.writeLog(mContext, TAG, "onReceive() -> getTimeStart : " +  mApp.getConfig().getTimeStart());
//
//            // 안심귀가 시작시간 알람 등록
//            mApp.registerEmergencyStartAlarm();
//

            //서비스 종료 시 Gyroscope event도 종료
            if(EtcUtil.isGyroTouchServiceStopDevice() && (false == mApp.getIsSoundParserStop())) {
                GyroService.getInstance(mContext).stopGyronfo();
            }

//            // 미디어 버튼 서비스 종료
//            intent = new Intent(mContext, MediaService.class);
//            intent.putExtra("start", false);
//            mContext.startService(intent);
//            mContext.stopService(new Intent(mContext, MediaPlayerButtonService.class));

            ServiceUtil serviceUtil = new ServiceUtil();
            serviceUtil.startTouchSoriService(mContext, TOUCH_ACTION_EMERGNECY, "start", Define.TOUCH_SERVICE_TYPE_END);


        } else if (Define.ACTION_SEND_LOCATION.equals(intent.getAction())) { // 위치정보 전송
            // 전역 (Application) 변수
            if (mApp == null) mApp = (SoriApplication) mContext;
            LogUtil.d(TAG, "getLocationCount : " + mApp.getLocationCount());
            if (mApp.getLocationCount() > 3
                    || mApp.getLocationCount() == -1) {
                mApp.setLocationCount(-1);

                // 위치정보 전송 알람 해제
                unregisterLocationAlarm();

                //위치 전송 메시지 플래그 해지
                mApp.setMessageSending(false);

                ServiceUtil serviceUtil = new ServiceUtil();
                serviceUtil.startTouchSoriService(mContext, TOUCH_ACTION_EMERGNECY, "start", Define.TOUCH_SERVICE_TYPE_START);
            } else {
                // 메시지 서비스 시작
                intent = new Intent(mContext, TouchMessageService.class);
                intent.setAction(Define.MSG_SERVICE_ACTION_LOCATION);
                mContext.startService(intent);
            }
        }

     if (Define.ACTION_SEND_LOCATION.equals(intent.getAction())) { // 위치정보 전송
            // 전역 (Application) 변수
            if (mApp == null) mApp = (SoriApplication) mContext;
            LogUtil.d(TAG, "getLocationCount : " + mApp.getLocationCount());
            if (mApp.getLocationCount() > 3
                    || mApp.getLocationCount() == -1) {
                mApp.setLocationCount(-1);

                // 위치정보 전송 알람 해제
                unregisterLocationAlarm();

                //위치 전송 메시지 플래그 해지
                mApp.setMessageSending(false);


                mApp.utils.startTouchSoriService(mContext, TOUCH_ACTION_EMERGNECY, "start", Define.TOUCH_SERVICE_TYPE_START);
            } else {
                // 메시지 서비스 시작
                intent = new Intent(mContext, TouchMessageService.class);
                intent.setAction(Define.MSG_SERVICE_ACTION_LOCATION);
                mContext.startService(intent);
            }
        }
    }

    /**
     * 위치정보 전송 알람 해제
     */
    private void unregisterLocationAlarm() {
        LogUtil.i(TAG, "unregisterLocationAlarm() -> Start !!!");

        Intent intent = new Intent(mContext, SoundReceiver.class);
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
     * EmergencyTime 시작 시 시작함을 소리로 알려준다.
     * 진동 등 설정이 된 소리가 나도록 변경 후 원상 복구한다.
     */
    private void emergencyTimeStartPlaySound() {
        final int orignRingerMode = DeviceUtil.getAudioRingerMode(mContext);
        DeviceUtil.setAudioRingerMode(mContext, AudioManager.RINGER_MODE_NORMAL);
        final int orignMusicStreamVolume = DeviceUtil.getStremMusicVolume(mContext);
        DeviceUtil.setNormalStremMusicVolume(mContext);

        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.emergencystart_ko);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                DeviceUtil.setAudioRingerMode(mContext, orignRingerMode);
                DeviceUtil.setStremMusicVolume(mContext, orignMusicStreamVolume);
            }
        });
    }

}
