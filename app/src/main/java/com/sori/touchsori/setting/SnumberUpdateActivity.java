package com.sori.touchsori.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.sori.touchsori.R;
import com.sori.touchsori.base.BaseActivity;

public class SnumberUpdateActivity extends BaseActivity {

    private TextView updateDone;
    private EditText updateEdt;

    String sNumber;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_snumber_update_view);

        sNumber = getIntent().getStringExtra("sNumber");
        initView();

    }

    private void initView() {
        updateDone = findViewById(R.id.setting_update_done_tv);
        updateEdt = findViewById(R.id.setting_snumber_update_edt);
        updateEdt.setText(sNumber);


        updateDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("sNumber" , updateEdt.getText().toString());
                setResult(RESULT_OK , intent);
                finish();
            }
        });
    }
}
