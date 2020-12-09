package com.foreveross.atwork.modules.dropbox.component;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.dropbox.Dropbox;
import com.foreveross.atwork.infrastructure.model.dropbox.DropboxConfig;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ScreenUtils;
import com.foreveross.atwork.manager.DropboxConfigManager;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.modules.dropbox.activity.DropboxRWSettingActivity;
import com.foreveross.atwork.modules.dropbox.loader.OrgDropboxLoader;
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 16/9/8.
 */
public class DropboxOrgChileItemView extends RelativeLayout {

    private Context mContext;

    private View mVRoot;
    private ImageView mAvatar;
    private TextView mFileName;
    private TextView mFileSize;
    private TextView mSetRWBtn;
    private TextView mReadOnlyTip;
    private TextView mReadonlyMsg;
    private View mLastLine;
    private View mNotLastLine;


    public DropboxOrgChileItemView(Context context) {
        super(context);
        mContext = context;
        initViews(context);
        WorkplusTextSizeChangeHelper.handleHeightEnlargedTextSizeStatus(mVRoot);
    }


    private void initViews(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_dropbox_org_child, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mAvatar = view.findViewById(R.id.org_space_avatar);
        mFileName = view.findViewById(R.id.file_name);
        mFileSize = view.findViewById(R.id.file_size);
        mSetRWBtn = view.findViewById(R.id.set_wr_btn);
        mReadOnlyTip = view.findViewById(R.id.read_only_tip);
        mReadonlyMsg = view.findViewById(R.id.only_edit_by_admin_text);
        mLastLine = view.findViewById(R.id.last_line);
        mNotLastLine = view.findViewById(R.id.not_last_line);
    }

    /**
     *
     * @param discussion
     */
    public void setDiscussion(Discussion discussion, boolean moveOrCopy, boolean isLast) {
        mAvatar.setImageResource(OrgDropboxLoader.PUBLIC_ID.equalsIgnoreCase(discussion.mDiscussionId) ? R.mipmap.icon_public_area : R.mipmap.icon_dropbox_discussion);
        mFileName.setText(discussion.mName);
        Organization organization = OrganizationManager.getInstance().getOrganizationSyncByOrgCode(mContext, discussion.getOrgCodeCompatible());

        if (OrgDropboxLoader.PUBLIC_ID.equalsIgnoreCase(discussion.mDiscussionId)) {
            boolean isAdmin = OrganizationManager.getInstance().isLoginAdminOrgSync(BaseApplicationLike.baseContext, discussion.getOrgCodeCompatible());
            DropboxConfig dropboxConfig = DropboxConfigManager.getInstance().syncGetDropboxConfigBySourceId(mContext, discussion.getOrgCodeCompatible());
            setWRSettingView(isAdmin && !moveOrCopy);
            setWRTipView(dropboxConfig.mReadOnly);
            mSetRWBtn.setOnClickListener(view -> {
                Intent intent = DropboxRWSettingActivity.getIntent(mContext, discussion.getOrgCodeCompatible(), organization.mDomainId, Dropbox.SourceType.Organization, dropboxConfig.mReadOnly);
                mContext.startActivity(intent);
            });
        } else {
            DropboxConfig dropboxConfig = DropboxConfigManager.getInstance().syncGetDropboxConfigBySourceId(mContext, discussion.mDiscussionId);
            boolean isOwnerDiscussion = discussion.isYouOwner(BaseApplicationLike.baseContext);

            setWRSettingView(isOwnerDiscussion && !moveOrCopy);
            setWRTipView(dropboxConfig.mReadOnly);
            mSetRWBtn.setOnClickListener(view -> {
                Intent intent = DropboxRWSettingActivity.getIntent(mContext, discussion.mDiscussionId, organization.mDomainId, Dropbox.SourceType.Discussion, dropboxConfig.mReadOnly);
                mContext.startActivity(intent);
            });
        }
        setLine(isLast);

//        setNameMaxWidth();
    }

    private void setNameMaxWidth() {
        int avatarWidth = mAvatar.getWidth();
        int labelWidth = mReadOnlyTip.getWidth();
        int permissionWidth = mSetRWBtn.getWidth();

        int nameViewMaxWidth = ScreenUtils.getScreenWidth(getContext()) - avatarWidth - labelWidth - permissionWidth - DensityUtil.dip2px(28 + 8 + 5 + 10);
        mFileName.setMaxWidth(nameViewMaxWidth);

    }

    public void setWRSettingView(boolean isAdmin) {
        mSetRWBtn.setVisibility(isAdmin ? VISIBLE : GONE);
    }

    public void setWRTipView(boolean readOnly) {
        mReadOnlyTip.setVisibility(readOnly ? VISIBLE : GONE);
        mReadonlyMsg.setVisibility(readOnly ? VISIBLE : GONE);
    }

    private void setLine(boolean isLast) {
        mLastLine.setVisibility(isLast ? VISIBLE : GONE);
        mNotLastLine.setVisibility(isLast ? GONE : VISIBLE);
    }


}
