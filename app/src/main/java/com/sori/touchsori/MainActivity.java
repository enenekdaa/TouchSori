package com.sori.touchsori;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sori.touchsori.activity.AnsimActivity;
import com.sori.touchsori.activity.PolicyActivity;
import com.sori.touchsori.base.BaseActivity;
import com.sori.touchsori.intro.IntroActivity;
import com.sori.touchsori.setting.SettingActivity;
import com.sori.touchsori.signIn.SignInActivity;
import com.sori.touchsori.utill.LogUtil;

public class MainActivity extends BaseActivity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();

    private ImageView mainSettingImg;
    private TextView mainAnsimTv;

    private final long FINISH_INTERVAL_TIME = 2000;
    private long mBackPressedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        mainAnsimTv = findViewById(R.id.main_ansim_tv);
        mainSettingImg = findViewById(R.id.main_setting_img);

        mainAnsimTv.setOnClickListener(this);
        mainSettingImg.setOnClickListener(this);

        String serial = utils.getSerialNumber();
        if (utils.getSiren().equals("on")) {
        }else {
            utils.saveSiren("off");
        }
        boolean serialBoolean = utils.isRegistrated();
        LogUtil.d(": : : : : " , serial + " //////////////////////// " + serialBoolean);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_setting_img : {
                Intent intent = new Intent(MainActivity.this , SettingActivity.class);
                startActivity(intent);
                break;
            }

            case R.id.main_ansim_tv : {
                Intent intent = new Intent(MainActivity.this , AnsimActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (deviceObj != null && deviceObj.size() != 0){
            refreshToken();
        }

        if (soriApplication.isInitialized()) {
            soriApplication.startTouchsoriService(TAG);
        }

    }

    @Override
    public void onBackPressed() {

        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - mBackPressedTime;

        if (0 <= intervalTime && FINISH_INTERVAL_TIME >= intervalTime) {
            super.onBackPressed();
        } else {
            mBackPressedTime = tempTime;
            Toast.makeText(this , "뒤로 가기를 한번 더 누르면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }

    }
}

