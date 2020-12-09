package com.foreveross.atwork.listener;

/**
 * Created by dasunsy on 2018/3/15.
 */

public interface EnterLeaveAppListener {

//    long CHECK_DELAY_GAP = 3 * 1000;
    long CHECK_DELAY_GAP = 0;

    String ACTION_ENTER_LEAVE_APP = "ACTION_ENTER_LEAVE_APP";

    String DATA_ENTER_LEAVE = "DATA_ENTER_LEAVE";

    int LEAVE = 0;

    int ENTER = 1;

    /**
     * app 变为前台不可见, 该处因为使用定时器监听来保证回调的准确性, 所以会略有延迟, 并且回调是在线程里, 需要注意
     * */
    void onLeaveApp();


    /**
     * app 变为前台可见, 该处因为使用定时器监听来保证回调的准确性, 所以会略有延迟, 并且回调是在线程里, 需要注意
     * */
    void onEnterApp();

}
