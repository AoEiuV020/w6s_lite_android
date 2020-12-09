package com.foreveross.atwork.modules.contact.component;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IntegerRes;
import androidx.annotation.RawRes;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.modules.chat.fragment.ChatInfoFragment;
import com.foreveross.atwork.modules.contact.util.ContactInfoViewUtil;

/**
 * Created by lingen on 15/4/27.
 * Description:
 * 群详情中的CONTACT信息
 */
public class ChatInfoContactItemView extends RelativeLayout {


    private ImageView mIvAvatar;

    private TextView mTvName;

    private ShowListItem mShowContact;

    private View mViewRemove;

    private ChatInfoFragment.AddOrRemoveListener addOrRemoveListener;

    public ChatInfoContactItemView(Context context) {
        super(context);
        initView();
        registerListener();
    }

    private void registerListener() {
        mViewRemove.setOnClickListener(v -> {
            if (CommonUtil.isFastClick(3000)) {
                return;
            }
            if (null != addOrRemoveListener) {
                addOrRemoveListener.removeUser(mShowContact);
            }
        });
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_info_user_list, this);
        mIvAvatar = view.findViewById(R.id.user_list_avatar);
        mTvName = view.findViewById(R.id.user_list_name);
        mViewRemove = view.findViewById(R.id.user_remove);
    }

    public void refreshView(ShowListItem contact, boolean remove) {
        mShowContact = contact;
        if (remove) {
            mViewRemove.setVisibility(VISIBLE);
        } else {
            mViewRemove.setVisibility(GONE);
        }

        ContactInfoViewUtil.dealWithContactInitializedStatus(mIvAvatar, mTvName, mShowContact, true, true);

        mTvName.setVisibility(VISIBLE);

    }

    public void refreshLocal(@DrawableRes int iconId) {
        mIvAvatar.setImageResource(iconId);
        mTvName.setVisibility(INVISIBLE);
        mViewRemove.setVisibility(GONE);

    }

    public ChatInfoFragment.AddOrRemoveListener getAddOrRemoveListener() {
        return addOrRemoveListener;
    }

    public void setAddOrRemoveListener(ChatInfoFragment.AddOrRemoveListener addOrRemoveListener) {
        this.addOrRemoveListener = addOrRemoveListener;
    }

}
