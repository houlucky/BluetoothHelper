package top.wuhaojie.bthelper.receiver;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import top.wuhaojie.bthelper.OnBtStateChangeListener;

/**
 * Created by Houxy on 2016/11/3.
 */

public class BtStateReceiver extends BroadcastReceiver{

    private OnBtStateChangeListener mOnBtStateListener;

    public BtStateReceiver(OnBtStateChangeListener onBtStateListener){
        mOnBtStateListener = onBtStateListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if( null != intent.getAction()){
            int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
            switch (blueState){
                case BluetoothAdapter.STATE_OFF:
                    mOnBtStateListener.OnBtStateOFF();
                    break;
                case BluetoothAdapter.STATE_ON:
                    mOnBtStateListener.OnBtStateON();
                    break;
                default:break;
            }
        }

    }
}
