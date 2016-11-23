package com.houxya.bthelper.i;

/**
 * Listener for send message process.
 * Created by wuhaojie on 2016/9/10 20:17.
 */
public interface OnSendMessageListener extends IErrorListener, OnConnectionLostListener {

    void onSuccess(String s);
}
