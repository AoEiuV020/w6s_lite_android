package com.foreveross.atwork.modules.contact.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.manager.FriendManager;
import com.foreveross.atwork.modules.contact.data.StarUserListDataWrap;
import com.foreveross.atwork.modules.contact.util.ContactInfoViewUtil;
import com.foreveross.atwork.utils.AvatarHelper;

import java.util.List;


public class ContactListItemInSimpleModeView extends RelativeLayout {

    private ImageView mAvatarView;
    private TextView mTitleView;
    private ImageView mIvLabel1;
    private ImageView mIvLabel2;
    private ImageView mSelectView;

    private ShowListItem mContact;
    private List<String> mNotAllowedSelectedContacts;

    private boolean mSelectedMode = false;
    private boolean mIsSingleContact = false;

    public ContactListItemInSimpleModeView(Context context) {
        super(context);
        initView();
        registerListener();
    }

    public ContactListItemInSimpleModeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        registerListener();
    }

    public void setSelectedMode(boolean selectedMode) {
        this.mSelectedMode = selectedMode;
    }

    private void registerListener() {

    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_contact_in_simple_mode_list_item, this);
        mAvatarView = view.findViewById(R.id.contact_list_item_avatar);
        mTitleView = view.findViewById(R.id.contact_list_item_title);
        mIvLabel1 = view.findViewById(R.id.iv_label_1);
        mIvLabel2 = view.findViewById(R.id.iv_label_2);
        mSelectView = view.findViewById(R.id.chat_list_select);

        mSelectView.setVisibility(GONE);

    }




    public void refreshSelected() {
        if (mSelectedMode && !mIsSingleContact) {
            showSelect();

            if(null != mNotAllowedSelectedContacts && mNotAllowedSelectedContacts.contains(mContact.getId())) {
                mSelectView.setImageResource(R.mipmap.icon_selected_disable_new);
                return;
            }

            if (mContact.isSelect()) {
                select();
                return;
            }

            unselect();
            return;
        }


        hideSelect();
    }

    public void hideSelect() {
        mSelectView.setVisibility(GONE);
    }

    public void showSelect() {
        mSelectView.setVisibility(VISIBLE);
    }

    public void unselect() {
        mSelectView.setImageResource(R.mipmap.icon_seclect_no_circular);
    }

    public void select() {
        mSelectView.setImageResource(R.mipmap.icon_selected);
    }

    public void refreshCommonContactUI() {
        refreshSelected();


        refreshInfoView();


    }

    private void refreshInfoView() {
        if(mContact instanceof Discussion) {
            Discussion discussion = (Discussion) mContact;
            AvatarHelper.setDiscussionAvatarById(mAvatarView, discussion.mDiscussionId, true, true);
            mTitleView.setText(discussion.getTitle());

            if(discussion.isInternalDiscussion()) {
                mIvLabel1.setImageResource(R.mipmap.icon_internal_discussion);

            } else {

                mIvLabel1.setImageResource(0);

            }

            mIvLabel2.setImageResource(0);

        } else {
            ContactInfoViewUtil.dealWithContactInitializedStatus(mAvatarView, mTitleView, mContact, false, true);

            if(FriendManager.getInstance().containsKey(mContact.getId())) {
                mIvLabel1.setImageResource(R.mipmap.icon_friend_label);
            } else {
                mIvLabel1.setImageResource(0);
            }

            if(StarUserListDataWrap.getInstance().containsKey(mContact.getId())) {
                mIvLabel2.setImageResource(R.mipmap.icon_star);

            } else {
                mIvLabel2.setImageResource(0);
            }


        }

        ViewUtil.setVisible(mIvLabel1, null != mIvLabel1.getDrawable());
        ViewUtil.setVisible(mIvLabel2, null != mIvLabel2.getDrawable());
    }


    public void refreshContactView(ShowListItem contact) {
        mContact = contact;
        refreshCommonContactUI();

    }

    public void setNotAllowedSelectedContacts(List<String> notAllowedSelectedContacts) {
        this.mNotAllowedSelectedContacts = notAllowedSelectedContacts;
    }




    public ImageView getAvatarView() {
        return mAvatarView;
    }

    public TextView getTitleView() {
        return mTitleView;
    }

    public ImageView getSelectView() {
        return mSelectView;
    }


    public ImageView getIvLabel1() {
        return mIvLabel1;
    }

    public ImageView getIvLabel2() {
        return mIvLabel2;
    }

    public void setSingleContact(boolean isSingleContact) {
        this.mIsSingleContact = isSingleContact;
    }
}
