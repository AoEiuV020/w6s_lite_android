package com.foreveross.atwork.utils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by dasunsy on 2017/12/20.
 */

public class RecyclerViewUtil {

    public static boolean isRecyclerScrollable(RecyclerView recyclerView) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (null == layoutManager || null == adapter) {
            return false;
        }

        int lastCompletelyVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();

        if(-1 == lastCompletelyVisibleItemPosition) {
            return false;
        }

        return lastCompletelyVisibleItemPosition < adapter.getItemCount() - 1;
    }
}
