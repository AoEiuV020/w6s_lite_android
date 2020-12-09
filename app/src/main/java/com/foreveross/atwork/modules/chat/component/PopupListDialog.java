package com.foreveross.atwork.modules.chat.component;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.SelectDialogList;

import java.util.ArrayList;

/**
 * Created by dasunsy on 16/7/18.
 */
public class PopupListDialog extends DialogFragment {
    public static final String DATA_ITEMS_LIST = "DATA_ITEMS_LIST";

    private ArrayList<String> mItemsList = null;
    private OnListItemClickListener mOnListItemClickListener;


    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mItemsList = getArguments().getStringArrayList(DATA_ITEMS_LIST);
        }


        final SelectDialogList view = new SelectDialogList(getActivity(), mItemsList, item -> {
            getDialog().dismiss();
            mOnListItemClickListener.onItemClick(item);
        });
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Light);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.white);
        return view;
    }

    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.mOnListItemClickListener = onListItemClickListener;
    }

    public interface OnListItemClickListener {
        void onItemClick(String item);
    }
}
