package com.foreveross.atwork.modules.dropbox.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.AtWorkGridView;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.modules.dropbox.adapter.SortedImageAdapter;

import java.util.List;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/9/26.
 */

public class SortedTypeItem extends LinearLayout {

    private AtWorkGridView mSortedImageGrid;
    private View mVLine;

    private SortedImageAdapter mAdapter;
    private OnImageGridItemClickListener mListener;

    public SortedTypeItem(Context context, OnImageGridItemClickListener listener) {
        super(context);
        mListener = listener;
        initViews(context);

    }

    private void initViews(Context context){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_sorted_type_item, this);
        mSortedImageGrid = (AtWorkGridView)view.findViewById(R.id.sorted_image_grid);
        mVLine = view.findViewById(R.id.v_line);

        mAdapter = new SortedImageAdapter(context);
        mSortedImageGrid.setAdapter(mAdapter);
        mSortedImageGrid.setOnItemClickListener((parent, view1, position, id) -> {
            mListener.onImageGridItemClick(mAdapter.getItem(position));
        });
    }

    public void handleLineVisibility(boolean show) {
        if(show) {
            mVLine.setVisibility(VISIBLE);
        } else {
            mVLine.setVisibility(GONE);
        }
    }

    public void setList(List<Dropbox> dropboxes) {
        mAdapter.setList(dropboxes);
    }

    public interface OnImageGridItemClickListener {
        void onImageGridItemClick(Dropbox dropbox);
    }
}
