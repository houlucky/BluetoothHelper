package com.houxya.bthelper.runn;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.houxya.bthelper.bean.MessageItem;
import com.houxya.bthelper.i.OnSendMessageListener;

/**
 * Created by Houxy on 2016/11/2.
 */

public class WriteRunnable implements Runnable {

    private static final String TAG = WriteRunnable.class.getSimpleName();
    private static final int HANDLER_WHAT_SEND_FAILED = 1;
    private static final int HANDLER_WHAT_SEND_SUCCESS = 2;
    private OnSendMessageListener listener;
    //TODO
    private OutputStream outputStream;
    private MessageItem mMessageItem;


    private Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_WHAT_SEND_SUCCESS:
                    listener.onSuccess((String) msg.obj);
                    break;
                case HANDLER_WHAT_SEND_FAILED:
                    listener.onConnectionLost();
                    break;
                default:break;
            }
        }
    };


    public WriteRunnable(MessageItem messageItem,OnSendMessageListener listener,  OutputStream outputStream) {
        if(null == listener){
            throw new NullPointerException("OnSendMessageListener can not null");
        }
        this.listener = listener;
        mMessageItem = messageItem;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {


        if (null != outputStream) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));
            try {
                writer.write(mMessageItem.getData());
//                writer.newLine();//换行显示
                writer.flush();
                Log.d("TAG", "send: " + mMessageItem.getData());
                Message message = new Message();
                message.what = HANDLER_WHAT_SEND_SUCCESS;
                message.obj = mMessageItem.getData();
                mHandler.sendMessage(message);
            } catch (IOException e) {
                mHandler.sendEmptyMessage(HANDLER_WHAT_SEND_FAILED);
            }
        }
    }
}
