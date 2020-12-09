package com.foreveross.atwork.modules.search.component;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.AppBundles;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.orgization.Department;
import com.foreveross.atwork.infrastructure.model.orgization.Organization;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.manager.OrganizationManager;
import com.foreveross.atwork.modules.contact.util.ContactInfoViewUtil;
import com.foreveross.atwork.modules.file.service.FileTransferService;
import com.foreveross.atwork.modules.search.model.SearchAction;
import com.foreveross.atwork.modules.search.model.SearchMessageCompatItem;
import com.foreveross.atwork.modules.search.model.SearchMessageItem;
import com.foreveross.atwork.modules.search.util.SearchHelper;
import com.foreveross.atwork.utils.AvatarHelper;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.TimeViewUtil;
import com.foreveross.atwork.utils.WorkplusTextSizeChangeHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;


public class SearchListItemView extends RelativeLayout {
    private View mVRoot;
    //头像
    private ImageView mAvatarView;
    private TextView mTitleView;
    private TextView mInfoView;
    private ImageView mSelectView;
    private ImageView mIvArrow;
    private ImageView mIvBioAuthProtected;
    private ImageView mIvLineChatSearch;

    private TextView mTvTime;

    private ShowListItem mSearchItem;

    private String mKey;

    private SearchAction mSearchAction;

    private boolean mNeedSelectStatus = false;

    /**是否显示在智能机器人界面*/
    private boolean isRobot = false;

    public SearchListItemView(Context context) {
        super(context);
        initView();
//        registerListener();
        WorkplusTextSizeChangeHelper.handleHeightEnlargedTextSizeStatus(mVRoot);
    }

    public SearchListItemView(Context context, SearchAction searchAction) {
        super(context);
        initView();
//        registerListener();
        WorkplusTextSizeChangeHelper.handleHeightEnlargedTextSizeStatus(mVRoot);
        this.mSearchAction = searchAction;

    }

    public SearchListItemView(Context context, SearchAction searchAction,boolean isRobot) {
        super(context);
        this.isRobot = isRobot;
        initView();
//        registerListener();
        WorkplusTextSizeChangeHelper.handleHeightEnlargedTextSizeStatus(mVRoot);
        this.mSearchAction = searchAction;
    }

