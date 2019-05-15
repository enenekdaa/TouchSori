package com.sori.touchsori.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.sori.touchsori.R;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CustomAnsimUpdateDialog extends Dialog {

    CustomAnsimUpdateDialog mCustomAnsimUpdateDialog;

    TextView startTimeTv, endTimeTv;
    CheckBox time24Cb;
    TimePickerDialog timePicker;

    TextView okTv , noTv;

    Context mContext;

    public CustomAnsimUpdateDialog(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public CustomAnsimUpdateDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CustomAnsimUpdateDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.5f;
        getWindow().setAttributes(lpWindow);
        setContentView(R.layout.custom_ansim_update_dialog);

        mCustomAnsimUpdateDialog = this;

        final Calendar calendar = Calendar.getInstance();
        okTv = findViewById(R.id.dialog_ok_tv);
        noTv = findViewById(R.id.dialog_no_tv);
        startTimeTv = findViewById(R.id.ansim_dialog_start_time_tv);
        time24Cb = findViewById(R.id.dialog_cb_24);
        endTimeTv = findViewById(R.id.ansim_dialog_end_time_tv);


        noTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCustomAnsimUpdateDialog.dismiss();
            }
        });

        okTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        startTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                timePicker = new TimePickerDialog(mContext, AlertDialog.THEME_HOLO_DARK, startlistener, hour, minute, false);
                timePicker.show();
            }
        });

        endTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);
                timePicker = new TimePickerDialog(mContext, android.R.style.Theme_Holo_Light_Dialog, endlistener, hour, minute, false);
                timePicker.show();
            }
        });


        time24Cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switch (buttonView.getId()) {
                    case R.id.dialog_cb_24: {
                        if (isChecked) {
                            startTimeTv.setText("오전 0:00");
                            endTimeTv.setText("오전 0:00");
                            startTimeTv.setEnabled(false);
                            endTimeTv.setEnabled(false);
                        } else {
                            startTimeTv.setText("출발시간 설정");
                            endTimeTv.setText("도착시간 설정");
                            startTimeTv.setEnabled(true);
                            endTimeTv.setEnabled(true);
                        }
                    }
                }
            }
        });

    }

    private TimePickerDialog.OnTimeSetListener startlistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (hourOfDay < 12) {
                startTimeTv.setText("오전 " + String.valueOf(hourOfDay) + " : " + String.valueOf(minute));
            }else {
                startTimeTv.setText("오후 " + String.valueOf(hourOfDay - 12) + " : " + String.valueOf(minute));
            }

        }
    };

    private TimePickerDialog.OnTimeSetListener endlistener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if (hourOfDay < 12) {
                endTimeTv.setText("오전 " + String.valueOf(hourOfDay) + " : " + String.valueOf(minute));
            }else {
                endTimeTv.setText("오후 " + String.valueOf(hourOfDay - 12) + " : " + String.valueOf(minute));
            }
        }
    };

}
