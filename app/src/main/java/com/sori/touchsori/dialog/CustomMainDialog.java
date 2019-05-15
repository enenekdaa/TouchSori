package com.sori.touchsori.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.sori.touchsori.MainActivity;
import com.sori.touchsori.R;

public class CustomMainDialog extends Dialog implements View.OnClickListener {

    CustomMainDialog mCustomMainDialog;

    TextView titleTv;
    TextView okTv;
    TextView noTv;
    TextView detailTv;

    TextView contentTv;


    public CustomMainDialog(@NonNull Context context) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);

    }

    public CustomMainDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomMainDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.custom_dialog);
        mCustomMainDialog = this;


        titleTv = findViewById(R.id.dialog_title_tv);
        contentTv = findViewById(R.id.dialog_content);
        okTv = findViewById(R.id.dialog_ok_tv);
        noTv = findViewById(R.id.dialog_no_tv);
        detailTv = findViewById(R.id.dialog_detail_tv);

        okTv.setOnClickListener(this);
        noTv.setOnClickListener(this);
        detailTv.setOnClickListener(this);

    }

    public void onClickOKListener (View.OnClickListener onClickListener) {
        okTv.setOnClickListener(onClickListener);
    }

    public void onClickDetailListener (View.OnClickListener onClickListener) {
        detailTv.setOnClickListener(onClickListener);
    }

    public void setDialogTitle(String title) {
        titleTv.setText(title);
    }

    public void setDialogContent(String content) {
        contentTv.setText(content);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.dialog_no_tv : {
                mCustomMainDialog.dismiss();
                break;
            }

        }
    }


}
