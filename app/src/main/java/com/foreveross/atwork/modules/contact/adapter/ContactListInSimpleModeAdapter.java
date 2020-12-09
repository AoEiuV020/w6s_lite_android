package com.foreveross.atwork.modules.contact.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.FirstLetterUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.modules.contact.component.ContactListItemInSimpleModeView;
import com.foreveross.atwork.modules.contact.component.LetterTitleItemView;
import com.foreveross.atwork.modules.friend.model.FirstLetterItem;

import java.util.ArrayList;
import java.util.List;


public class ContactListInSimpleModeAdapter extends BaseAdapter {
    private Context mCtx;
    private List<ShowListItem> mItemList = new ArrayList<>();
    private List<String> mNotAllowedSelectedContacts;
    private boolean mSelectedMode;
    private boolean mIsSingleContact;


    public ContactListInSimpleModeAdapter(Context context, boolean selectedMode, boolean isSingleContact) {
        this.mCtx = context;
        this.mSelectedMode = selectedMode;
        this.mIsSingleContact = isSingleContact;
    }

    public void setNotAllowedSelectedContacts(List<String> notAllowedSelectedContacts) {
        this.mNotAllowedSelectedContacts = notAllowedSelectedContacts;
    }

    public void refreshData(List<ShowListItem> contactList) {
        mItemList.clear();

        List<String> letterList = new ArrayList<>();

        List<ShowListItem> unLetterContactList = new ArrayList<>();

        for (ShowListItem contact : contactList) {
            String firstLetter;

            if (!(StringUtils.isEmpty(contact.getTitlePinyin()))) {
                firstLetter = (String) contact.getTitlePinyin().subSequence(0, 1);

            } else {
                firstLetter = FirstLetterUtil.getFirstLetter(contact.getTitleI18n(BaseApplicationLike.baseContext));
            }

            firstLetter = firstLetter.toUpperCase();

            //如果非字母, 则用 "#"
            if (!FirstLetterUtil.isLetter(firstLetter)) {
                unLetterContactList.add(contact);

            } else {
                if (!letterList.contains(firstLetter)) {


                    mItemList.add(new FirstLetterItem(firstLetter));

                    letterList.add(firstLetter);
                }

                mItemList.add(contact);

            }

        }

        /**
         * 因为此处所得的 user 列表是通过数据库的拼音字段升序排序的, 所以默认非字母会排在前面, 此处通过逻辑把非字母
         的添加到尾巴
         */
        if (!ListUtil.isEmpty(unLetterContactList)) {
            mItemList.add(new FirstLetterItem("#"));
            mItemList.addAll(unLetterContactList);
        }

        notifyDataSetChanged();

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
                convertView = new ContactListItemInSimpleModeView(mCtx);
            }
        }


        if (showListItem instanceof FirstLetterItem) {
            LetterTitleItemView letterView = (LetterTitleItemView) convertView;

            refreshLetterViewParams(position, letterView);

            letterView.setText(((FirstLetterItem) showListItem).mLetter.toUpperCase());

        } else {
            ContactListItemInSimpleModeView contactListItemInSimpleModeView = (ContactListItemInSimpleModeView) convertView;
            contactListItemInSimpleModeView.setSelectedMode(mSelectedMode);
            contactListItemInSimpleModeView.setSingleContact(mIsSingleContact);
            contactListItemInSimpleModeView.setNotAllowedSelectedContacts(mNotAllowedSelectedContacts);

            contactListItemInSimpleModeView.refreshContactView(showListItem);
        }


        return convertView;
    }

    private void refreshLetterViewParams(int position, LetterTitleItemView letterView) {
        if(0 == position) {
            ViewUtil.setTopMargin(letterView.getTitleView(), 0);
        } else {

            ViewUtil.setTopMargin(letterView.getTitleView(), DensityUtil.dip2px(6));

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
