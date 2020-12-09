package com.foreveross.atwork.modules.group.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrganizationResult;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.file.service.FileTransferService;
import com.foreveross.atwork.utils.AvatarHelper;


public class SelectContactHeadItemView extends LinearLayout {

    private View remove;


    private ShowListItem mContact;

    private ImageView avatarView;

    private RemoveContactListener removeContactListener;


    public SelectContactHeadItemView(Context context) {
        super(context);
        initView();
        registerListener();
    }

    public SelectContactHeadItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        registerListener();
    }

    public void setRemoveContactListener(RemoveContactListener removeContactListener) {
        this.removeContactListener = removeContactListener;
    }

    private void registerListener() {
        remove.setOnClickListener(v -> removeContactListener.removeContact(mContact));

    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_select_user_head_item, this);
        remove = view.findViewById(R.id.select_user_head_remove);
        avatarView = view.findViewById(R.id.select_user_head_icon);
    }

    public void refreshView(ShowListItem contact) {
        mContact = contact;


        if(contact instanceof Session) {

            refreshView((Session) contact);

        } else if(contact instanceof OrganizationResult) {
            OrganizationResult org = (OrganizationResult) contact;
            AvatarHelper.setOrgIconrByOrgId(org.getAvatar(), avatarView, true , true);

        } else {

            showAvatar(contact, contact.getAvatar());
        }
    }

    private void refreshView(Session session) {
        if(FileTransferService.INSTANCE.checkVariation(avatarView, null, session)) {
            return;
        }

        AvatarHelper.setSessionAvatarById(avatarView, session, true, true);
    }

    private void showAvatar(ShowListItem contact, String avatar) {
        if (contact instanceof User) {
            if(FileTransferService.INSTANCE.checkVariation(avatarView, null, (User)contact)) {
                return;
            }
        }


        if (!StringUtils.isEmpty(avatar)) {
            AvatarHelper.setUserAvatarByAvaId(avatar, avatarView, true, true);

        } else {
            AvatarHelper.setUserAvatarById(avatarView, contact.getId(), contact.getDomainId(), true, true);
        }
    }

    public interface RemoveContactListener {

        void removeContact(ShowListItem contact);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }
}
