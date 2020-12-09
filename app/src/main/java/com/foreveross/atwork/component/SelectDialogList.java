package com.foreveross.atwork.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.listview.MaxHeightListView;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;

import java.util.ArrayList;

/**
 * Created by reyzhang22 on 15/6/30.
 */
public class SelectDialogList extends LinearLayout {

    private ArrayList<String> mDialogItems;
    private OnDialogItemClickListener mListener;
    private View mTitleView;
    private TextView mTitle;

    private SelectDialogListAdapter mAdapter;

    public SelectDialogList(Context context, ArrayList<String> dialogItems, OnDialogItemClickListener listener) {
        super(context);
        mDialogItems = dialogItems;
        initView(context);
        mListener = listener;
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.dialog_listview, this);
        MaxHeightListView listView = view.findViewById(R.id.dialog_listview);
        int maxHeight = ScreenUtils.getScreenHeight(context) * 3 / 5;
        listView.setMaxListViewHeight(maxHeight);
        mTitleView = view.findViewById(R.id.title_view);
        mTitle = mTitleView.findViewById(R.id.title);
        mAdapter = new SelectDialogListAdapter(context, mDialogItems);
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> mListener.onItemSelect(mDialogItems.get(position)));
    }

    public void addItem(String item) {
        if (mAdapter == null) {
            return;
        }
        mAdapter.addItem(item);
    }


    public interface OnDialogItemClickListener {
        void onItemSelect(String item);
    }

    public void setTitle(String title) {
        mTitleView.setVisibility(VISIBLE);
        mTitle.setText(title);
    }
}
