package dev.romdev.com.m2pad.net;

/**
 * Created by LCL on 2017/11/17.
 */

public class EventBusMsg {

    public int what;
    public Object object;

    public EventBusMsg(int what ,Object object ){
       this.what = what;
        this.object = object;

    }

}
