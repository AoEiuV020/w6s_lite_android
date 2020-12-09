package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.foreverht.workplus.module.chat.activity.BaseMessageHistoryActivity;
import com.foreverht.workplus.module.chat.activity.MessageHistoryActivity;
import com.foreveross.atwork.AtworkApplicationLike;
import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.message.MessageAsyncNetService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.model.app.ServiceApp;
import com.foreveross.atwork.infrastructure.model.user.LoginUserBasic;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.BodyType;
import com.foreveross.atwork.infrastructure.newmessage.messageEnum.ParticipantType;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.ServeEventChatMessage;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.utils.CommonUtil;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.modules.chat.dao.ChatDaoService;
import com.foreveross.atwork.modules.chat.data.SendMessageDataWrap;
import com.foreveross.atwork.modules.chat.service.ChatSendOnMainViewService;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ChatMessageHelper;
import com.w6s.module.MessageTags;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lingen on 15/5/8.
 */
public class ServiceMenuView extends RelativeLayout {

    private LinearLayout menuTotalLine;

    private View menuKeyBoardView;

    private TextView menuOne;

    private TextView menuTwo;

    private TextView menuThree;

    private View menuOneView;

    private View menuTwoView;

    private View menuThreeView;

    private View iconMenuOneView;

    private View iconMenuTwoView;

    private View iconMenuThreeView;


    private ToInputModelListener toInputModelListener;

    private List<ServiceApp.ServiceMenu> menuList;

    private PopupServiceAppView popUpListView;

    private Session session;

    private User user;

    private App app;

    public static final String OPEN_HISTORY_MENU_INDEX = "workplus://openHistory?";


    public ServiceMenuView(Context context) {
        super(context);
        initView();
        initData();
        registerListener();
    }

