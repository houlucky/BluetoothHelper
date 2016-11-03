package top.wuhaojie.bthelper.runn;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import top.wuhaojie.bthelper.BtHelperClient;
import top.wuhaojie.bthelper.Constants;
import top.wuhaojie.bthelper.MessageItem;
import top.wuhaojie.bthelper.OnSendMessageListener;

/**
 * Created by Houxy on 2016/11/2.
 */

public class WriteRunnable implements Runnable {

    private static final String TAG = WriteRunnable.class.getSimpleName();
    private static final int HANDLER_WHAT_SEND_FAILED = 1;
    private static final int HANDLER_WHAT_SEND_SUCCESS = 2;
    private OnSendMessageListener listener;
    //TODO
    private boolean needResponse;
    private OutputStream outputStream;
    private MessageItem mMessageItem;
//    private Queue<MessageItem> mMessageQueue = new LinkedBlockingQueue<>();


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


    public WriteRunnable(MessageItem messageItem,OnSendMessageListener listener, boolean needResponse, OutputStream outputStream) {
        if(null == listener){
            throw new NullPointerException("OnSendMessageListener can not null");
        }
        this.listener = listener;
        this.needResponse = needResponse;
        mMessageItem = messageItem;
        this.outputStream = outputStream;
    }

    @Override
    public void run() {
//        mWritable = true;
//            Log.d(TAG, "准备写入");
        // 并且要写入线程未被取消
//        /**
//         * TODO:这行代码待商榷
//         */
//        while (mCurrStatus != BtHelperClient.STATUS.CONNECTED && mWritable) ;

//        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(mConnectDeviceRunnable.getOutputStream()));
//            BufferedReader reader = new BufferedReader(new InputStreamReader(mInputStream));
//            Log.d(TAG, "开始写入");


        if (null != outputStream) {
//             = mMessageQueue.poll();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

            try {
                writer.write(mMessageItem.getData());
                writer.newLine();
                writer.flush();
                Log.d(TAG, "send: " + mMessageItem.getData());
                Message message = new Message();
                message.what = HANDLER_WHAT_SEND_SUCCESS;
                message.obj = mMessageItem.getData();
                mHandler.sendMessage(message);
            } catch (IOException e) {
                mHandler.sendEmptyMessage(HANDLER_WHAT_SEND_FAILED);
//                break;
            }

            // ----- Read For Response -----
//            if (!needResponse) continue;
//            try {
////                    String s = reader.readLine();
//                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
//                StringBuilder builder = new StringBuilder();
//
//                while (mInputStream.available() == 0) ;
//
//                while (true) {
//                    int num = mInputStream.read(buffer);
//                    String s = new String(buffer, 0, num);
//                    builder.append(s);
//                    if (mInputStream.available() == 0) break;
//
//                }
//                String s = builder.toString().trim();
//                if (mFilter != null) {
//                    if (mFilter.isCorrect(s)) {
//                        message.obj = s;
//                        mHandler.sendMessage(message);
//                    } else {
//                        message.obj = "";
//                        message.arg1 = Constants.STATUS_ERROR;
//                        mHandler.sendMessage(message);
//                    }
//                } else {
//                    message.obj = s;
//                    mHandler.sendMessage(message);
//                }
//
//            } catch (IOException e) {
////                    e.printStackTrace();
//                if (listener != null)
//                    listener.onConnectionLost(e);
//                mCurrStatus = BtHelperClient.STATUS.FREE;
//            }
//
//        }
        }
    }
}
