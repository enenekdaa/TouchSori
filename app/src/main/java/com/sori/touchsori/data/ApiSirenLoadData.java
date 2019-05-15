package com.sori.touchsori.data;

import com.google.gson.annotations.SerializedName;

public class ApiSirenLoadData {

    @SerializedName("configId")
    private int configId = 0;

    @SerializedName("serviceId")
    private String serviceId = null;

    @SerializedName("deviceId")
    private String deviceId = null;

    @SerializedName("configGroup")
    private String configGroup = null;

    @SerializedName("configCode")
    private String configCode = null;

    @SerializedName("data")
    private String data = null;

    @SerializedName("status")
    private String status = null;

    @SerializedName("processStep")
    private String processStep = null;

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getConfigGroup() {
        return configGroup;
    }

    public void setConfigGroup(String configGroup) {
        this.configGroup = configGroup;
    }

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProcessStep() {
        return processStep;
    }

    public void setProcessStep(String processStep) {
        this.processStep = processStep;
    }
}
