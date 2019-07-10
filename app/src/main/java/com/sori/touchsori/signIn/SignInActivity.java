package com.sori.touchsori.signIn;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.sori.touchsori.MainActivity;
import com.sori.touchsori.R;
import com.sori.touchsori.activity.PolicyActivity;
import com.sori.touchsori.base.BaseActivity;
import com.sori.touchsori.data.ApiAuthorizationData;
import com.sori.touchsori.data.ApiDeviceData;
import com.sori.touchsori.dialog.CustomMainDialog;
import com.sori.touchsori.utill.ErrorCode;
import com.sori.touchsori.utill.StringUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SignInActivity extends BaseActivity implements View.OnClickListener {

    private TextView mainPolicyTv;
    private TextView mainNumberEdt;
    private TextView mainSNumberEdt;
    private TextView mainDoneTv;

    private String pNumber, sNumber;

    CustomMainDialog mCustomMainDialog;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);

        initView();

    }

    private void initView() {
        mainDoneTv = findViewById(R.id.main_done_tv);
        mainNumberEdt = findViewById(R.id.main_number_edt);
        mainSNumberEdt = findViewById(R.id.main_snumber_edt);
        mainPolicyTv = findViewById(R.id.main_policy_tv);

        mainDoneTv.setOnClickListener(this);
        mainPolicyTv.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.main_done_tv: {

                if (mainNumberEdt.length() < 11) {
                    if (mainNumberEdt.length() == 0) {
                        showMessage("핸드폰번호를 입력해주세요");
                        return;
                    } else {
                        showMessage("핸드폰 번호를 잘못입력하였습니다.");
                        return;
                    }
                } else if (mainSNumberEdt.length() < 9) {
                    if (mainSNumberEdt.length() == 0) {
                        showMessage("시리얼 번호를 입력해주세요");
                        return;
                    } else {
                        showMessage("시리얼 번호를 잘못입력하였습니다.");
                        return;
                    }
                } else {
                    pNumber = mainNumberEdt.getText().toString();
                    sNumber = mainSNumberEdt.getText().toString();
                    int error = utils.checkValidationLongSerial(sNumber);
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
                    showDialog();
                }

                break;
            }

            case R.id.main_policy_tv: {

                Intent intent = new Intent(SignInActivity.this, PolicyActivity.class);
                intent.putExtra("textType", "1");
                startActivity(intent);

                break;
            }

        }
    }

    private void showDialog() {
        mCustomMainDialog = new CustomMainDialog(this);
        mCustomMainDialog.setCancelable(true);
        mCustomMainDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mCustomMainDialog.show();

        mCustomMainDialog.setDialogTitle("위치기반서비스 이용약관");
        mCustomMainDialog.setDialogContent(getResources().getString(R.string.text2));

        mCustomMainDialog.onClickOKListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                userInsertApi();

            }
        });

        utils.dialogSizeSetting(this, mCustomMainDialog);

        mCustomMainDialog.onClickDetailListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignInActivity.this, PolicyActivity.class);
                intent.putExtra("textType", "2");
                startActivity(intent);
            }
        });


    }

    private void userInsertApi() {
        JSONObject jsonObject = new JSONObject();

        try {
            jsonObject.put("phoneNo", pNumber);
            jsonObject.put("deviceName", deviceObj.get("deviceName").getAsString());
            jsonObject.put("maker", deviceObj.get("maker").getAsString());
            jsonObject.put("imei", deviceObj.get("imei").getAsString());
            jsonObject.put("version", deviceObj.get("version").getAsString());
            jsonObject.put("iccid", deviceObj.get("iccid").getAsString());
            jsonObject.put("model", deviceObj.get("model").getAsString());
            jsonObject.put("serialNo", sNumber);
            deviceObj.addProperty("serialNo", sNumber);
            deviceObj.addProperty("phoneNo", pNumber);
            utils.setDeviceInfo(deviceObj.toString());
            jsonObject.put("userId", deviceObj.get("username").getAsString());
            jsonObject.put("passwd", deviceObj.get("password").getAsString());

            apiUtil.deviceAdd(jsonObject).enqueue(new Callback<ApiDeviceData>() {
                @Override
                public void onResponse(Call<ApiDeviceData> call, Response<ApiDeviceData> response) {
                    String message;
                    if (response.isSuccessful()) {
                        String deviceId = response.body().getDeviceId();
                        utils.setDeviceID(deviceId);
                        utils.setSerialNumber(deviceObj.get("serialNo").getAsString());
                        utils.setIsFirstLogin(true);
                        mCustomMainDialog.dismiss();
                        finish();
                    } else {
                        try {
                            JSONObject jsonError = new JSONObject(response.errorBody().string());
                            String code = jsonError.getString("code");
                            message = jsonError.getString("message");

                            mCustomMainDialog.dismiss();
                         //   System.exit(0);
                            if (code.equals("5802")) {
                                showMessage(message);

                            }else {
                                showMessage(message);
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<ApiDeviceData> call, Throwable t) {
                    t.printStackTrace();
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void goMain() {
        mCustomMainDialog.dismiss();

        JSONObject jsonObject = new JSONObject();
        try {
            //  jsonObject.put("username", utils.stringHash(deviceObj.get("username").getAsString()));
            jsonObject.put("username", deviceObj.get("username").getAsString());
            jsonObject.put("password", deviceObj.get("password").getAsString());


            apiUtil.loginPost(jsonObject).enqueue(new Callback<ApiAuthorizationData>() {
                @Override
                public void onResponse(Call<ApiAuthorizationData> call, Response<ApiAuthorizationData> response) {
                    String message;
                    if (response.isSuccessful()) {
                        String result = response.body().getAuthorization();

                        deviceObj.addProperty("deviceId" , response.body().getDeviceId());
                        deviceObj.addProperty("role" , response.body().getRole());
                        deviceObj.addProperty("uid" , response.body().getUid());

                        utils.setDeviceInfo(deviceObj.toString());
                        utils.setDeviceID(deviceObj.get("deviceId").getAsString());
                        utils.saveLoginToken(result);
                        utils.setIsFirstLogin(true);
                        finish();
                    } else {
                        utils.setIsFirstLogin(true);
                        finish();
                        try {
                            JSONObject jsonError = new JSONObject(response.errorBody().string());
                            message = jsonError.getString("message");
                            showMessage(message);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                @Override
                public void onFailure(Call<ApiAuthorizationData> call, Throwable t) {
                    t.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
