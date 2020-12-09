package com.foreveross.atwork.modules.search.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.TitleItemView;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.modules.search.component.SearchListItemView;
import com.foreveross.atwork.modules.search.model.SearchAction;
import com.foreveross.atwork.modules.search.model.SearchTextTitleItem;
import com.foreveross.atwork.modules.search.model.SearchViewType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/3/27.
 * Description:
 * 搜索结果的BaseAdapter
 */
public class SearchListAdapter extends BaseAdapter {

    //搜索到的用户信息结果ITEM(好友跟雇员)
    public List<ShowListItem> userSearchItem = new ArrayList<>();
    private Activity mActivity;
    //搜索到的group结果ITEM
    private List<Discussion> discussionSearchItem = new ArrayList<>();
    //搜索到的应用结果ITEM
    private List<App> appSearchItem = new ArrayList<>();
    //搜索到的聊天记录ITEM
    private List<ShowListItem> chatSearchItem = new ArrayList<>();
    //搜索到的设备 ITEM
    private List<ShowListItem> deviceSearchItem = new ArrayList<>();

    private SearchTextTitleItem moreTip;
    private SearchTextTitleItem userItem;
    private SearchTextTitleItem deviceItem;
    private SearchTextTitleItem groupItem;
    private SearchTextTitleItem appItem;
    private SearchTextTitleItem chatItem;
    private SearchTextTitleItem bingItem;
    private List<ShowListItem> searchItems = new ArrayList<>();
    private String key;

    private SearchAction mSearchAction;
    private boolean mNeedSelectStatus = false;


    public SearchListAdapter(Activity activity, SearchAction searchAction) {
        this.mActivity = activity;
        this.mSearchAction = searchAction;

    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setNeedSelectStatus(boolean needSelectStatus) {
        mNeedSelectStatus = needSelectStatus;
    }

    /**
     * 设置人员搜索结果
     *
     * @param empSearchItemList
     */
    public void setUserSearchItem(List<ShowListItem> empSearchItemList, SearchAction searchAction) {

        if(SearchAction.VOIP.equals(searchAction)) {
            ShowListItem removedContact = null;
            for(ShowListItem contact : empSearchItemList) {
                if(User.isYou(mActivity, contact.getId())) {
                    removedContact = contact;
                    break;
                }
            }

            if(null != removedContact)  {
                empSearchItemList.remove(removedContact);

            }
        }

        this.userSearchItem.clear();
        this.userSearchItem.addAll(empSearchItemList);
        refresh();
    }

    /**
     * 追加人员搜索结果
     *
     * @param friendUserSearchItem
     */
    public void addUserSearchItem(List<ShowListItem> friendUserSearchItem, SearchAction searchAction) {
        List<ShowListItem> bucketList = new ArrayList<>();

        if(SearchAction.VOIP.equals(searchAction)) {
            ShowListItem removedContact = null;
            for(ShowListItem contact : friendUserSearchItem) {
                if(User.isYou(mActivity, contact.getId())) {
                    removedContact = contact;
                    break;
                }
            }

            if(null != removedContact)  {
                friendUserSearchItem.remove(removedContact);

            }
        }


        //去重处理
        for(ShowListItem friendListItem : friendUserSearchItem) {
            boolean isDuplicated = false;

            for(ShowListItem showListItem : this.userSearchItem) {
                if(friendListItem.getId().equals(showListItem.getId())) {
                    isDuplicated = true;
                    break;
                }

            }

            if(!isDuplicated) {
                bucketList.add(friendListItem);

            }
        }


        this.userSearchItem.addAll(bucketList);
        refresh();
    }


    /**
     * 设置设备搜索结果
     * */
    public void setDeviceSearchItem(List<ShowListItem> deviceSearchItem) {
        this.deviceSearchItem = deviceSearchItem;
        refresh();
    }


    /**
     * 设置群组搜索结果
     *
     * @param discussionSearchItem
     */
    public void setDiscussionSearchItem(List<Discussion> discussionSearchItem) {
        this.discussionSearchItem = discussionSearchItem;
        refresh();
    }

    /**
     * 设置APP搜索到的结果
     *
     * @param appSearchItem
     */
    public void setAppSearchItem(List<App> appSearchItem) {
        this.appSearchItem = appSearchItem;
        refresh();
    }

    /**
     * 设置搜索到的聊天结果
     *
     * @param chatSearchItem
     */

    public void addSearchItems(List<ShowListItem> chatSearchItem) {
        this.chatSearchItem.addAll(chatSearchItem);
        refresh();
    }

    /**
     * 清除搜索
     */
    public void clearData() {
        this.userSearchItem.clear();
        this.discussionSearchItem.clear();
        this.chatSearchItem.clear();
        this.appSearchItem.clear();
        this.deviceSearchItem.clear();
        refresh();
    }

    private void refresh() {
        calculateAllSearchItem();
        mActivity.runOnUiThread(() -> {
            notifyDataSetChanged();
        });

    }


    //根据不同的计算结果，计算出所有的ITEM
    private void calculateAllSearchItem() {

        searchItems.clear();

        if (userSearchItem.size() > 0) {
            searchItems.add(getUserItem());
            searchItems.addAll(userSearchItem);
        }

        if (userSearchItem.size() > 20) {
            searchItems.add(getMoreTipItem());
        }

        if(deviceSearchItem.size() > 0) {
            searchItems.add(getDeviceItem());
            searchItems.addAll(deviceSearchItem);
        }

        if (discussionSearchItem.size() > 0) {
            searchItems.add(getGroupItem());
            searchItems.addAll(discussionSearchItem);
        }

        if (appSearchItem.size() > 0) {
            searchItems.add(getAppItem());
            searchItems.addAll(appSearchItem);
        }

        if (chatSearchItem.size() > 0) {
            searchItems.add(getChatItem());
            searchItems.addAll(chatSearchItem);
        }
    }

    @Override
    public int getCount() {
        return searchItems.size();
    }

    @Override
    public ShowListItem getItem(int position) {
        return searchItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        ShowListItem searchItem = getItem(position);
        if (searchItem instanceof SearchTextTitleItem) {
            return SearchViewType.TITLE;
        } else {
            return SearchViewType.COMMON;
        }
    }


    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShowListItem searchItem = getItem(position);
        if(null == convertView) {
            if (searchItem instanceof SearchTextTitleItem) {
                convertView = new TitleItemView(mActivity);

            } else {
                convertView = new SearchListItemView(mActivity, mSearchAction);
                ((SearchListItemView)convertView).setNeedSelectStatus(mNeedSelectStatus);
            }
        }

        if (searchItem instanceof SearchTextTitleItem) {
            SearchTextTitleItem textTitleItem = (SearchTextTitleItem) searchItem;
            TitleItemView titleItemView = (TitleItemView) convertView;

            titleItemView.setTitle(searchItem.getTitle());

            if (textTitleItem.center) {
                titleItemView.center();
                titleItemView.white();
            } else {
                titleItemView.left();
                titleItemView.gray();
            }

        } else {
            SearchListItemView searchListItemView = (SearchListItemView) convertView;

            searchListItemView.refreshView(searchItem, key);
        }

        return convertView;
    }

