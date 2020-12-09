package com.foreveross.atwork.modules.friend.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.FirstLetterUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.OnlineManager;
import com.foreveross.atwork.modules.friend.component.FriendListItemVIew;
import com.foreveross.atwork.modules.friend.component.LetterTitleItemView;
import com.foreveross.atwork.modules.friend.model.FirstLetterItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 16/5/19.
 */
public class FriendListAdapter extends BaseAdapter {
    private Context mCtx;
    private List<ShowListItem> mItemList = new ArrayList<>();
    private List<String> mNotAllowedSelectedContacts;
    private boolean mSelectedMode;
    private boolean mIsSingleContact;


    public FriendListAdapter(Context context, boolean selectedMode, boolean singleContact) {
        this.mCtx = context;
        this.mSelectedMode = selectedMode;
        this.mIsSingleContact = singleContact;
    }

    public void setNotAllowedSelectedContacts(List<String> notAllowedSelectedContacts) {
        this.mNotAllowedSelectedContacts = notAllowedSelectedContacts;
    }

    public void setData(List<User> userList) {
        mItemList.clear();

        List<String> letterList = new ArrayList<>();

        List<User> unLetterUserList = new ArrayList<>();

        for (User user : userList) {
            String firstLetter;

            if (!(StringUtils.isEmpty(user.mInitial))) {
                firstLetter = (String) user.mInitial.subSequence(0, 1);

            } else {
                firstLetter = (String) user.mPinyin.subSequence(0, 1);

            }

            firstLetter = firstLetter.toUpperCase();

            //如果非字母, 则用 "#"
            if (!FirstLetterUtil.isLetter(firstLetter)) {
                unLetterUserList.add(user);

            } else {
                if (!letterList.contains(firstLetter)) {


                    mItemList.add(new FirstLetterItem(firstLetter));

                    letterList.add(firstLetter);
                }

                mItemList.add(user);

            }

        }

        /**
         * 因为此处所得的 user 列表是通过数据库的拼音字段升序排序的, 所以默认非字母会排在前面, 此处通过逻辑把非字母
         的添加到尾巴
         */
        if (!ListUtil.isEmpty(unLetterUserList)) {
            mItemList.add(new FirstLetterItem("#"));
            mItemList.addAll(unLetterUserList);
        }

    }

    @Override
    public int getItemViewType(int position) {
        ShowListItem searchItem = getItem(position);
        if (searchItem instanceof FirstLetterItem) {
            return ViewType.TITLE;
        } else {
            return ViewType.COMMON;
        }
    }


    @Override
    public int getViewTypeCount() {
        return ViewType.COUNT;
    }

    @Override
    public int getCount() {
        return mItemList.size();
    }

    @Override
    public ShowListItem getItem(int position) {
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ShowListItem showListItem = getItem(position);
        if (null == convertView) {
            if (showListItem instanceof FirstLetterItem) {
                convertView = new LetterTitleItemView(mCtx);
            } else {
                convertView = new FriendListItemVIew(mCtx);
            }
        }


        if (showListItem instanceof FirstLetterItem) {
            LetterTitleItemView letterView = (LetterTitleItemView) convertView;
            letterView.setText(((FirstLetterItem) showListItem).mLetter.toUpperCase());

        } else {
            FriendListItemVIew friendView = (FriendListItemVIew) convertView;
            friendView.setSelectedMode(mSelectedMode);
            friendView.setSingleContact(mIsSingleContact);
            friendView.refreshView(showListItem, mNotAllowedSelectedContacts, OnlineManager.getInstance().isOnline(showListItem.getId()));
            refreshLine(position, friendView);
        }


        return convertView;
    }

    public void refreshLine(int position, FriendListItemVIew friendView) {
        try {
            if (position != getCount() - 1) {
                ShowListItem nextShowListItem = getItem(position + 1);

                if (nextShowListItem instanceof FirstLetterItem) {
                    friendView.hideLine();

                } else {
                    friendView.showLine();

                }
            } else {
                friendView.hideLine();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public int getSectionForPosition(int position) {
//        return list.get(position).getSortLetters().charAt(0);
//    }

    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            ShowListItem item = mItemList.get(i);
            if (item instanceof FirstLetterItem) {
                String sortStr = ((FirstLetterItem) item).mLetter;
                char firstChar = sortStr.toUpperCase().charAt(0);
                if (firstChar == section) {
                    return i;
                }
            }
            continue;
        }

        return -1;
    }

    final class ViewType {
        public final static int COUNT = 2;

        public final static int TITLE = 0;
        public final static int COMMON = 1;
    }
}
