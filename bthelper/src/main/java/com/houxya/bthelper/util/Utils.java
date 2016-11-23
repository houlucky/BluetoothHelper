package com.houxya.bthelper.util;

/**
 * Created by Houxy on 2016/11/2.
 */

public class Utils {

    public static byte[] getHexBytes(String message) {
//        int len = message.length() / 2;
//        char[] chars = message.toCharArray();
//        String[] hexStr = new String[len];
//        byte[] bytes = new byte[len];
//        for (int i = 0, j = 0; j < len; i += 2, j++) {
//            hexStr[j] = "" + chars[i] + chars[i + 1];
//            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
//        }
        return getHexBytes(message.toCharArray());
    }

    public static byte[] getHexBytes(char[] message) {
//        int len = message.length() / 2;
//        char[] chars = message.toCharArray();
        String[] hexStr = new String[message.length];
        byte[] bytes = new byte[message.length];
        for (int i = 0, j = 0; j < message.length; i += 2, j++) {
            hexStr[j] = "" + message[i] + message[i + 1];
            bytes[j] = (byte) Integer.parseInt(hexStr[j], 16);
        }
        return bytes;
    }


}
