package com.sori.touchsori.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sori.touchsori.R;
import com.sori.touchsori.activity.AnsimActivity;
import com.sori.touchsori.search.SearchActivity;

public class CustomEditTextDialog extends Dialog {

    CustomEditTextDialog mCustomEditTextDialog;
    LinearLayout editLl , tvLl;
    EditText nameEdt , numberEdt;
    TextView okTv , noTv;
    Context mContext;
    String flag = "tv";

    public CustomEditTextDialog(@NonNull Context context) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);

        mContext = context;
    }

    public CustomEditTextDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomEditTextDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.custom_dialog_edt);
        mCustomEditTextDialog = this;

        editLl = findViewById(R.id.dialog_edt_ll);
        tvLl = findViewById(R.id.dialog_tv_ll);
        nameEdt = findViewById(R.id.dialog_name_edt);
        numberEdt = findViewById(R.id.dialog_phoneNumber_edt);
        okTv = findViewById(R.id.dialog_ok_tv);
        noTv = findViewById(R.id.dialog_no_tv);

        okTv.setText(mContext.getResources().getString(R.string.sms_with));
        noTv.setText(mContext.getResources().getString(R.string.selfinsert));

        tvLl.setVisibility(View.VISIBLE);
        editLl.setVisibility(View.GONE);

        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (okTv.getText().toString().equals(mContext.getResources().getString(R.string.sms_with))) {
                    // Intent ...
                    Intent intent = new Intent(mContext , SearchActivity.class);
                    intent.putExtra("type" , "contact");
                    mContext.startActivity(intent);
                    mCustomEditTextDialog.dismiss();
                }else {
                    // 저장 api
                    ((AnsimActivity) mContext).addContacts(nameEdt.getText().toString() , numberEdt.getText().toString());
                    mCustomEditTextDialog.dismiss();
                }

            }
        });
        noTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noTv.getText().toString().equals(mContext.getResources().getString(R.string.selfinsert))) {
                    tvLl.setVisibility(View.GONE);
                    editLl.setVisibility(View.VISIBLE);
                    okTv.setText(mContext.getResources().getString(R.string.ok));
                    noTv.setText(mContext.getResources().getString(R.string.cancle));
                    flag = "edt";
                }else {
                    mCustomEditTextDialog.dismiss();
                }
            }
        });



    }


}
