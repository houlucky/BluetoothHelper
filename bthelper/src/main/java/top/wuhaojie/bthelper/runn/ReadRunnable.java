package top.wuhaojie.bthelper.runn;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;

import top.wuhaojie.bthelper.i.OnReceiveMessageListener;

/**
 * Created by Houxy on 2016/11/2.
 */

public class ReadRunnable implements Runnable {

    private static final int HANDLER_WHAT_NEW_MSG=1;
    private static final int HANDLER_CONNECTION_INTERRUPTED=2;
    private OnReceiveMessageListener mListener;
    private InputStream mInputStream;

    public ReadRunnable(OnReceiveMessageListener listener, InputStream inputStream) {
        mListener = listener;
        mInputStream = inputStream;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_WHAT_NEW_MSG:
                    String s = (String) msg.obj;
                    mListener.onNewLine(s);
                    break;
            }
        }
    };

    @Override
    public void run() {
        if( null != mInputStream){

            boolean runFlag = true;
            int n;
            byte[] buffer = new byte[256];
            while (runFlag){
                //TODO ???
//                DataInputStream dataInputStream = new DataInputStream(mInputStream);
                try {
                    n = mInputStream.read(buffer);
                    String s = new String(buffer, 0, n);
                    mListener.onNewLine(s);
                } catch (IOException e) {
                    e.printStackTrace();
                    runFlag = false;
                    mListener.onConnectionLost();
                }
            }
        }
    }
}
