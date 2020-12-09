package com.foreveross.atwork.manager;

import android.content.Context;
import android.content.Intent;
import android.util.SparseBooleanArray;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.modules.main.activity.MainActivity;

import static com.foreveross.atwork.modules.chat.fragment.ChatListFragment.DATA_RECEIVING;
import static com.foreveross.atwork.modules.chat.fragment.ChatListFragment.INTENT_RECEIVING_TITLE_HANDLED;

/**
 * {@link MainActivity}的 Title 状态栏的管理类, 使用{@link #mReceivingTitleQueue}来管理, 当所有标签动作都
 * 结束后, 才视为隐藏掉"收取中"的状态
 */
public class ReceivingTitleQueueManager {
    private static ReceivingTitleQueueManager mInstance;
    public static final int TAG_SYNC = 1;
    public static final int TAG_GET_OFFLINE = 2;
    public static final int TAG_BING_SYNC = 3;

    public SparseBooleanArray mReceivingTitleQueue = new SparseBooleanArray();


    public static ReceivingTitleQueueManager getInstance() {
        if (null == mInstance) {
            mInstance = new ReceivingTitleQueueManager();
        }
        return mInstance;
    }

    public void push(Context context, int tag) {
        handle(context, tag, true);
    }

    public void pull(Context context, int tag) {
        handle(context, tag, false);
    }

    public void handle(Context context, int tag, boolean isReceived) {
        if (0 == mReceivingTitleQueue.size()) {
            initReceivingTitleQueue();
        }

        mReceivingTitleQueue.put(tag, isReceived);
        sendReceivingBroadcast(context);
    }

    public void initReceivingTitleQueue() {
        mReceivingTitleQueue.put(TAG_SYNC, false);
        mReceivingTitleQueue.put(TAG_GET_OFFLINE, false);
        mReceivingTitleQueue.put(TAG_BING_SYNC, false);
    }

    /**
     * 发送正在接受消息广播
     */
    public void sendReceivingBroadcast(Context context) {
        boolean shouldShowTitle = false;

        for(int i = 0; i < mReceivingTitleQueue.size(); i ++) {
            boolean value = mReceivingTitleQueue.valueAt(i);
            if(true == value) {
                shouldShowTitle = true;
                break;
            }
        }

        Intent receivingMessageIntent = new Intent(DATA_RECEIVING);
        receivingMessageIntent.putExtra(INTENT_RECEIVING_TITLE_HANDLED, shouldShowTitle);
        LocalBroadcastManager.getInstance(context).sendBroadcast(receivingMessageIntent);
    }
}