    public ServiceMenuView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
        initData();
        registerListener();
    }

    private void initData() {

    }

    private void registerListener() {
        menuKeyBoardView.setOnClickListener(v -> {
            if (toInputModelListener != null) {
                toInputModelListener.toInputMode();
            }
        });


        menuOne.setOnClickListener(v -> serviceMenuPop(0, menuOne));

        menuTwo.setOnClickListener(v -> serviceMenuPop(1, menuTwo));

        menuThree.setOnClickListener(v -> serviceMenuPop(2, menuThree));
    }

    private void serviceMenuPop(int location, View view) {

        ServiceApp.ServiceMenu serviceMenu = menuList.get(location);
        int popWidth = getWinPopWidth(serviceMenu.serviceMenus);
        int xOff = getPopWinTextXoff(popWidth, serviceMenu.serviceMenus);

        popUpListView = new PopupServiceAppView(getContext(), popWidth);

        if (serviceMenu.serviceMenus != null && serviceMenu.serviceMenus.size() > 0) {

            popUpListView.setPopItem(serviceMenu.serviceMenus, xOff);
            popUpListView.pop(view, popWidth);

            popUpListView.setServiceMenuListener(new PopupServiceAppView.ServiceMenuListener() {
                @Override
                public void clickEvent(ServiceApp.ServiceMenu serviceMenu) {
                    clickServiceMenu(serviceMenu);
                }

                @Override
                public void viewEvent(ServiceApp.ServiceMenu serviceMenu) {
                    viewServiceMenu(serviceMenu);
                }

                @Override
                public void viewServiceTagEvent(ServiceApp.ServiceMenu serviceMenu) {
                    viewServiceTagMenu(serviceMenu);
                }
            });

        } else {
            if (serviceMenu.type.equals(ServiceApp.ServiceMenuType.Click)) {
                clickServiceMenu(serviceMenu);
            }

            if (serviceMenu.type.equals(ServiceApp.ServiceMenuType.VIEW)) {
                viewServiceMenu(serviceMenu);
            }

            if (serviceMenu.type.equals(ServiceApp.ServiceMenuType.Tag)) {
                viewServiceTagMenu(serviceMenu);
            }
        }
    }

    private void viewServiceMenu(ServiceApp.ServiceMenu serviceMenu) {
        String redirectUrl = serviceMenu.value;
        if (StringUtils.isEmpty(redirectUrl)) {
            AtworkToast.showToast(getContext().getResources().getString(R.string.not_valid_url));
        } else {

            WebViewControlAction webViewControlAction = WebViewControlAction.newAction().setUrl(serviceMenu.value).setTitle(serviceMenu.getNameI18n(AtworkApplicationLike.baseContext));
            Intent intent = WebViewActivity.getIntent(getContext(), webViewControlAction);
            intent.putExtra(WebViewActivity.AUTO_AUTH_CORDOVA, true);
            getContext().startActivity(intent);
        }
    }

    private void viewServiceTagMenu(ServiceApp.ServiceMenu serviceMenu) {
        String redirectUrl = serviceMenu.value;
        if (redirectUrl.toLowerCase().startsWith(OPEN_HISTORY_MENU_INDEX.toLowerCase())) {
            Map<String, String> params = getUriParams(redirectUrl.substring(redirectUrl.lastIndexOf("?")+1, redirectUrl.length()));
            String id = params.get("id");
            ChatDaoService.getInstance().getMessageTagsByTagId(getContext(), id, session, new MessageAsyncNetService.OnMessageTagsListener() {
                @Override
                public void getMessageTagsSuccess(List<MessageTags> tagsList) {
                    App app = new App();
                    app.mOrgId = session.orgId;
                    app.mDomainId = session.mDomainId;
                    app.mAppId = session.identifier;
                    app.mAppName = session.name;
                    BaseMessageHistoryActivity.MessageHistoryViewAction action = new BaseMessageHistoryActivity.MessageHistoryViewAction(app, ListUtil.isEmpty(tagsList) ? null : tagsList.get(0));
                    getContext().startActivity(MessageHistoryActivity.Companion.getIntent(getContext(), action));
                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {

                }
            });


            return;
        }
    }

    private Map<String, String> getUriParams(String uri) {
        Map<String, String> params = new HashMap<>();
        if (TextUtils.isEmpty(uri)) {
            return params;
        }
        String[] splitedParams = uri.split("&");
        for (String splitedKV : splitedParams) {
            String[] kvs = splitedKV.split("=");
            if (kvs != null && kvs.length == 2) {
                params.put(kvs[0], kvs[1] == null ? "" : kvs[1]);
            }
        }
        return params;
    }

    private void clickServiceMenu(ServiceApp.ServiceMenu serviceMenu) {
        if(CommonUtil.isFastClick(300)) {
            return;
        }

        LoginUserBasic meUser = LoginUserInfo.getInstance().getLoginUserBasic(getContext());
        ServeEventChatMessage eventChatMessage = ServeEventChatMessage.newEventChatMessage(serviceMenu.value, BodyType.Event, meUser.mUserId, session.identifier, ParticipantType.User, ParticipantType.App, meUser.mDomainId, session.mDomainId, app.mOrgId, user.mAvatar, user.getShowName(), app.mAvatar, app.mAppName);

        //发送消息
        SendMessageDataWrap.getInstance().addChatSendingMessage(eventChatMessage);
        ChatMessageHelper.sendMessage(eventChatMessage);

        ChatSendOnMainViewService.ackCheckIMTimeOut(eventChatMessage);

    }

    private void initView() {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.chat_detail_service_menu, this);
        menuKeyBoardView = view.findViewById(R.id.service_detail_input_keyboard_service_view);
        menuTotalLine = view.findViewById(R.id.service_menu_line);
        menuOne = view.findViewById(R.id.service_menu_one);
        menuTwo = view.findViewById(R.id.service_menu_two);
        menuThree = view.findViewById(R.id.service_menu_three);
        menuOneView = view.findViewById(R.id.service_menu_one_layout);
        menuTwoView = view.findViewById(R.id.service_menu_two_layout);
        menuThreeView = view.findViewById(R.id.service_menu_three_layout);
        menuOneView.setVisibility(GONE);
        menuTwoView.setVisibility(GONE);
        menuThreeView.setVisibility(GONE);

        iconMenuOneView = view.findViewById(R.id.service_menu_one_icon);
        iconMenuTwoView = view.findViewById(R.id.service_menu_two_icon);
        iconMenuThreeView = view.findViewById(R.id.service_menu_three_icon);
    }

    public void setToInputModelListener(ToInputModelListener toInputModelListener) {
        this.toInputModelListener = toInputModelListener;
    }

    public void refreshMenu(List<ServiceApp.ServiceMenu> menuList) {
        this.menuList = menuList;
        if (menuList.size() > 0) {
            oneMenu(menuList.get(0));
        }

        if (menuList.size() > 1) {
            twoMenu(menuList.get(1));
        }

        if (menuList.size() > 2) {
            threeMenu(menuList.get(2));
        }
    }

    private void oneMenu(ServiceApp.ServiceMenu serviceMenu) {
        menuOneView.setVisibility(VISIBLE);
        menuOne.setText(serviceMenu.getNameI18n(BaseApplicationLike.baseContext));
        if(serviceMenu.serviceMenus==null || serviceMenu.serviceMenus.size()==0){
            iconMenuOneView.setVisibility(GONE);
        }
    }

    private void twoMenu(ServiceApp.ServiceMenu serviceMenu) {
        menuTwoView.setVisibility(VISIBLE);
        menuTwo.setText(serviceMenu.getNameI18n(BaseApplicationLike.baseContext));
        if(serviceMenu.serviceMenus==null || serviceMenu.serviceMenus.size()==0){
            iconMenuTwoView.setVisibility(GONE);
        }
    }

    private void threeMenu(ServiceApp.ServiceMenu serviceMenu) {
        menuThreeView.setVisibility(VISIBLE);
        menuThree.setText(serviceMenu.getNameI18n(BaseApplicationLike.baseContext));
        if(serviceMenu.serviceMenus==null || serviceMenu.serviceMenus.size()==0){
            iconMenuThreeView.setVisibility(GONE);
        }
    }

    public void hideMenuKeyBoardView() {
        menuKeyBoardView.setVisibility(View.GONE);
    }

    public void showMenuKeyBoardView() {
        menuKeyBoardView.setVisibility(View.VISIBLE);
    }

    /**
     * 弹出菜单默认使用底部菜单栏 1/3 的长度(6个字以内)
     * */
    private int getWinPopWidth( List<ServiceApp.ServiceMenu> serviceMenusItems){
        int width = (menuTotalLine.getMeasuredWidth()) / 3;
        int maxFontSize = getMaxSizeName(serviceMenusItems).length();

        if(2 < maxFontSize){
            int textSize = DensityUtil.sp2px(16);
            int needSize = textSize * 8 + 20; //20 is paddingLeft + paddingRight

            width *= 1.3;
            if(width < needSize) {
                width = needSize;
            }
        }

        return width;
    }

    private String getMaxSizeName(List<ServiceApp.ServiceMenu> serviceMenusItems){
        String maxName = "";
        if(serviceMenusItems!=null){
            for(ServiceApp.ServiceMenu item : serviceMenusItems){
                String itemNameI18n = item.getNameI18n(BaseApplicationLike.baseContext);
                if(maxName.length() < itemNameI18n.length()){
                    maxName = itemNameI18n;
                }
            }
        }
        return maxName;
    }

    /**
     *获得子菜单应该偏移的值
     * @param winPopWidth
     * @param serviceMenusItems
     * @return off
     *       若返回-1, 则不需要偏移, 以居中显示
     * */
    private int getPopWinTextXoff(int winPopWidth, List<ServiceApp.ServiceMenu> serviceMenusItems){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.component_text_item, this);
        TextView tv = view.findViewById(R.id.list_view_item_text);
        int xOff = -1;

        String maxName = getMaxSizeName(serviceMenusItems);
        if(6 < maxName.length()){
            Rect bounds = new Rect();
            Paint textPaint = tv.getPaint();
            textPaint.getTextBounds(maxName, 0, maxName.length(), bounds);
            xOff = (winPopWidth - bounds.width()) / 2;
        }

        return xOff;
    }

    public void setSession(Session session) {
        this.session = session;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public void setApp(App app) {
        this.app = app;
    }



    public interface ToInputModelListener {
        void toInputMode();
    }

}
