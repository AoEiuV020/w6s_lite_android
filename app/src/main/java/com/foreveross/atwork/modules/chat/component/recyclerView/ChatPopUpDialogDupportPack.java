package com.foreveross.atwork.modules.chat.component.recyclerView;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.newmessage.post.event.UndoEventMessage;
import com.foreveross.atwork.modules.chat.fragment.ChatDetailFragment;
import com.foreveross.atwork.modules.chat.util.UndoMessageHelper;

import java.util.ArrayList;
import java.util.List;

public class ChatPopUpDialogDupportPack extends DialogFragment {

    public static final String DATA_ITEMS_LIST = "DATA_ITEMS_LIST";
    public static final String ANCHOR_HEIGHT = "ANCHOR_HEIGHT";
    public static final String AREA_HEIGHT = "AREA_HEIGHT";
    /**
     * 调起长按菜单的消息id
     */
    public static final String DELIVERY_ID = "DELIVERY_ID";
    private String showDeliveryId = "";
    private ArrayList<String> mItemsList = null;
    private SelectDialogRecyclerView mSelectDialogRecyclerView;
    private OnRecyclerItemClickListener mOnRecyclerItemClickListener;
    private OnDismissingListener mOnDismissingListener;

    private int mAnchorHeight, mAreaHeight;

    private BroadcastReceiver mUndoMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            if (UndoMessageHelper.UNDO_MESSAGE_RECEIVED.equals(intent.getAction())) {
                UndoEventMessage undoEventMessage = (UndoEventMessage) intent.getSerializableExtra(ChatDetailFragment.DATA_NEW_MESSAGE);
                List<String> mEnvIds = undoEventMessage.mEnvIds;
                for (int i = 0; i < mEnvIds.size(); i++) {
                    if (!TextUtils.isEmpty(showDeliveryId) && showDeliveryId.equals(mEnvIds.get(i))) {
                        getDialog().dismiss();
                    }
                }
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();

        Window window = getActivity().getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.dimAmount = 0.0f;
        window.setAttributes(lp);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).unregisterReceiver(mUndoMessageReceiver);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_FRAME, 0);
        setCancelable(true);

        IntentFilter undoIntentFilter = new IntentFilter();
        undoIntentFilter.addAction(UndoMessageHelper.UNDO_MESSAGE_RECEIVED);
        LocalBroadcastManager.getInstance(BaseApplicationLike.baseContext).registerReceiver(mUndoMessageReceiver, undoIntentFilter);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mItemsList = getArguments().getStringArrayList(DATA_ITEMS_LIST);
            mAnchorHeight = getArguments().getInt(ANCHOR_HEIGHT, 200);
            mAreaHeight = getArguments().getInt(AREA_HEIGHT, 200);
            showDeliveryId = getArguments().getString(DELIVERY_ID);
        }
        mSelectDialogRecyclerView = new SelectDialogRecyclerView(getActivity(), mItemsList, item -> {
            getDialog().dismiss();
            mOnRecyclerItemClickListener.onItemClick(item);
        });

        mSelectDialogRecyclerView.setVisibility(View.INVISIBLE);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        getDialog().getWindow().setGravity(Gravity.TOP);
        WindowManager.LayoutParams lp = getDialog().getWindow().getAttributes();
        //获取屏幕的高度
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        mSelectDialogRecyclerView.setViewHeightListener(dialogHeight -> {

            if (mAnchorHeight > screenHeight / 2) {
                lp.y = mAnchorHeight - dialogHeight - 55;
            } else {
                int heightTemp = mAnchorHeight + mAreaHeight;
                if (heightTemp + dialogHeight + dialogHeight / 2 < screenHeight) {
                    lp.y = heightTemp - 55;
                } else {
                    lp.y = screenHeight / 2 - dialogHeight / 2;
                }
            }
            getDialog().getWindow().setAttributes(lp);

            mSelectDialogRecyclerView.postDelayed(() -> mSelectDialogRecyclerView.setVisibility(View.VISIBLE), 50);

        });

        setCancelable(true);
        getDialog().setCanceledOnTouchOutside(true);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Light);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        return mSelectDialogRecyclerView;
    }

    public void setOnclick(OnRecyclerItemClickListener onRecyclerItemClickListener) {

        mOnRecyclerItemClickListener = onRecyclerItemClickListener;
    }


    public void setOnDismissingListener(OnDismissingListener onDismissingListener) {
        this.mOnDismissingListener = onDismissingListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if(null != mOnDismissingListener) {
            mOnDismissingListener.dismissing();
        }
    }

    public interface OnRecyclerItemClickListener {
        void onItemClick(String item);
    }

    public interface OnDismissingListener {
        void dismissing();
    }
}
