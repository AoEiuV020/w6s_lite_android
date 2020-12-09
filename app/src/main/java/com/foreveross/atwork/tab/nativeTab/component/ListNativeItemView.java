package com.foreveross.atwork.tab.nativeTab.component;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.api.sdk.util.LightNoticeHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.beeworks.NativeItem;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.common.component.LightNoticeItemView;
import com.foreveross.atwork.modules.common.lightapp.LightNoticeMapping;
import com.foreveross.atwork.modules.common.lightapp.SimpleLightNoticeMapping;
import com.foreveross.atwork.modules.main.data.TabNoticeManager;
import com.foreveross.atwork.modules.qrcode.activity.QrcodeScanActivity;
import com.foreveross.atwork.modules.voip.utils.VoipHelper;
import com.foreveross.atwork.tab.helper.BeeworksTabHelper;
import com.foreveross.atwork.utils.AtworkToast;

/**
 * Created by lingen on 15/12/23.
 */
public class ListNativeItemView extends RelativeLayout {

    public ImageView iconView;
    public TextView titleView;

    public LightNoticeItemView mNoticeView;

    private NativeItem nativeItem;

    public ListNativeItemView(Context context) {
        super(context);
        initView();
        registerListener();
    }

    private void registerListener() {
        setOnClickListener(v -> {
            if ("url".equalsIgnoreCase(nativeItem.mActionType)) {
                WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(nativeItem.mValue).setTitle(nativeItem.mTitle);
                Intent webViewActivity = WebViewActivity.getIntent(getContext(), webViewControlAction);
                getContext().startActivity(webViewActivity);
                
            } else if ("view".equalsIgnoreCase(nativeItem.mActionType)) {

                if ("QRCodeView".equalsIgnoreCase(nativeItem.mValue)) {
                    if(VoipHelper.isHandlingVoipCall()) {
                        AtworkToast.showResToast(R.string.alert_is_handling_voip_meeting_click_voip);
                        return;
                    }

                    Intent activity = QrcodeScanActivity.getIntent(getContext());
                    getContext().startActivity(activity);
                }
            }

        });
    }

    public void refreshView(String tabId,NativeItem nativeItem, final LightNoticeItemView noticeItemView) {
        this.nativeItem = nativeItem;
        BeeworksTabHelper.getInstance().setBeeText(titleView, nativeItem.mTitle, nativeItem.mFontColor);

        //注册红点机制
        if (!StringUtils.isEmpty(nativeItem.mTipUrl)){

            final LightNoticeMapping lightNoticeModel =  SimpleLightNoticeMapping.createInstance(nativeItem.mTipUrl, tabId, NativeItem.class.getSimpleName() + nativeItem.mTitle + nativeItem.mTipUrl);
            TabNoticeManager.getInstance().registerLightNoticeMapping(lightNoticeModel);

            LightNoticeHelper.loadLightNotice(lightNoticeModel.getNoticeUrl(), BaseApplicationLike.baseContext, new LightNoticeHelper.LightNoticeListener() {
                @Override
                public void success(LightNoticeData lightNoticeJson) {
                    TabNoticeManager.getInstance().update(lightNoticeModel, lightNoticeJson);
                    noticeItemView.refreshLightNotice(lightNoticeJson);
                }

                @Override
                public void fail() {

                }
            });
        }
        BeeworksTabHelper.getInstance().setBeeImage(iconView, nativeItem.mIcon, 1);

    }


    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_beeworks_list_item, this);
        titleView = view.findViewById(R.id.me_function_name);
        iconView = view.findViewById(R.id.about_me_function_icon);
        mNoticeView = view.findViewById(R.id.me_notice_view);
        view.findViewById(R.id.me_switcher_button).setVisibility(GONE);
    }


}
