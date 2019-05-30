package com.sori.touchsori.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.service.ServiceUtil;
import com.sori.touchsori.utill.LogUtil;

/**
 * Created by Dongnam on 2017. 5. 19..
 */

public class BootReceiver extends BroadcastReceiver {
    private static final String TAG = BootReceiver.class.getSimpleName();   // 디버그 태그
    private Context mContext;                                               // 콘텍스트
    private SoriApplication mApp;                                      // 전역 (Application) 변수

    @Override
    public void onReceive(final Context context, Intent intent) {
        LogUtil.i(TAG, "onReceive() -> Start !!!");
        if (intent == null) return;
        LogUtil.d(TAG, "onReceive() -> action : " + intent.getAction());

        // 콘텍스트
        mContext = context.getApplicationContext();
        // 전역 (Application) 변수
        if (mApp == null) mApp = (SoriApplication) mContext;

        if (Intent.ACTION_BOOT_COMPLETED.equalsIgnoreCase(intent.getAction())) {
            mApp.startTouchsoriService(TAG);

            // 모니터링 서비스 시작
            ServiceUtil serviceUtil = new ServiceUtil();
            serviceUtil.startMonitorService(mContext);


        }
    }
}
