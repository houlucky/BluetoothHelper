package com.houxy.bluetoothcontrol.bean;

/**
 * Created by Houxy on 2016/11/5.
 */

public class NewDeviceHeader {

    private String headerName;
    private boolean progressBarState;

    public NewDeviceHeader(){}

    public NewDeviceHeader(String headerName, boolean progressBarState){
        this.headerName = headerName;
        this.progressBarState = progressBarState;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public void setProgressBarState(boolean progressBarState) {
        this.progressBarState = progressBarState;
    }

    public boolean getProgressBarState(){
        return progressBarState;
    }
}
