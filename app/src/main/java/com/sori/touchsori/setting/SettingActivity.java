package com.sori.touchsori.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sori.touchsori.R;
import com.sori.touchsori.base.BaseActivity;
import com.sori.touchsori.intro.IntroActivity;
import com.sori.touchsori.utill.CustomToolbar;
import com.sori.touchsori.utill.ErrorCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingActivity extends BaseActivity implements View.OnClickListener {


    CustomToolbar settingToolbar;
    LinearLayout countryLl, snumberLl;
    TextView countyTv, snumberTv;

    String sNumber;

    private int SNUMBER_REQUEST_CODE = 3000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting_view);

        initView();
    }

    private void initView() {
        settingToolbar = findViewById(R.id.setting_toolbar);
        settingToolbar.setToolbarTitle("설정");

        countryLl = findViewById(R.id.setting_country_ll);
        snumberLl = findViewById(R.id.setting_snumber_ll);

        countyTv = findViewById(R.id.setting_country_tv);
        snumberTv = findViewById(R.id.setting_snumber_tv);
        sNumber = utils.getSerialNumber();
        snumberTv.setText(sNumber);
        countryLl.setOnClickListener(this);
        //snumberLl.setOnClickListener(this);



    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_country_ll: {
                //   Intent intent = new Intent()
                showMessage("업데이트 중입니다.");
                break;
            }

            case R.id.setting_snumber_ll: {
                Intent intent = new Intent(SettingActivity.this, SnumberUpdateActivity.class);
                intent.putExtra("sNumber", snumberTv.getText().toString());
                startActivityForResult(intent, SNUMBER_REQUEST_CODE);
                break;
            }


        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if (resultCode == RESULT_OK) {
            if (requestCode == SNUMBER_REQUEST_CODE) {
                snumberTv.setText(data.getStringExtra("sNumber"));
                String serialNumber = snumberTv.getText().toString();

                int error = utils.checkValidationLongSerial(serialNumber);
                switch (error) {

                    case ErrorCode.ERROR_EMPTY_SERIAL:
                        showMessage("serial error !");
                        return;
                    case ErrorCode.ERROR_LENGTH_SERIAL:
                        showMessage("serial error !");
                        return;
                    case ErrorCode.ERROR_INVALID_SERIAL:
                        showMessage("serial error !");
                        return;
                    default:
                        break;
                }

                deviceObj.addProperty("serialNo", snumberTv.getText().toString());
                utils.setDeviceInfo(deviceObj.toString());
                utils.setSerialNumber(serialNumber);

                new IntroActivity().serialRegist();
            //    updateSNumberApi();

            }
        }

    }
//
//    private void updateSNumberApi() {
//        JSONObject jsonObject = new JSONObject();
//        try {
//            jsonObject.put("name", deviceObj.get("username").getAsString());
//            jsonObject.put("id", deviceObj.get("password").getAsString());
//
//            apiUtil.updateDiviceInfo(jsonObject).enqueue(new Callback<Void>() {
//                @Override
//                public void onResponse(Call<Void> call, Response<Void> response) {
//                    if (response.isSuccessful()) {
//                        showMessage("수정 되었습니다.");
//                    } else {
//                        try {
//                            String message;
//                            String code;
//                            JSONObject jsonError = new JSONObject(response.errorBody().string());
//                            code = jsonError.getString("code");
//                            if (code.equals("1201")){
//                                refreshToken();
//
//                            }
//                            message = jsonError.getString("message");
//                            showMessage(message);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Call<Void> call, Throwable t) {
//                    t.printStackTrace();
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
