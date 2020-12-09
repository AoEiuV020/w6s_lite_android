package com.foreveross.atwork.modules.contact.component;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.UrlConstantManager;
import com.foreveross.atwork.api.sdk.organization.responseModel.OrgApplyingCheckResponseJson;
import com.foreveross.atwork.component.NewMessageView;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ViewUtil;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.group.activity.UserSelectActivity;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.theme.manager.SkinHelper;
import com.foreveross.theme.manager.SkinMaster;

public class ContactHeadView extends RelativeLayout {

    private RelativeLayout mRlRoot;

    private ImageView mContactListHeadAvatar;

    private TextView mContactListHeadTitle;

    private ImageView mContactSelect;

    private ImageView mContactArrow;

    private TextView mBtnStatus;

    private NewMessageView mVUnreadTip;

    private boolean open;

    private Context mContext;

    private View mVTopBlankView;

    private View mVBottomLine;

    private boolean mIsGrayColor = false;

    private int mBtnTextResId = -1;

    private Organization mOrg;

    private boolean mNoNeedArrow = false;

    //todo 使用工厂模式创建
    /**
     * contactFragment "我的群组" "我的好友"等头部
     * */
    public static ContactHeadView getInstance(Context context, int imageResId, String title) {
        ContactHeadView contactHeadView = new ContactHeadView(context);
        contactHeadView.refreshCommonView(imageResId, title);
        return contactHeadView;
    }

    /**
     * 组织头部
     * */
    public static ContactHeadView getInstance(Context context, UserSelectActivity.SelectMode selectModel, Organization organization) {
        ContactHeadView contactHeadView = new ContactHeadView(context);
        contactHeadView.refreshOrgView(organization.mLogo, organization.getNameI18n(context), selectModel, organization);
        contactHeadView.mOrg = organization;
        return contactHeadView;
    }

    /**
     * 组织申请列表的头部
     * */
    public static ContactHeadView getInstance(Context context, OrgApplyingCheckResponseJson.ApplyingOrg applyingOrg) {
        ContactHeadView contactHeadView = new ContactHeadView(context);
        contactHeadView.refreshOrgApplyView(applyingOrg.orgLogo, applyingOrg.orgName);
        return contactHeadView;
    }

    private ContactHeadView(Context context) {
        super(context);
        mContext = context;
        initView();
    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_contact_item_head, this);
        mRlRoot = view.findViewById(R.id.rl_root);
        mContactListHeadAvatar = view.findViewById(R.id.contact_list_head_avatar);
        mContactListHeadTitle = view.findViewById(R.id.contact_list_head_title);
        mContactSelect = view.findViewById(R.id.contact_list_select);
        mVUnreadTip = view.findViewById(R.id.v_unread_tip);
        mContactArrow = view.findViewById(R.id.contact_list_head_more);
        mBtnStatus = view.findViewById(R.id.btn_status);
        mVTopBlankView = view.findViewById(R.id.item_topview);
        mVBottomLine = view.findViewById(R.id.iv_line_chat_contact);
        SkinMaster.getInstance().changeTheme((ViewGroup) view);
    }

    private void refreshOrgApplyView(String logoId, String title) {
        mIsGrayColor = true;

        ImageCacheHelper.displayImageByMediaId(logoId, mContactListHeadAvatar, ImageCacheHelper.getOrgLogoOptions());
        refreshView(title);

        mBtnTextResId = R.string.applying;
        mBtnStatus.setText(R.string.applying);
        mBtnStatus.setBackgroundResource(R.drawable.shape_common_gray);
        mBtnStatus.setVisibility(VISIBLE);

        mNoNeedArrow = true;
        hideArrow();
    }

    private void refreshOrgView(String logoId, String title, UserSelectActivity.SelectMode selectMode, final Organization organization) {
        ImageCacheHelper.displayImageByMediaId(logoId, mContactListHeadAvatar, ImageCacheHelper.getOrgLogoOptions());
        refreshView(title);
        refreshOrgView(selectMode, organization);
    }

    public void showArrow() {
        if(mNoNeedArrow) {
            return;
        }


        mContactArrow.setVisibility(VISIBLE);
    }

    public void hideArrow() {
        mContactArrow.setVisibility(GONE);
    }

    public void showSelect() {
        mContactSelect.setVisibility(VISIBLE);
    }

    public void hideSelect() {
        mContactSelect.setVisibility(GONE);
    }

    public void select() {
        mContactSelect.setImageResource(R.mipmap.icon_selected);
    }

    public void unselect() {
        mContactSelect.setImageResource(R.mipmap.icon_seclect_no_circular);

    }

    public void refreshBtnText() {
        if(-1 != mBtnTextResId) {
            mBtnStatus.setText(mBtnTextResId);
        }
    }


    private void refreshCommonView(int imageResId, String title) {

        if (-1 == imageResId) {
            mContactListHeadAvatar.setVisibility(GONE);
        } else {
            mContactListHeadAvatar.setImageResource(imageResId);
        }

        refreshView(title);
    }

    private void refreshView(String title) {
        mBtnStatus.setVisibility(View.GONE);

        mContactListHeadTitle.setText(title);

        refreshStatusBtnColor();
    }

    public Organization getOrg() {
        return mOrg;
    }

    public void refreshStatusBtnColor() {
        if(!mIsGrayColor) {
            SkinMaster.getInstance().changeBackground(mBtnStatus.getBackground(), SkinHelper.parseColorFromTag("c13"));
        }
    }

    private void refreshOrgView(UserSelectActivity.SelectMode selectMode, Organization organization) {
        if (organization != null) {

            if (selectMode.equals(UserSelectActivity.SelectMode.NO_SELECT) && organization.isMeAdmin(getContext())) {
                mBtnTextResId = R.string.label_invite;
                mBtnStatus.setVisibility(VISIBLE);

                mNoNeedArrow = true;
                hideArrow();

                mBtnStatus.setOnClickListener(v -> {
//                    String url = String.format(UrlConstantManager.getInstance().getOrgManagerUrl(), organization.mOrgCode);
                    String url = String.format(UrlConstantManager.getInstance().getOrgQrcodeUrl(), organization.mOrgCode, Uri.encode(getContext().getString(R.string.app_name)), false);
                    WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(url).setNeedShare(false);
                    Intent intent = WebViewActivity.getIntent(mContext, webViewControlAction);
                    mContext.startActivity(intent);
                });
            }

        }
    }

    private void setColor(int bgColor) {
        setBackgroundColor(bgColor);
    }

    public boolean isOpen() {
        return open;
    }

    public void open() {
        open = true;
    }

    public void close() {
        open = false;
    }

    public void setUnreadNum(int num) {
        mVUnreadTip.setNum(num);
    }

    public void shrinkArrow(Context context) {
        int length = DensityUtil.dip2px(20);
        mContactListHeadAvatar.getLayoutParams().height = length;
        mContactListHeadAvatar.getLayoutParams().width = length;
        mContactListHeadAvatar.requestLayout();
    }

    public void setBottomDividerVisible(boolean visible) {
        ViewUtil.setVisible(mVBottomLine, visible);
    }

    public void setTopBlankViewVisible(boolean visible) {
        ViewUtil.setVisible(mVTopBlankView, visible);
    }

    public boolean isGrayColor() {
        return mIsGrayColor;
    }


    public TextView getBtnStatus() {
        return mBtnStatus;
    }


    public ImageView getContactListHeadAvatar() {
        return mContactListHeadAvatar;
    }

    public RelativeLayout getRlRoot() {
        return mRlRoot;
    }
}
