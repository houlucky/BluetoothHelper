package com.houxya.bthelper.i;

/**
 * Created by wuhaojie on 2016/9/10 20:17.
 */
public interface OnReceiveMessageListener extends IErrorListener, OnConnectionLostListener {


    void onNewLine(String s);
}
