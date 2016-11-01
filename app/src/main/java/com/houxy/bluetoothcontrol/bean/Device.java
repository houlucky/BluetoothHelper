package com.houxy.bluetoothcontrol.bean;

/**
 * Created by Houxy on 2016/10/31.
 */

public class Device {

    private String deviceName;
    private String deviceAddress;

    public Device(String deviceName, String deviceAddress){
        this.deviceAddress = deviceAddress;
        this.deviceName = deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setDeviceAddress(String deviceAddress) {
        this.deviceAddress = deviceAddress;
    }

    public String getDeviceAddress() {
        return deviceAddress;
    }

    public String getDeviceName() {
        return deviceName;
    }
}
