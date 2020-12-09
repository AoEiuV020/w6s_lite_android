package com.foreveross.atwork.modules.chat.component;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.SelectDialogList;

import java.util.ArrayList;

/**
 * Created by lingen on 15/4/22.
 * Description:
 */
public class PopupListDialogSupportPack extends DialogFragment {


    public static final String DATA_ITEMS_LIST = "DATA_ITEMS_LIST";
    public static final String DATA_ITEMS_TITLE = "DATA_ITEMS_TITLE";

    private ArrayList<String> mItemsList = null;
    private String mTitle;
    private OnListItemClickListener mOnListItemClickListener;

    private  SelectDialogList mDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mItemsList = getArguments().getStringArrayList(DATA_ITEMS_LIST);
            mTitle = getArguments().getString(DATA_ITEMS_TITLE);
        }


        mDialog = new SelectDialogList(getActivity(), mItemsList, item -> {
            getDialog().dismiss();
            mOnListItemClickListener.onItemClick(item);
        });
        if (!TextUtils.isEmpty(mTitle)) {
            setTitle(mTitle);
        }
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setStyle(STYLE_NO_FRAME, android.R.style.Theme_Light);
        getDialog().getWindow().setBackgroundDrawableResource(R.color.white);
        return mDialog;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
    }


    public void setOnListItemClickListener(OnListItemClickListener onListItemClickListener) {
        this.mOnListItemClickListener = onListItemClickListener;
    }

    public void addItem(String item) {
        mDialog.addItem(item);
    }

    public void setTitle(String title) {
        mDialog.setTitle(title);
    }

    public interface OnListItemClickListener {
        void onItemClick(String item);
    }
}
