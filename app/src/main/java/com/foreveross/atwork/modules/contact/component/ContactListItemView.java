package com.foreveross.atwork.modules.contact.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.modules.contact.util.ContactInfoViewUtil;
import com.foreveross.atwork.modules.discussion.util.DiscussionUIHelper;
import com.foreveross.atwork.modules.discussion.util.RefreshMemberTagViewCarrier;
import com.foreveross.atwork.modules.file.service.FileTransferService;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.utils.TextViewHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lingen on 15/4/23.
 * Description:
 */
public class ContactListItemView extends RelativeLayout {

    //头像
    private ImageView mAvatarView;
    private TextView mTitleView;
    private TextView mInfoView;
    private TextView mJobView;
    private ImageView mSelectView;
    private LinearLayout mLlTags;
    private View mVLine;

    private ShowListItem mContact;

    private boolean mSelectedMode = false;
    private UserSelectActivity.SelectAction mSelectAction;
    private List<ShowListItem> mSelectContacts;
    private List<String> mNotAllowedSelectedContacts;

    public List<TextView> mTvTagList = new ArrayList<>();


    public ContactListItemView(Context context) {
        super(context);
        initView();
        registerListener();
    }

    public ContactListItemView(Context context, AttributeSet attrs) {
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
        View view = inflater.inflate(R.layout.component_contact_list_item, this);
        mAvatarView = view.findViewById(R.id.contact_list_item_avatar);
        mTitleView = view.findViewById(R.id.contact_list_item_title);
        mInfoView = view.findViewById(R.id.contact_list_item_info);
        mSelectView = view.findViewById(R.id.chat_list_select);
        mJobView = view.findViewById(R.id.contact_list_item_job);
        mLlTags = view.findViewById(R.id.ll_tags);
        mVLine = view.findViewById(R.id.iv_line_chat_search);
        mSelectView.setVisibility(GONE);
        mInfoView.setVisibility(GONE);
        mJobView.setVisibility(GONE);
    }

    public void refreshMemberTagView(String discussionId) {
        RefreshMemberTagViewCarrier carrier = new RefreshMemberTagViewCarrier(
                mContact.getId(),
                discussionId,
                mContact.getId(),
                mTvTagList
        );

        DiscussionUIHelper.refreshMemberTagView(mLlTags, carrier, null);
    }

    public void readTimeMode() {
        mJobView.setVisibility(GONE);
        mInfoView.setVisibility(VISIBLE);
        long readTime = -1;
        if (mContact instanceof Employee) {
            readTime = ((Employee) mContact).mReadTime;

        } else if (mContact instanceof User) {
            readTime = ((User) mContact).readTime;

        }
        mInfoView.setText(getResources().getString(R.string.contact_read_info, TimeUtil.getStringForMillis(readTime, TimeUtil.getTimeFormat2(BaseApplicationLike.baseContext))));
    }

    public void normalMode() {
        mJobView.setVisibility(VISIBLE);
        mInfoView.setVisibility(GONE);
    }

    public void jobTitleMode() {
        mJobView.setVisibility(VISIBLE);
        mInfoView.setVisibility(VISIBLE);
    }


    public void refreshSelected() {
        if (mSelectedMode) {
            showSelect();

            if (UserSelectActivity.SelectAction.SCOPE == mSelectAction
                    && !ContactInfoViewUtil.canEmployeeTreeEmpSelect(mContact, mSelectContacts)) {
                mSelectView.setImageResource(R.mipmap.icon_selected_disable_new);
                return;
            }

            if(null != mNotAllowedSelectedContacts && mNotAllowedSelectedContacts.contains(mContact.getId())) {
                mSelectView.setImageResource(R.mipmap.icon_selected_disable_new);
                return;
            }


            if (mContact.isSelect()) {
                select();
            } else {
                unselect();
            }
        } else {
            hideSelect();

        }
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

        if (mContact instanceof User) {
            if (FileTransferService.INSTANCE.checkVariation(mAvatarView, mTitleView, (User) mContact)) {
                mInfoView.setVisibility(GONE);
                return;
            }
        }

        refreshUserTypeUI();

    }

    private void refreshUserTypeUI() {
        ContactInfoViewUtil.dealWithContactInitializedStatus(mAvatarView, mTitleView, mContact, true, true);

        String jobTitle = StringUtils.EMPTY;
        if (mContact instanceof Employee) {
            jobTitle = ((Employee) mContact).getSearchShowJobTitle();
        }

        if (StringUtils.isEmpty(jobTitle)) {
            mInfoView.setVisibility(GONE);

        } else {
            mInfoView.setText(jobTitle);
            mInfoView.setVisibility(VISIBLE);

        }
    }


    public void refreshSessionContactUI() {
        refreshSelected();
        if (mContact instanceof Session) {
            ContactInfoViewUtil.dealWithSessionInitializedStatus(mAvatarView, mTitleView, (Session) mContact, true);
        }


    }


    public void refreshSessionView(ShowListItem session) {
        mContact = session;
        refreshSessionContactUI();
    }

    public void refreshContactView(ShowListItem contact) {
        mContact = contact;
        refreshCommonContactUI();
    }

    public void setSelectAction(UserSelectActivity.SelectAction selectAction) {
        this.mSelectAction = selectAction;
    }

    public void setSelectContacts(List<ShowListItem> selectContacts) {
        this.mSelectContacts = selectContacts;
    }

    public void setNotAllowedSelectedContacts(List<String> notAllowedSelectedContacts) {
        this.mNotAllowedSelectedContacts = notAllowedSelectedContacts;
    }

    public void mediumBold() {
        TextViewHelper.mediumBold(mTitleView);
    }

    public void setLineVisible(boolean visible) {
        ViewUtil.setVisible(mVLine, visible);
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

    public TextView getInfoView() {
        return mInfoView;
    }

    public View getVLine() {
        return mVLine;
    }
}
