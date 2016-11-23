package com.houxya.bthelper.receiver;

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
        if(BroadcastType.BROADCAST_TYPE_RECEIVED_MESSAGE.equals(intent.getAction())){
            if( null != intent.getStringExtra("message")){
                OnReceiveMessage(intent.getStringExtra("message"));
            }
        }
    }
}
