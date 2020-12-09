package com.foreveross.atwork.modules.chat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleChatMessage;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ArticleItem;
import com.foreveross.atwork.modules.chat.component.ImageArticleItemView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/6/3.
 * Description:
 */
public class MultiImageListAdapter extends BaseAdapter {


    private ArticleChatMessage articleChatMessage;

    private List<ArticleItem> articleItemList = new ArrayList<>();

    private Context context;

    private Session mSession;

    public MultiImageListAdapter(Context context) {
        this.context = context;
    }

    public MultiImageListAdapter(Context context, Session session) {
        this.context = context;
        mSession = session;
    }

    public void setArticleItems(ArticleChatMessage articleChatMessage, List<ArticleItem> articleItemList) {
        this.articleChatMessage = articleChatMessage;
        this.articleItemList.clear();
        this.articleItemList.addAll(articleItemList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return articleItemList.size() - 1;
    }

    @Override
    public ArticleItem getItem(int position) {
        return articleItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        ArticleItem articleItem = getItem(position+1);
        if (convertView == null) {
            viewHolder=new ViewHolder();
            convertView = new ImageArticleItemView(context, mSession);
            viewHolder.imgView=(ImageArticleItemView)convertView;
            convertView.setTag(viewHolder);
        }else {
            viewHolder=(ViewHolder)convertView.getTag();
        }
        ImageArticleItemView imageArticleItemView = viewHolder.imgView;
        imageArticleItemView.refreshView(articleChatMessage, articleItem);
        return convertView;
    }

    public static class ViewHolder{
        ImageArticleItemView imgView;
    }

}
