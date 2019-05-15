package com.sori.touchsori.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.sori.touchsori.R;

public class CustomAnsimDialog extends Dialog implements View.OnClickListener{
    TextView deleteTv , noTv  , titleTv , updateTv;
    Context mContext;
    CustomAnsimDialog mCustomAnsimDialog;

    public CustomAnsimDialog(@NonNull Context context) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
        this.mContext = context;
    }

    public CustomAnsimDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomAnsimDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.custom_ansim_dialog);
        mCustomAnsimDialog = this;

        deleteTv = findViewById(R.id.dialog_delete_tv);
        noTv = findViewById(R.id.dialog_no_tv);
        updateTv = findViewById(R.id.dialog_update_tv);
        titleTv = findViewById(R.id.dialog_title_tv);

        noTv.setOnClickListener(this);


        deleteTv.setText(mContext.getResources().getString(R.string.delete));
        noTv.setText(mContext.getResources().getString(R.string.cancle));
    }

    public void onClickDeleteListener (View.OnClickListener onClickListener) {
        deleteTv.setOnClickListener(onClickListener);
    }

    public void onClickUpdateListener (View.OnClickListener onClickListener) {
        updateTv.setOnClickListener(onClickListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_no_tv : {
                mCustomAnsimDialog.dismiss();
                break;
            }

        }
    }

}
