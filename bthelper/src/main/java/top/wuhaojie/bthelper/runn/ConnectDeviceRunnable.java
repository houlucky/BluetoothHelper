package top.wuhaojie.bthelper.runn;

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
import top.wuhaojie.bthelper.Constants;
import top.wuhaojie.bthelper.i.IConnectionListener;
import top.wuhaojie.bthelper.i.OnReceiveMessageListener;

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
//            mBluetoothAdapter.cancelDiscovery();
        try {
            mHandler.sendEmptyMessage(HANDLER_WHAT_CONNECTION_START);
            Log.d("TAG", "prepare to connect: " + remoteDevice.getAddress() + " " + remoteDevice.getName());
            mSocket = remoteDevice.createInsecureRfcommSocketToServiceRecord(UUID.fromString(Constants.STR_UUID));
            mSocket.connect();
            mInputStream = mSocket.getInputStream();
            mOutputStream = mSocket.getOutputStream();
            mHandler.sendEmptyMessage(HANDLER_WHAT_CONNECTION_SUCCESS);

//            boolean runFlag = true;
//            int n=0;
//            byte[] buffer = new byte[32];
//            StringBuilder stringBuilder = new StringBuilder();
//            while (runFlag){
////                DataInputStream dataInputStream = new DataInputStream(mInputStream);
//                try {
//                        n = mInputStream.read(buffer);
//                        String s = new String(buffer, 0, n);
//                        stringBuilder.append(s);
//
//                    Log.d("TAG", "re mes : " + stringBuilder.toString());
//                    stringBuilder.delete(0, n);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    runFlag = false;
//                }
//            }

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

    public void receiveMessage(InputStream inputStream) {

        OnReceiveMessageListener onReceiveMessageListener = new OnReceiveMessageListener() {
            @Override
            public void onNewLine(String s) {

            }

            @Override
            public void onConnectionLost() {

            }

            @Override
            public void onError(Exception e) {

            }
        };
        ReadRunnable readRunnable = new ReadRunnable(onReceiveMessageListener, inputStream);

    }

}
