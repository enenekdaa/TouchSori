package com.sori.touchsori.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.utill.LogUtil;
import com.sori.touchsori.utill.WakeLockUtil;

import static com.sori.touchsori.utill.Define.ACTION_WAKE_LOCK;


/**
 * Created by Dongnam on 2017. 6. 7..
 */

public class WakeLockReceiver extends BroadcastReceiver {
    private static final String TAG = WakeLockReceiver.class.getSimpleName();   // 디버그 태그
    private Context mContext;                                                   // 콘텍스트
    private SoriApplication mApp;                                          // 전역 (Application) 변수

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i(TAG, "onReceive() -> Start !!!");
        if (intent == null) return;
        LogUtil.d(TAG, "onReceive() -> action : " + intent.getAction());

        // 콘텍스트
        mContext = context.getApplicationContext();


        // 전역 (Application) 변수
        if (mApp == null) mApp = (SoriApplication) mContext;

        if (ACTION_WAKE_LOCK.equalsIgnoreCase(intent.getAction())) {
            // GCM Registration ID가 있고 충전중이 아니면,
//            if (mApp.getConfig().getGCMRegistratinID() != null
//                    && mApp.getConfig().getIsPowerDisconnected()) {
//                NotificationUtil.requestSendGoogleCloudNotification(mApp.getConfig().getGCMRegistratinID());
////                FileUtil.writeLog(mContext, TAG, intent.getAction());
//            }
            WakeLockUtil.wakeLockAcquire(mContext);

//            WakeLockUtil.releaseWakeLock();
//            FileUtil.writeLog(mContext, TAG, "releaseWakeLock !!!");
        }
    }
}