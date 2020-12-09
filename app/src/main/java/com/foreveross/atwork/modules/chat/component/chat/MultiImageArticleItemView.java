package com.foreveross.atwork.modules.chat.component.chat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.modules.chat.adapter.MultiImageListAdapter;
import com.foreveross.atwork.modules.chat.component.ChatDetailItemDataRefresh;
import com.foreveross.atwork.modules.chat.data.AnchorInfo;
import com.foreveross.atwork.modules.chat.inter.ChatItemLongClickListener;
import com.foreveross.atwork.modules.chat.inter.HasChatItemLongClickListener;
import com.foreveross.atwork.modules.chat.util.ArticleItemHelper;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.List;

/**
 * Created by lingen on 15/5/12.
 * Description:
 * 多图文
 */
public class MultiImageArticleItemView extends LinearLayout implements ChatDetailItemDataRefresh, HasChatItemLongClickListener {

    private ImageView coverView;

    private ListView articleListView;

    private ArticleChatMessage articleChatMessage;

    private MultiImageListAdapter multiImageListAdapter;

    private TextView firstArticleView;

    private Session mSession;
    private Context mContext;
    private View mRootView;

    protected ChatItemLongClickListener mChatItemLongClickListener;

    public MultiImageArticleItemView(Context context, Session session) {
        super(context);
        initView(context);
        initData(session);
        mSession = session;
        mContext = context;
        registerListener();

    }

    private void registerListener() {
        coverView.setOnClickListener(v -> {
            ArticleItem articleItem = articleChatMessage.articles.get(0);

            if (articleItem != null) {
                ArticleItemHelper. startWebActivity(mContext, mSession, articleChatMessage, articleItem);
                return;
            }
            AtworkToast.showToast(getResources().getString(R.string.article_url_not_config));
        });
    }


    private void initView(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.component_multi_image_article, this);
        mRootView = view.findViewById(R.id.multi_image_layout);
        coverView = view.findViewById(R.id.multi_image_article_cover);
        int margin = DensityUtil.dip2px(40);
        int width = ScreenUtils.getScreenWidth(context) - margin;
        coverView.getLayoutParams().width = width;
        coverView.getLayoutParams().height = width * 5 / 9;
        articleListView = view.findViewById(R.id.multi_image_article_list_view);
        firstArticleView = view.findViewById(R.id.first_image_article_text);
    }

    private void initData(Session session) {
        multiImageListAdapter = new MultiImageListAdapter(getContext(), session);
        articleListView.setAdapter(multiImageListAdapter);
        coverView.setOnLongClickListener(v -> {
            onLongClickCommond();
            return true;
        });
    }

    private void onLongClickCommond() {
        if (mChatItemLongClickListener != null) {
            mChatItemLongClickListener.showDeleteLongClick(articleChatMessage, getAnchorInfo());
        }
    }


    @Override
    public void refreshItemView(ChatPostMessage message) {
        articleChatMessage = (ArticleChatMessage) message;
        multiImageListAdapter.setArticleItems(articleChatMessage, articleChatMessage.articles);
        firstArticleView.setText(articleChatMessage.articles.get(0).title);

        ImageCacheHelper.displayImage(ArticleItemHelper.getCoverUrl(articleChatMessage.articles.get(0)), coverView, getDisplayImageOptions());

    }

    @Override
    public void refreshMessagesContext(List<ChatPostMessage> messages) {

    }


    @Override
    public String getMsgId() {
        if (articleChatMessage != null) {
            return articleChatMessage.deliveryId;
        }
        return null;
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

    @Override
    public void setChatItemLongClickListener(ChatItemLongClickListener chatItemLongClickListener) {
        mChatItemLongClickListener = chatItemLongClickListener;
    }

    public AnchorInfo getAnchorInfo() {
        mRootView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int[] location = new  int[2] ;
        //获取mLlTextContent在屏幕中的位置
        mRootView.getLocationOnScreen(location);
        //View的面积高度
        int chatViewHeight;
        chatViewHeight = mRootView.getHeight();
        int anchorHeight = location[1];

        AnchorInfo info = new AnchorInfo();
        info.anchorHeight = anchorHeight;
        info.chatViewHeight = chatViewHeight;

        return info;
    }
}
