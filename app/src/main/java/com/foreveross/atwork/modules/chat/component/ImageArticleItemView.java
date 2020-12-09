package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.clickStatistics.Type;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.modules.chat.util.ArticleItemHelper;
import com.foreveross.atwork.modules.clickStatistics.ClickStatisticsManager;
import com.foreveross.atwork.modules.newsSummary.util.CheckUnReadUtil;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ImageCacheHelper;

/**
 * Created by lingen on 15/5/12.
 * Description:
 * 多图文消息中的单条图文
 */
public class ImageArticleItemView extends RelativeLayout {

    private TextView mTitleView;

    private ImageView mCoverView;

    private ArticleChatMessage mArticleChatMessage;

    private ArticleItem mArticleItem;

    public ImageArticleItemView(Context context, Session session) {
        super(context);
        initView();
        registerListener(session);
    }


    /**
     * UI初始化
     */
    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_multi_image_article_item, this);
        mTitleView = view.findViewById(R.id.article_title);
        mCoverView = view.findViewById(R.id.article_image);
        //预设底图
        mCoverView.setImageResource(R.mipmap.bg_avatar);

    }


    private void registerListener(final Session session) {
        setOnClickListener(v -> {
            //使用16进制色无效，原因未知
            setBackgroundColor(Color.rgb(220, 220, 220));
            new Handler().postDelayed(() -> setBackgroundColor(0), 200);

            if (mArticleItem != null) {
                ArticleItemHelper.startWebActivity(getContext(), session, mArticleChatMessage, mArticleItem);
                CheckUnReadUtil.Companion.CompareTime(session.identifier,mArticleChatMessage.deliveryTime);
                //更新点击率
                ClickStatisticsManager.INSTANCE.updateClick(session.identifier, Type.NEWS_SUMMARY);
                return;
            }
            AtworkToast.showToast(getResources().getString(R.string.article_url_not_config));
        });
    }

    public void refreshView(ArticleChatMessage articleChatMessage, ArticleItem articleItem) {
        this.mArticleChatMessage = articleChatMessage;
        this.mArticleItem = articleItem;

        mTitleView.setText(articleItem.title);

        ImageCacheHelper.displayImage(ArticleItemHelper.getCoverUrl(articleItem), mCoverView, ImageCacheHelper.getRectOptions(R.mipmap.loading_icon_square));

    }

}
