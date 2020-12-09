package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.modules.chat.component.ChatDetailItemDataRefresh;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.inter.ChatItemLongClickListener;
import com.foreveross.atwork.modules.chat.inter.HasChatItemLongClickListener;
import com.foreveross.atwork.modules.chat.util.ArticleItemHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.TimeViewUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

/**
 * Created by lingen on 15/5/12.
 * Description:
 * 单图文消息
 */
public class SingleImageArticleItemView extends LinearLayout implements ChatDetailItemDataRefresh, HasChatItemLongClickListener {


    private ArticleChatMessage articleChatMessage;

    private TextView titleView;

    private TextView timeView;

    private ImageView coverView;

    private TextView summaryView;

    private TextView readView;

    private ArticleItem articleItem;

    private LinearLayout mSingleImageArticleLayout;

    private Session mSession;
    private Context mContext;

    protected ChatItemLongClickListener mChatItemLongClickListener;

    public SingleImageArticleItemView(Context context, AttributeSet attrs, Session session) {
        super(context, attrs);
        initView(context);
        mSession = session;
        mContext = context;
        registerListener();

    }


    public SingleImageArticleItemView(Context context, Session session) {
        super(context);
        initView(context);
        mSession = session;
        mContext = context;
        registerListener();

    }

    private void initView(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.component_single_imag_article, this);
        titleView = view.findViewById(R.id.single_article_title);
        timeView = view.findViewById(R.id.single_article_time);
        coverView = view.findViewById(R.id.single_article_cover);

        int margin = DensityUtil.dip2px(40);
        int width = ScreenUtils.getScreenWidth(context) - margin;
        coverView.getLayoutParams().width = width;
        coverView.getLayoutParams().height = width * 5 / 9;

        summaryView = view.findViewById(R.id.single_article_summary);
        readView = view.findViewById(R.id.single_read_article);
        mSingleImageArticleLayout = view.findViewById(R.id.single_article_layout);

        mSingleImageArticleLayout.setOnLongClickListener(v -> {
            if (mChatItemLongClickListener != null) {
                mChatItemLongClickListener.showDeleteLongClick(articleChatMessage, getAnchorInfo());
            }
            return true;
        });
    }

    @Override
    public void refreshItemView(ChatPostMessage message) {
        articleChatMessage = (ArticleChatMessage) message;
        articleItem = articleChatMessage.articles.get(0);
        titleView.setText(articleItem.title);
        timeView.setText(TimeViewUtil.getUserCanViewTime(BaseApplicationLike.baseContext, articleItem.createTime));
        summaryView.setText(articleItem.summary);

        ImageCacheHelper.displayImage(ArticleItemHelper.getCoverUrl(articleItem), coverView, getDisplayImageOptions());


    }

    @Override
    public void refreshMessagesContext(List<ChatPostMessage> messages) {

    }

    private DisplayImageOptions getDisplayImageOptions() {
        DisplayImageOptions.Builder builder = new DisplayImageOptions.Builder();
        builder.cacheOnDisk(true);
        builder.cacheInMemory(true);
        builder.showImageOnLoading(R.mipmap.loading_cover_size);
        builder.showImageForEmptyUri(R.mipmap.loading_cover_size);
        builder.showImageOnFail(R.mipmap.loading_cover_size);
        return builder.build();
    }


    private void registerListener() {
        mSingleImageArticleLayout.setOnClickListener(v -> toDetailInfo());
    }

    private void toDetailInfo() {
        if (articleItem != null) {
            ArticleItemHelper.startWebActivity(mContext, mSession, articleChatMessage, articleItem);
            return;
        }
        AtworkToast.showToast(getResources().getString(R.string.article_url_not_config));
    }


    @Override
    public String getMsgId() {
        if (articleChatMessage != null) {
            return articleChatMessage.deliveryId;
        }
        return null;
    }

    @Override
    public void setChatItemLongClickListener(ChatItemLongClickListener chatItemLongClickListener) {
        mChatItemLongClickListener = chatItemLongClickListener;
    }

    public AnchorInfo getAnchorInfo() {
        mSingleImageArticleLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int[] location = new  int[2] ;
        //获取mLlTextContent在屏幕中的位置
        mSingleImageArticleLayout.getLocationOnScreen(location);
        //View的面积高度
        int chatViewHeight;
        chatViewHeight = mSingleImageArticleLayout.getHeight();
        int anchorHeight = location[1];

        AnchorInfo info = new AnchorInfo();
        info.anchorHeight = anchorHeight;
        info.chatViewHeight = chatViewHeight;

        return info;
    }
}