    public SearchListItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        WorkplusTextSizeChangeHelper.handleHeightEnlargedTextSizeStatus(mVRoot);
//        registerListener();
    }

    private void registerListener() {

        setOnClickListener(v -> {
            Context context = getContext();
            if (context instanceof Activity) {
                SearchHelper.handleSearchResultCommonClick(((Activity) context), "", mSearchItem, mSearchAction, null);
            }

        });


    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_contact_list_item, this);
        mVRoot = view.findViewById(R.id.rl_root);
        mAvatarView = view.findViewById(R.id.contact_list_item_avatar);
        mTitleView = view.findViewById(R.id.contact_list_item_title);
        mInfoView = view.findViewById(R.id.contact_list_item_info);
        mSelectView = view.findViewById(R.id.chat_list_select);
        mIvArrow = view.findViewById(R.id.iv_arrow);
        mIvBioAuthProtected = view.findViewById(R.id.iv_bio_auth_protected);
        mIvLineChatSearch = view.findViewById(R.id.iv_line_chat_search);
        mTvTime = view.findViewById(R.id.tv_time);
        view.findViewById(R.id.contact_list_item_job).setVisibility(GONE);

        if(isRobot){
            showInRobot();
        }

    }

    private void showInRobot(){
        mVRoot.setBackgroundColor(getResources().getColor(R.color.black));
        mTitleView.setTextColor(getResources().getColor(R.color.white));
        mIvArrow.setImageResource(R.mipmap.icon_arrow_white);
        mIvLineChatSearch.setVisibility(GONE);
    }

    public void hideLine(boolean hideLine) {
        mIvLineChatSearch.setVisibility(hideLine || isRobot ? GONE : VISIBLE);
    }

    public void setNeedSelectStatus(boolean needSelectStatus) {
        mNeedSelectStatus = needSelectStatus;
    }

    @SuppressLint("StringFormatMatches")
    public void refresh() {
        boolean needHighlightInfoView = true;

        if(mNeedSelectStatus) {

            if(mSearchItem.isSelect()) {
                mSelectView.setImageResource(R.mipmap.icon_selected);

            } else {
                mSelectView.setImageResource(R.mipmap.icon_seclect_no_circular);

            }

            mSelectView.setVisibility(VISIBLE);

        } else {
            mSelectView.setVisibility(GONE);

        }

        if (mSearchItem instanceof Discussion) {
            revertAvatarView();

            Discussion discussion = (Discussion) mSearchItem;
            mAvatarView.setImageResource(R.mipmap.default_discussion_chat);

            mTitleView.setText(mSearchItem.getTitle());
            AvatarHelper.setDiscussionAvatarById(mAvatarView, discussion.mDiscussionId, true, true);
            if (discussion.mMemberList.isEmpty()) {
                mInfoView.setVisibility(GONE);
            } else {
                mInfoView.setVisibility(VISIBLE);
                mInfoView.setText(getContext().getString(R.string.contain) + discussion.mMemberList.get(0).name);
            }


        } else if (mSearchItem instanceof SearchMessageItem) {
            revertAvatarView();

            SearchMessageItem searchMessageItem = (SearchMessageItem) mSearchItem;
            ChatPostMessage message = searchMessageItem.message;
            AvatarHelper.setUserAvatarById(mAvatarView, message.from, message.mFromDomain, true, true);
            mTitleView.setText(message.mMyName);
            mInfoView.setText(mSearchItem.getInfo());
            mInfoView.setVisibility(VISIBLE);

            mTvTime.setText(TimeViewUtil.getSimpleUserCanViewTime(getContext() , message.deliveryTime));


        } else if (mSearchItem instanceof SearchMessageCompatItem) {
            revertAvatarView();

            SearchMessageCompatItem searchMessageItem = (SearchMessageCompatItem) mSearchItem;
            Session session = searchMessageItem.session;
            ContactInfoViewUtil.dealWithSessionInitializedStatus(mAvatarView, mTitleView, session, true);

            mInfoView.setText(mSearchItem.getInfo());
            mInfoView.setVisibility(VISIBLE);

        } else if (mSearchItem instanceof User) {

            if (FileTransferService.INSTANCE.checkVariation(mAvatarView, mTitleView, (User) mSearchItem)) {
                revertAvatarView();
                mInfoView.setVisibility(GONE);

            } else {
                refreshUserTypeView();
            }


        } else if(mSearchItem instanceof Employee) {
            refreshUserTypeView();

        } else if(mSearchItem instanceof AppBundles){
            revertAvatarView();
            AvatarHelper.setAppAvatarByAvaId(mAvatarView, mSearchItem.getAvatar(), true, true);

            mTitleView.setText(mSearchItem.getTitleI18n(BaseApplicationLike.baseContext));
            mInfoView.setVisibility(GONE);

        } else if (mSearchItem instanceof Department) {
            String orgCode = mSearchItem.getAvatar();
            Organization organization = OrganizationManager.getInstance().getOrganizationSyncByOrgCode(getContext(), orgCode);
            if (null != organization && !StringUtils.isEmpty(organization.mLogo)) {
                ImageCacheHelper.displayImageByMediaId(organization.mLogo, mAvatarView, ImageCacheHelper.getRoundOptions(R.mipmap.icon_org));
            } else {
                ImageCacheHelper.setImageResource(mAvatarView, R.mipmap.icon_org);
            }
            mInfoView.setVisibility(VISIBLE);
            mTitleView.setText(mSearchItem.getTitle());
            mInfoView.setText(mSearchItem.getInfo());
        } else {
            revertAvatarView();

            ImageCacheHelper.displayImageByMediaId(mSearchItem.getAvatar(), mAvatarView, ImageCacheHelper.getRoundOptions(-1));
            mTitleView.setText(mSearchItem.getTitle());
            mInfoView.setVisibility(GONE);

        }

        handleArrow();

        if(!(mSearchItem instanceof SearchMessageItem)) {
            mTvTime.setVisibility(GONE);
        } else {
            mTvTime.setVisibility(VISIBLE);

        }

        highlightKey(mTitleView);

        if (needHighlightInfoView) {
            highlightKey(mInfoView);
        }

        if(isRobot){
            showInRobot();
        }
    }

    private void refreshUserTypeView() {
        ContactInfoViewUtil.dealWithContactInitializedStatus(mAvatarView, mTitleView, mSearchItem, false, true);

        //当是 user 时, 或者 info 为空时,  需要居中隐藏处理, 以达到居中
        if (!AtworkConfig.EMPLOYEE_CONFIG.getShowPeerJobTitle() || mSearchItem instanceof User || StringUtils.isEmpty(mSearchItem.getInfo())) {
            mInfoView.setVisibility(GONE);

        } else {
            mInfoView.setText(mSearchItem.getInfo());
            mInfoView.setVisibility(VISIBLE);
        }
    }

    private void handleArrow() {
        if(mSearchItem instanceof App) {
            mIvArrow.setVisibility(VISIBLE);
        } else {
            mIvArrow.setVisibility(GONE);

        }
    }

    /**
     * 在线离线会制灰view, 该方法用于恢复 view 的状态
     */
    private void revertAvatarView() {
        if (DomainSettingsManager.getInstance().handleUserOnlineFeature()) {
            mAvatarView.clearColorFilter();
        }
    }

    private DisplayImageOptions getBingDisplayImageOptions() {
        return ImageCacheHelper.getRoundOptions(R.mipmap.icon_normal_bing, R.mipmap.icon_normal_bing);
    }


    private void highlightKey(TextView textView) {

        int color = getContext().getResources().getColor(R.color.blue_lock);

        String text = textView.getText().toString();
        if (!StringUtils.isEmpty(text)) {
            int start = text.toLowerCase().indexOf(mKey.toLowerCase());
            int end = -1;
            if (start != -1) {
                end = start + mKey.length();
                SpannableString spannableString = new SpannableString(text);
                spannableString.setSpan(new ForegroundColorSpan(color), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                textView.setText(spannableString);
            }
        }
    }


    public void refreshView(ShowListItem searchItem, String key) {
        this.mKey = key;

        refreshView(searchItem);
    }

    public void refreshView(ShowListItem searchItem) {
        this.mSearchItem = searchItem;
        refresh();
    }
}
