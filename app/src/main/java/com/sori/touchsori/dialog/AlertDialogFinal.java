package com.sori.touchsori.dialog;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sori.touchsori.R;
import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.base.BaseActivity;
import com.sori.touchsori.callback.CallbackAlert;
import com.sori.touchsori.intro.IntroActivity;
import com.sori.touchsori.receiver.EmergencyRecevier;
import com.sori.touchsori.utill.Define;
import com.sori.touchsori.utill.LogUtil;

import static com.sori.touchsori.utill.Define.ALARM_ID_SEND_LOCATION;


/**
 * Created by sgkim on 2017-03-27.
 */

public class AlertDialogFinal extends BaseActivity {
    private static final String TAG = AlertDialogFinal.class.getSimpleName();    // 디버그 태그

    public static TextView msgView;                                                 // 메시지 텍스트 뷰
    public static CallbackAlert callbackAlert;                                      // 알림창 콜백
    private  String message;                                                        // 메시지
    private SoriApplication mApp;                                              // 전역 (Application) 변수
    private Context mContext;                                                   // 콘텍스트

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setFinishOnTouchOutside(false);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_alert_dialog);

        // 콘텍스트
        mContext = getApplicationContext();

        // 전역 (Application) 변수
        if (mApp == null) mApp = (SoriApplication) getApplicationContext();

        // 메시지 텍스트뷰
        msgView = (TextView)findViewById(R.id.activity_alert_dialog_tv_message);


        // 인텐트
        Intent intent = getIntent();
        LogUtil.d(TAG,"onCreate() -> intent : " + intent.toString());
        if (intent == null) finish();

        // 메시지
        message = intent.getStringExtra("message");
        int gravity = Gravity.CENTER;
        if (TextUtils.isEmpty(message)) finish();
        msgView.setText(message);
//
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(msgView.getLayoutParams());
//        params.topMargin = ViewUtil.convertDpToPixels(18, mContext);
//        params.leftMargin = ViewUtil.convertDpToPixels(10, mContext);
//        params.rightMargin = ViewUtil.convertDpToPixels(10, mContext);
//        if(gravity == Gravity.LEFT) {
//            params.weight = 1.0f;
//            params.height = LinearLayout.LayoutParams.MATCH_PARENT;
//            params.gravity = Gravity.LEFT|Gravity.CENTER_VERTICAL;
//            msgView.setLayoutParams(params);
//        }else {
//            params.weight = 1.0f;
//            params.leftMargin = 0;
//            params.rightMargin = 0;
//            params.gravity = Gravity.CENTER;
//            msgView.setLayoutParams(params);
//        }
//        msgView.setGravity(gravity|Gravity.CENTER_VERTICAL);
//        msgView.setText(message);

        //  알림창 콜백
        callbackAlert = new CallbackAlert() {
            @Override
            public void OnConfirmPressed(boolean isPressed) {
                if (isPressed) finish();
            }
        };
    }

    /**
     * 확인버튼
     * @param v
     */
    public void onConfirm(View v) {
//        if (getString(R.string.gps_info_off_a).equals(message)) {
//            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//            intent.addCategory(Intent.CATEGORY_DEFAULT);
//            startActivity(intent);
//        } else if (getString(R.string.msg_info_multimessage).equals(message)) {
//            // 확인버튼 클릭 버튼
//            if (ImageViewActivity.callbackAlert != null) ImageViewActivity.callbackAlert.OnConfirmPressed(true);
//        } else if (message != null
//                && message.startsWith("이어폰을 해제합니다.")) {
//            // 확인버튼 클릭 버튼
//            if (HeadsetReceiver.callbackAlert != null) HeadsetReceiver.callbackAlert.OnConfirmPressed(true);
//        }else if (getString(R.string.notification_send_location_cancel).equals(message) ||
//                getString(R.string.finish_send_location_info).equals(message)) {
//
//            mApp.setLocationCount(-1);
//
//            // 위치전송 알림 해제
//            unregisterLocationAlarm();
//
//            //위치 전송 메시지 플래그 해지
//            mApp.setMessageSending(false);
//
//            // 터치소리 서비스 시작
//            mApp.startTouchsoriService(TAG);
//        }
        // 엑티비티 종료
        unregisterLocationAlarm();

        if (BaseActivity.foregroundCount > 1)  {
            Log.d(TAG ,"final popup ffff: : : :  ==== : : : : " + BaseActivity.foregroundCount);
            finish();
        }else {
            Log.d(TAG ,"final popup iiii: : : :  ==== : : : : " + BaseActivity.foregroundCount);
            Intent intent = new Intent(this , IntroActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
       //finish();



    }


    /**
     * 위치정보 전송 알람 해제
     */
    private void unregisterLocationAlarm() {
        LogUtil.i(TAG, "unregisterLocationAlarm() -> Start !!!");

        Intent intent = new Intent(AlertDialogFinal.this, EmergencyRecevier.class);
        intent.setAction(Define.ACTION_SEND_LOCATION);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                AlertDialogFinal.this,
                ALARM_ID_SEND_LOCATION,
                intent,
                0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }

    /**
     * 백버튼
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}