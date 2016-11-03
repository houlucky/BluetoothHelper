package com.houxy.bluetoothcontrol.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Houxy on 2016/11/3.
 */

public abstract class MessageReceiver extends BroadcastReceiver {

    protected abstract void OnReceiveMessage(String message);

    @Override
    public void onReceive(Context context, Intent intent) {
        if("com.houxy.action.MESSAGE".equals(intent.getAction())){
            if( null != intent.getStringExtra("message")){
                OnReceiveMessage(intent.getStringExtra("message"));
            }
        }
    }
}
