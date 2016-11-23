package com.houxya.bthelper.receiver;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

import com.houxya.bthelper.i.OnSearchDeviceListener;

/**
 * Created by Houxy on 2016/11/2.
 */

public class BtReceiver extends BroadcastReceiver{

    private ArrayList<BluetoothDevice> mBondedList;
    private ArrayList<BluetoothDevice> mNewList;
    private OnSearchDeviceListener mOnSearchDeviceListener;

    public BtReceiver(OnSearchDeviceListener onSearchDeviceListener){
        if(null == onSearchDeviceListener){
            throw new NullPointerException("onSearchDeviceListener can not be null");
        }
        this.mOnSearchDeviceListener = onSearchDeviceListener;
        if(null == mBondedList)
            mBondedList = new ArrayList<>();
        if(null == mNewList)
            mNewList = new ArrayList<>();

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                mNewList.add(device);
                mOnSearchDeviceListener.onNewDeviceFound(device);
            } else if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                mBondedList.add(device);
            }

        } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            mOnSearchDeviceListener.onSearchCompleted(mBondedList, mNewList);
        }
    }

}
