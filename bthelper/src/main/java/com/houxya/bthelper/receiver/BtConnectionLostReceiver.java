package com.houxya.bthelper.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Houxy on 2016/11/4.
 */

public abstract class BtConnectionLostReceiver extends BroadcastReceiver{

    public abstract void OnConnectionLost();

    @Override
    public void onReceive(Context context, Intent intent) {

        if(BroadcastType.BROADCAST_TYPE_CONNECTION_LOST.equals(intent.getAction())){
            OnConnectionLost();
        }
    }
}