    public SearchTextTitleItem getMoreTipItem() {
        if (moreTip == null) {
            moreTip = new SearchTextTitleItem(mActivity.getResources().getString(R.string.search_user_too_much));
            moreTip.center = true;
        }
        return moreTip;
    }

    public SearchTextTitleItem getUserItem() {
        if (userItem == null) {
            userItem = new SearchTextTitleItem(mActivity.getResources().getString(R.string.search_title_user));
        }
        return userItem;
    }

    public SearchTextTitleItem getDeviceItem() {
        if (deviceItem == null) {
            deviceItem = new SearchTextTitleItem(mActivity.getResources().getString(R.string.device));
        }
        return deviceItem;
    }

    public SearchTextTitleItem getGroupItem() {
        if (groupItem == null) {
            groupItem = new SearchTextTitleItem(mActivity.getResources().getString(R.string.search_title_group));
        }
        return groupItem;
    }

    public SearchTextTitleItem getAppItem() {
        if (appItem == null) {
            appItem = new SearchTextTitleItem(mActivity.getResources().getString(R.string.search_title_app));
        }
        return appItem;
    }

    public SearchTextTitleItem getChatItem() {
        if (chatItem == null) {
            chatItem = new SearchTextTitleItem(mActivity.getResources().getString(R.string.search_title_chat));
        }
        return chatItem;
    }

    public SearchTextTitleItem getBingItem() {
        if(bingItem == null) {
            bingItem = new SearchTextTitleItem(mActivity.getResources().getString(R.string.search_title_bing));
        }

        return bingItem;
    }


}
