package com.foreveross.atwork.modules.search.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.foreveross.atwork.R;
import com.foreveross.theme.manager.SkinHelper;

/**
 * Created by lingen on 15/5/21.
 * Description:
 */
public class SearchHeadView extends LinearLayout {

    private EditText mEditTextSearch;

    private RelativeLayout mRlClearSearch;

    private View mSearchUnderline;

    private LinearLayout mLayout;

    private int mHintResId = -1;

    public SearchHeadView(Context context) {
        super(context);
        initView();
    }

    public SearchHeadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_search, this);
        mEditTextSearch = (EditText) view.findViewById(R.id.search_edit_text);
        mRlClearSearch = (RelativeLayout) view.findViewById(R.id.rl_clear_search);
        mSearchUnderline = view.findViewById(R.id.search_under_line);
        mLayout = (LinearLayout) view.findViewById(R.id.head_layout);

        mRlClearSearch.setOnClickListener(v -> mEditTextSearch.setText(""));
        initSearchViewColor();

    }


    private void initSearchViewColor() {
        mEditTextSearch.setHintTextColor(SkinHelper.getSecondaryTextColor());
        mEditTextSearch.setTextColor(SkinHelper.getPrimaryTextColor());
    }

    public void setHint(int resId) {
        mHintResId = resId;
        mEditTextSearch.setHint(resId);
    }

    public void refreshText() {
        if(-1 != mHintResId) {
            mEditTextSearch.setHint(mHintResId);
        }
    }

    public void setSearchUnderlineVisible(int visible) {
        mSearchUnderline.setVisibility(visible);
    }

    public void setBackground(int color) {
        mLayout.setBackgroundColor(color);
    }

    public EditText getEditTextSearch() {
        return mEditTextSearch;
    }

    public RelativeLayout getImageViewClearSearch() {
        return mRlClearSearch;
    }

    public void clearFocusableInTouchMode() {
        mLayout.setFocusableInTouchMode(false);
    }


}
