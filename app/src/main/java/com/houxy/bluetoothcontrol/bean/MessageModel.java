package com.houxy.bluetoothcontrol.bean;

import java.util.ArrayList;

/**
 * Created by Houxy on 2016/11/3.
 */

public class MessageModel {

    private ArrayList<DataItem> dataItems;

    public  enum TYPE{
        RECEIVER_TXT,
        SEND_TXT
    }

    public MessageModel(){}

    public MessageModel(ArrayList<DataItem> dataItems){
        this.dataItems = dataItems;
    }

    public void setDataItems(ArrayList<DataItem> dataItems) {
        this.dataItems = dataItems;
    }

    public ArrayList<DataItem> getDataItems() {
        return dataItems;
    }
}
