package com.sori.touchsori.search;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.sori.touchsori.R;

public class SearchAdapterDialog extends Dialog {

    SearchAdapterDialog mSearchAdapterDialog;

    TextView titleTv;
    TextView okTv;
    TextView noTv;

    public SearchAdapterDialog(@NonNull Context context) {
        super(context , android.R.style.Theme_Translucent_NoTitleBar);
    }

    public SearchAdapterDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected SearchAdapterDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_dialog);
        mSearchAdapterDialog = this;
        titleTv = findViewById(R.id.serach_dialog_title);
        okTv = findViewById(R.id.dialog_ok_tv);
        noTv = findViewById(R.id.dialog_no_tv);

        noTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchAdapterDialog.dismiss();
            }
        });
    }

    public void okDeleteListener(View.OnClickListener onClickListener) {
        okTv.setOnClickListener(onClickListener);
    }

    public void setTitleTv(String title){
        titleTv.setText(title);
    }

}
