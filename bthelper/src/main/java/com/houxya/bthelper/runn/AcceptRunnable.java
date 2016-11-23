package com.houxya.bthelper.runn;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import com.houxya.bthelper.Constants;
import com.houxya.bthelper.i.OnAcceptListener;

/**
 * Created by Houxy on 2016/11/5.
 */

public class AcceptRunnable implements Runnable {



    private BluetoothServerSocket mServerSocket;
    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private OnAcceptListener mOnAcceptListener;


    public AcceptRunnable(OnAcceptListener OnAcceptListener) {
        mOnAcceptListener = OnAcceptListener;
        BluetoothServerSocket temp = null;
        try {
            temp = BluetoothAdapter.getDefaultAdapter()
                    .listenUsingRfcommWithServiceRecord("BT", UUID.fromString(Constants.STR_UUID));

        } catch (IOException e) {
            e.printStackTrace();
        }
        mServerSocket = temp;
    }

    @Override
    public void run() {
        BluetoothSocket socket = null;
        while (true) {
            try {
                socket = mServerSocket.accept();
            } catch (IOException e) {
                mOnAcceptListener.OnAcceptFailed(e);
                e.printStackTrace();
                break;
            }
            if (socket != null) {

                try {
                    mInputStream = socket.getInputStream();
                    mOutputStream = socket.getOutputStream();
                    mOnAcceptListener.OnAcceptSuccess(socket.getRemoteDevice());
                    mServerSocket.close();
                } catch (IOException e) {
                    mOnAcceptListener.OnAcceptFailed(e);
                    e.printStackTrace();
                    break;
                }
                break;
            }
        }
    }

    public InputStream getInputStream() {
        return mInputStream;
    }

    public OutputStream getOutputStream() {
        return mOutputStream;
    }

    public void cancel() {
        try {
            mServerSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
