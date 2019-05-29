package com.sori.touchsori.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.task.SoundPaserTask;
import com.sori.touchsori.utill.LogUtil;

import static com.sori.touchsori.service.TouchService.callbackSound;
import static com.sori.touchsori.utill.Define.ACTION_SOUND_PARSER_RUNNING;

/**
 * Created by Dongnam on 2017. 5. 22..
 */

public class SoundReceiver extends BroadcastReceiver {
    private static final String TAG = SoundReceiver.class.getSimpleName();  // 디버그 태그
    private SoriApplication mApp;                                      // 전역 (Application) 변수
    private Context mContext;                                               // 콘텍스트

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.i(TAG, "onReceive() -> Start !!!");

        // 콘텍스트
        mContext = context.getApplicationContext();


        if (intent == null) return;
        LogUtil.d(TAG, "onReceive() -> action : " + intent.getAction());


        // 전역 (Application) 변수
        if (mApp == null) mApp = (SoriApplication) mContext;
        LogUtil.d(TAG, "onReceive() -> getIsServiceStop : " + mApp.getIsServiceStop());



        if (ACTION_SOUND_PARSER_RUNNING.equals(intent.getAction())) {
            LogUtil.d(TAG, "onReceive() -> callbackSound : " + callbackSound);
//            if (!mApp.getIsServiceStop() && null != callbackSound) {
            if (!mApp.getIsServiceStop() ) {
                // 버튼 타입
                int buttonType = mApp.utils.getButtonType();
                LogUtil.d(TAG, "onReceive() -> buttonType : " + buttonType);
                new SoundPaserTask(callbackSound, mContext).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, buttonType);
            }
        }
    }
}