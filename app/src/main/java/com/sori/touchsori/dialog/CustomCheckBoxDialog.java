package com.sori.touchsori.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sori.touchsori.R;
import com.sori.touchsori.data.ApiDefault;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomCheckBoxDialog extends Dialog {

    CustomCheckBoxDialog mCustomCheckBoxDialog;

    CheckBox m1Cv , m2Cv , w1Cv , w2Cv;
    Context mContext;
    TextView noTv , okTv;

    public CustomCheckBoxDialog(@NonNull Context context) {
        super(context ,  android.R.style.Theme_Translucent_NoTitleBar);
        mContext = context;
    }

    public CustomCheckBoxDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomCheckBoxDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.custom_dialog_checkbox);
        mCustomCheckBoxDialog = this;

        okTv = findViewById(R.id.dialog_ok_tv);
        noTv = findViewById(R.id.dialog_no_tv);

        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //api insert...
            }
        });

        noTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomCheckBoxDialog.dismiss();
            }
        });

        m1Cv = findViewById(R.id.m10_cb);
        m2Cv = findViewById(R.id.m20_cb);

        w1Cv = findViewById(R.id.w1_cb);
        w2Cv = findViewById(R.id.w5_cb);

        m1Cv.setOnCheckedChangeListener(onCheckedChangeListener);
        m2Cv.setOnCheckedChangeListener(onCheckedChangeListener);
        w1Cv.setOnCheckedChangeListener(onCheckedChangeListener);
        w2Cv.setOnCheckedChangeListener(onCheckedChangeListener);

        // 체크박스 데이터 조회


    }

   public void defaultChecked(JsonObject jsonObject) {
        //데이터 값받고 ... 체크값 상세설정
   }

    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            switch (buttonView.getId()) {
                case R.id.m10_cb : {
                    if (isChecked) {

                    }else {

                    }
                }

                case R.id.m20_cb : {
                    if (isChecked) {

                    }else {

                    }
                }


                case R.id.w1_cb : {
                    if (isChecked) {

                    }else {

                    }
                }

                case R.id.w5_cb : {
                    if (isChecked) {

                    }else {

                    }
                }
            }
        }
    };
}
