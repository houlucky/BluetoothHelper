package com.houxya.bthelper.runn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.houxya.bthelper.Constants;
import com.houxya.bthelper.i.IConnectionListener;

/**
 * Created by Houxy on 2016/11/2.
 */

public class ConnectDeviceRunnable implements Runnable {
    private static final int HANDLER_WHAT_CONNECTION_START = 3;
    private static final int HANDLER_WHAT_CONNECTION_SUCCESS = 4;
    private static final int HANDLER_WHAT_CONNECTION_FAILED = 5;

    private String mac;
    private IConnectionListener listener;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private BluetoothSocket mSocket;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_WHAT_CONNECTION_START:
                    listener.OnConnectionStart();
                    break;
                case HANDLER_WHAT_CONNECTION_SUCCESS:
                    listener.OnConnectionSuccess();
                    break;
                case HANDLER_WHAT_CONNECTION_FAILED:
                    listener.OnConnectionFailed((Exception) msg.obj);
                default:break;
            }
        }
    };

    public ConnectDeviceRunnable(String mac, IConnectionListener listener) {
        if(null == listener){
            throw new NullPointerException("IConnectionListener can not be null");
        }

        this.mac = mac;
        this.listener = listener;
    }

    @Override
    public void run() {
        // always return a remote device
        BluetoothDevice remoteDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mac);
        try {
            mHandler.sendEmptyMessage(HANDLER_WHAT_CONNECTION_START);
            Log.d("TAG", "prepare to connect: " + remoteDevice.getAddress() + " " + remoteDevice.getName());
            mSocket = remoteDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(Constants.STR_UUID));
            mSocket.connect();
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
            mHandler.sendEmptyMessage(HANDLER_WHAT_CONNECTION_SUCCESS);


        } catch (Exception e) {
            Message message = new Message();
            message.obj = e;
            message.what = HANDLER_WHAT_CONNECTION_FAILED;
            mHandler.sendMessage(message);
            try {
                mInputStream.close();
                mOutputStream.close();
            } catch (IOException closeException) {
                closeException.printStackTrace();
            }
        }
    }


    public InputStream getInputStream() {
        if(mSocket.isConnected())
            return mInputStream;
        return null;
    }

    public OutputStream getOutputStream() {
        if(mSocket.isConnected())
            return mOutputStream;
        return null;
    }
}
