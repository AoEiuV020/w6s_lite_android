package com.foreveross.atwork.modules.friend.component;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.utils.AvatarHelper;

import java.util.List;

/**
 * Created by dasunsy on 16/5/19.
 */
public class FriendListItemVIew extends RelativeLayout{
    private ImageView mAvatarView;
    private ImageView mSelectView;
    private TextView mNameView;
    private TextView mMobileView;
    private ImageView mIvLine;

    private boolean mSelectedMode = false;
    private boolean mIsSingleContact = false;

    public FriendListItemVIew(Context context) {
        super(context);
        initViews();
    }

    public FriendListItemVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    private void initViews() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.list_item_friend, this);
        mAvatarView = view.findViewById(R.id.iv_avatar);
        mNameView = view.findViewById(R.id.tv_name);
        mMobileView = view.findViewById(R.id.tv_mobile);
        mSelectView = view.findViewById(R.id.iv_select);
        mIvLine = view.findViewById(R.id.iv_line);
        mSelectView.setVisibility(GONE);

    }

    public void setSelectedMode(boolean selectedMode) {
        this.mSelectedMode = selectedMode;
    }

    public void setSingleContact(boolean singleContact) {
        this.mIsSingleContact = singleContact;
    }

    public void refreshView(ShowListItem showListItem, List<String> notAllowedSelectedContacts, boolean online) {
        User user = (User) showListItem;
        refreshSelected(user, notAllowedSelectedContacts);
        setOnlineStatus(user, online);

    }

    private void refreshSelected(User user, List<String> notAllowedSelectedContacts) {
        if(mIsSingleContact) {
            mSelectView.setVisibility(GONE);
            return;
        }


        if (mSelectedMode) {
            mSelectView.setVisibility(VISIBLE);

            if(notAllowedSelectedContacts.contains(user.getId())) {
                mSelectView.setImageResource(R.mipmap.icon_selected_disable_new);
                return;
            }

            if (user.mSelect) {
                mSelectView.setImageResource(R.mipmap.icon_selected);
            } else {
                mSelectView.setImageResource(R.mipmap.icon_seclect_no_circular);
            }
            return;
        }

        mSelectView.setVisibility(GONE);
    }

    public void showLine() {
        mIvLine.setVisibility(VISIBLE);
    }


    public void hideLine() {
        mIvLine.setVisibility(GONE);
    }

    public void setOnlineStatus(User user, boolean online) {
        boolean onlineFeature = DomainSettingsManager.getInstance().handleUserOnlineFeature();
        ColorMatrix matrix = new ColorMatrix();
        if (online || !onlineFeature) {
            matrix.reset();
        } else {
//            matrix.setSaturation(0);
        }
        AvatarHelper.setUserAvatarByAvaId(user.mAvatar, mAvatarView, false, true);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        mAvatarView.setColorFilter(filter);

        mNameView.setText(online || !onlineFeature ? user.getShowName() : user.getShowName() + " " + getContext().getString(R.string.tip_not_online));
        mMobileView.setText(user.mPhone);
    }
}
