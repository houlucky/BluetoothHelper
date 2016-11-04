package top.wuhaojie.bthelper.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Houxy on 2016/11/4.
 */

public  class BtConnectionReceiver extends BroadcastReceiver{

    public  void OnBtConnected(){}
    public  void OnBtConnecting(){}
    public  void OnBtDisconnected(){}
    public  void OnBtDisconnecting(){}

    @Override
    public void onReceive(Context context, Intent intent) {

        int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
        switch (blueState){

            case BluetoothAdapter.STATE_CONNECTED:
                OnBtConnected();
                break;
            case BluetoothAdapter.STATE_CONNECTING:
                OnBtConnecting();
                break;
            case BluetoothAdapter.STATE_DISCONNECTED:
                OnBtDisconnected();
                break;
            case BluetoothAdapter.STATE_DISCONNECTING:
                OnBtDisconnecting();
                break;
            default:break;
        }
    }
}
