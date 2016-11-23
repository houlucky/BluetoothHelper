package com.houxya.bthelper.bean;

/**
 * Created by wuhaojie on 2016/9/10 20:23.
 */
public class MessageItem {

//    public enum TYPE {
//        STRING,
//        CHAR
//    }

//    public String text;
//    public char[] data;

//    public TYPE mTYPE;
    private String data;

    public MessageItem(String text) {
//        this.text = text;
//        mTYPE = TYPE.STRING;
        data = text;
    }

    public MessageItem(char[] text) {
//        this.data = data;
//        mTYPE = TYPE.CHAR;
        data = new String(text);
    }

    public String getData() {
        return data;
    }
}
