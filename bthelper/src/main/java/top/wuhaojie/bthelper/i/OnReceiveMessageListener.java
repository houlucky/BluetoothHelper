package top.wuhaojie.bthelper.i;

import top.wuhaojie.bthelper.i.IErrorListener;
import top.wuhaojie.bthelper.i.OnConnectionLostListener;

/**
 * Created by wuhaojie on 2016/9/10 20:17.
 */
public interface OnReceiveMessageListener extends IErrorListener, OnConnectionLostListener {


    void onNewLine(String s);
}
