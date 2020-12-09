package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.utils.ImageCacheHelper;

/**
 * Created by dasunsy on 2017/9/4.
 */

public class PopLinkTranslatingView extends RelativeLayout {

    private View mLoadingView;
    private View mVUrlShareCard;
    private TextView mTitle;
    private ImageView mCoverImage;
    private TextView mSummary;
    private View mNotMatchView;
    private OnShareLinkClickListener mListener;
    private ArticleItem mArticleItem;

    public PopLinkTranslatingView(Context context) {
        super(context);
        initViews();
    }
    public PopLinkTranslatingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PopLinkTranslatingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }


    public void setOnShareLinkClickListener(OnShareLinkClickListener listener) {
        mListener = listener;
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_pop_link_translating_window, this);
        mLoadingView = view.findViewById(R.id.pb_loading);
        mVUrlShareCard = view.findViewById(R.id.rl_url_card);
        mTitle = mVUrlShareCard.findViewById(R.id.tv_title);
        mCoverImage = mVUrlShareCard.findViewById(R.id.iv_cover);
        mSummary = mVUrlShareCard.findViewById(R.id.tv_content);
        mNotMatchView = view.findViewById(R.id.not_match_view);

        this.setOnClickListener(v -> {
            if (mListener == null || !mVUrlShareCard.isShown()) {
                return;
            }
            mListener.onShareLinkClick(mArticleItem);
        });
    }

    public boolean isShowing() {
        return mLoadingView.isShown() || mVUrlShareCard.isShown() || mNotMatchView.isShown();
    }

    public void translating() {
        this.setVisibility(VISIBLE);
        mLoadingView.setVisibility(VISIBLE);
        mVUrlShareCard.setVisibility(GONE);
        mNotMatchView.setVisibility(GONE);
    }

    public void showMatchedResult(ArticleItem articleItem) {
        mArticleItem = articleItem;
        this.setVisibility(VISIBLE);
        mLoadingView.setVisibility(GONE);
        mVUrlShareCard.setVisibility(VISIBLE);
        mNotMatchView.setVisibility(GONE);

        setContent(articleItem);
    }

    public void showNotMatch() {
        this.setVisibility(VISIBLE);
        mLoadingView.setVisibility(GONE);
        mVUrlShareCard.setVisibility(GONE);
        mNotMatchView.setVisibility(VISIBLE);
    }

    public void nothing() {
        this.setVisibility(GONE);
    }

    private void setContent(ArticleItem articleItem) {
        if (articleItem == null) {
            return;
        }
        mTitle.setText(articleItem.title);
        mSummary.setText(articleItem.summary);
        ImageCacheHelper.displayImage(articleItem.mCoverUrl, mCoverImage, ImageCacheHelper.getRectOptions(R.mipmap.default_link), null);
    }

    public interface OnShareLinkClickListener {
        void onShareLinkClick(ArticleItem articleItem);
    }
}
