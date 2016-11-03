package top.wuhaojie.bthelper.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.InputStream;

import top.wuhaojie.bthelper.OnReceiveMessageListener;

/**
 * Created by Houxy on 2016/11/2.
 */

public class ReceiveDataService extends Service{


    public ReceiveDataService(InputStream inputStream, OnReceiveMessageListener onReceiveMessageListener){

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
