package com.houxy.bluetoothcontrol.bean;

/**
 * Created by Houxy on 2016/11/5.
 */

public class NoDeviceFoundHeader {

    private String headerName;

    public NoDeviceFoundHeader(){}

    public NoDeviceFoundHeader(String headerName){
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }
}
