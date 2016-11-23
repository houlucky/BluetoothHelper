package com.houxya.bthelper.receiver;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.houxya.bthelper.Constants;

/**
 * Created by Houxy on 2016/11/4.
 */

public abstract class BtAcceptReceiver extends BroadcastReceiver{

    public abstract void OnAccept(BluetoothDevice device);


    @Override
    public void onReceive(Context context, Intent intent) {

        if(BroadcastType.BROADCAST_TYPE_ACCEPT_CONNECTION.equals(intent.getAction())){
            if( null != intent.getParcelableExtra(Constants.REMOTE_DEVICE)){
                OnAccept((BluetoothDevice) intent.getParcelableExtra(Constants.REMOTE_DEVICE));
            }
        }
    }
}
