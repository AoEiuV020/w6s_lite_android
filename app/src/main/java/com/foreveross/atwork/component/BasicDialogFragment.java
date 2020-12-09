package com.foreveross.atwork.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreverht.workplus.ui.component.dialogFragment.AtworkAlertDialog;
import com.foreverht.workplus.ui.component.dialogFragment.BasicUIDialogFragment;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.newmessage.PostTypeMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.util.UndoMessageHelper;

/**
 * Created by dasunsy on 2017/2/21.
 */

public class BasicDialogFragment extends BasicUIDialogFragment {

    private BroadcastReceiver mUndoMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (UndoMessageHelper.UNDO_MESSAGE_RECEIVED.equals(intent.getAction())) {
                UndoEventMessage undoEventMessage = (UndoEventMessage) intent.getSerializableExtra(ChatDetailFragment.DATA_NEW_MESSAGE);
                onUndoMsgReceive(undoEventMessage);


            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        registerCoreBroadcast();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterCoreBroadcast();
    }



    /**
     * 收到撤回消息通知
     * */
    public void onUndoMsgReceive(UndoEventMessage undoEventMessage) {

    }

    protected void showUndoDialog(Context context, PostTypeMessage message) {
        AtworkAlertDialog dialog = new AtworkAlertDialog(context, AtworkAlertDialog.Type.SIMPLE)
                .setContent(UndoMessageHelper.getUndoContent(context, message))
                .hideDeadBtn()
                .setClickBrightColorListener(dialog1 -> dismissAllowingStateLoss());
        dialog.setCancelable(false);
        dialog.show();
    }

    private void registerCoreBroadcast() {

        IntentFilter undoIntentFilter = new IntentFilter();
        undoIntentFilter.addAction(UndoMessageHelper.UNDO_MESSAGE_RECEIVED);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mUndoMessageReceiver, undoIntentFilter);

    }


    private void unregisterCoreBroadcast() {
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mUndoMessageReceiver);
    }
}
