package com.foreveross.atwork.modules.contact.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.contact.component.ContactListItemView;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;

import java.util.ArrayList;
import java.util.List;


public class ContactListArrayListAdapter extends ArrayAdapter<ShowListItem> {

    public static final int MODE_NORMAL = 0;

    //已读时间模式
    public static final int MODE_READ_TIME = 1;

    //显示job title信息的模式
    public static final int MODE_JOB_INFO = 2;


    private Activity mActivity;

    private boolean mSelectedMode = false;

    private int mDisplayMode = MODE_NORMAL;


    private boolean mNeedLine = true;

    private boolean mMediumBoldTitleMode = false;

    private boolean mIsSingleContact = false;

    private UserSelectActivity.SelectAction mSelectAction;
    private List<ShowListItem> mSelectContacts;
    private List<String> mNotAllowedSelectedContacts;

    private String mDiscussionId;


    public ContactListArrayListAdapter(Activity activity, boolean selectedMode) {
        super(activity, 0, new ArrayList<>());
        this.mSelectedMode = selectedMode;
        this.mActivity = activity;
    }

    public void setSelectAction(UserSelectActivity.SelectAction selectAction) {
        this.mSelectAction = selectAction;
    }

    public void setSelectContacts(List<ShowListItem> mSelectContacts) {
        this.mSelectContacts = mSelectContacts;
    }

    public void setNotAllowedSelectedContacts(List<String> notAllowedSelectedContacts) {
        this.mNotAllowedSelectedContacts = notAllowedSelectedContacts;
    }

    public void readTimeMode(boolean readTimeMode) {
        if(readTimeMode) {
            this.mDisplayMode = MODE_READ_TIME;

        } else {
            this.mDisplayMode = MODE_NORMAL;

        }
    }

    public void setDisplayMode(int mode) {
        this.mDisplayMode = mode;
    }

    public void needLine(boolean isNeed) {
        this.mNeedLine = isNeed;
    }

    public void setMediumBoldMode(boolean mediumBoldTitleMode) {
        this.mMediumBoldTitleMode = mediumBoldTitleMode;
    }

    public void setDiscussionId(String discussionId) {
        this.mDiscussionId = discussionId;
    }

    public void refreshData(List<User> contactItemList) {
        clear();
        addAll(contactItemList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new ContactListItemView(getContext());
        }

        ContactListItemView contactListItemView = (ContactListItemView) convertView;
        contactListItemView.setSelectAction(mSelectAction);
        contactListItemView.setSelectContacts(mSelectContacts);
        contactListItemView.setNotAllowedSelectedContacts(mNotAllowedSelectedContacts);

        if (mIsSingleContact) {
            contactListItemView.setSelectedMode(false);
        } else {
            contactListItemView.setSelectedMode(mSelectedMode);
        }
        ShowListItem userItem = getItem(position);

        contactListItemView.refreshContactView(userItem);

        switch (mDisplayMode) {
            case MODE_READ_TIME:
                ((ContactListItemView) (convertView)).readTimeMode();

                break;

            case MODE_JOB_INFO:
                ((ContactListItemView) (convertView)).jobTitleMode();

                break;

            default:
                ((ContactListItemView) (convertView)).normalMode();

        }

        if(!StringUtils.isEmpty(mDiscussionId)) {
            contactListItemView.refreshMemberTagView(mDiscussionId);
        }

        if (mNeedLine) {
            contactListItemView.setLineVisible(getCount() - 1 != position);
        } else {
            contactListItemView.setLineVisible(false);
        }

        if(mMediumBoldTitleMode) {
            contactListItemView.mediumBold();
        }

        return convertView;
    }


    public void setSingleContact(boolean isContact) {
        mIsSingleContact = isContact;
    }

}
