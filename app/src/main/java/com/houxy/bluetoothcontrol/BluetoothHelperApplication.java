package com.houxy.bluetoothcontrol;

import android.app.Application;

/**
 * Created by Houxy on 2016/11/2.
 */

public class BluetoothHelperApplication extends Application{

    private static BluetoothHelperApplication INSTANCE;
    public static String cacheDir = "";

    public static BluetoothHelperApplication getContext() {
        return INSTANCE;
    }

    private void setInstance(BluetoothHelperApplication app) {
        setDaysApplication(app);
    }

    private static void setDaysApplication(BluetoothHelperApplication a) {
        BluetoothHelperApplication.INSTANCE = a;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setInstance(this);
        //初始化

        /**
         * 如果存在SD卡则将缓存写入SD卡,否则写入手机内存
         */
        if (getApplicationContext().getExternalCacheDir() != null) {
            cacheDir = getApplicationContext().getExternalCacheDir().toString();
        } else {
            cacheDir = getApplicationContext().getCacheDir().toString();
        }
    }
}
