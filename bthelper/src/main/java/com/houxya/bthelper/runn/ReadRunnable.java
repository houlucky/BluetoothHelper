package com.houxya.bthelper.runn;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.houxya.bthelper.i.OnReceiveMessageListener;

/**
 * Created by Houxy on 2016/11/2.
 */

public class ReadRunnable implements Runnable {

    private OnReceiveMessageListener mListener;
    private InputStream mInputStream;

    public ReadRunnable(OnReceiveMessageListener listener, InputStream inputStream) {
        mListener = listener;
        mInputStream = inputStream;
    }

    @Override
    public void run() {
        if( null != mInputStream){

            boolean runFlag = true;
            int n;
            char[] buffer = new char[1024];
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mInputStream));
            while (runFlag){
                try {

                    if(mInputStream.available() <= 0){
                        continue;
                    }else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    n = bufferedReader.read(buffer);
                    String s = new String(buffer, 0, n);
                    Log.d("TAG", "receive : "+ s);
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
