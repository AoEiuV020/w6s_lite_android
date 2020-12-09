package com.foreveross.atwork.modules.chat.component.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.foreveross.atwork.R;

import java.util.ArrayList;

public class SelectDialogRecyclerView extends LinearLayout {

    private ArrayList<String> mDialogItems;
    private SelectDialogRecyclerAdapter mSelectDialogRecyclerAdapter;
    private OnDialogItemClickListener mOnDialogItemClickListener;
    private ViewHeightListener mViewHeightListener;

    private LinearLayout mLlChatSelectDialog;
    private int mHeight;


    public SelectDialogRecyclerView(Context context, ArrayList<String> dialogItems, OnDialogItemClickListener onDialogItemClickListener) {
        super(context);
        mDialogItems = dialogItems;
        mOnDialogItemClickListener = onDialogItemClickListener;
        initView(context);
    }

    private void initView(Context context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_select_dialog_recycler_view, this);
        RecyclerView recyclerView = view.findViewById(R.id.select_recycler_view);
        mLlChatSelectDialog = view.findViewById(R.id.ll_chat_select_dialog);
        //用于指定recyclerView布局的方式
        StaggeredGridLayoutManager managerDemandLayoutManager = new StaggeredGridLayoutManager(5, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(managerDemandLayoutManager);
        mSelectDialogRecyclerAdapter = new SelectDialogRecyclerAdapter(mDialogItems, mOnDialogItemClickListener);
        recyclerView.setAdapter(mSelectDialogRecyclerAdapter);
        ViewTreeObserver vto = mLlChatSelectDialog.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (vto.isAlive()) {
                    mLlChatSelectDialog.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                mHeight = mLlChatSelectDialog.getHeight();

                if (null != mViewHeightListener) {
                    mViewHeightListener.setDialogHeight(mHeight);
                }
                mLlChatSelectDialog.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void setViewHeightListener(ViewHeightListener mViewHeightListener) {
        this.mViewHeightListener = mViewHeightListener;
    }

    public interface OnDialogItemClickListener {
        void onItemSelect(String item);
    }

    public interface ViewHeightListener {
        void setDialogHeight(int height);
    }

}
