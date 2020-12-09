package com.foreveross.atwork.tab.helper;/**
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
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 *                       __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 *                            |__|
 */


import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.beeworks.BeeWorksNetService;
import com.foreveross.atwork.component.beeworks.BeeWorksGridView;
import com.foreveross.atwork.component.beeworks.BeeWorksImageSwitcher;
import com.foreveross.atwork.component.beeworks.BeeWorksWebview;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorks;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksGrid;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksImages;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksTab;
import com.foreveross.atwork.infrastructure.beeworks.BeeWorksWebViewItem;
import com.foreveross.atwork.infrastructure.beeworks.NativeContent;
import com.foreveross.atwork.infrastructure.beeworks.NativeItem;
import com.foreveross.atwork.infrastructure.beeworks.NativeJson;
import com.foreveross.atwork.infrastructure.model.WebViewControlAction;
import com.foreveross.atwork.infrastructure.utils.DensityUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.modules.app.activity.WebViewActivity;
import com.foreveross.atwork.tab.h5tab.H5WebViewFragment;
import com.foreveross.atwork.tab.h5tab.model.H5Json;
import com.foreveross.atwork.tab.nativeTab.component.ListGroupView;
import com.foreveross.atwork.utils.AtworkToast;
import com.foreveross.atwork.utils.ImageCacheHelper;
import com.foreveross.atwork.utils.ImageViewUtil;
import com.google.gson.Gson;

import org.apache.cordova.CordovaWebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求相关tab下的数据处理类
 * Created by reyzhang22 on 16/5/16.
 */
public class BeeworksTabHelper {

    private static final String TAG = BeeworksTabHelper.class.getSimpleName();

    private static BeeworksTabHelper sInstance = new BeeworksTabHelper();

    public Map<String, List<View>> mRefreshMap = new HashMap<>();

    private Activity mActivity;


    public static BeeworksTabHelper getInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new BeeworksTabHelper();
            }
            return sInstance;
        }
    }

    public void getSystemNavigation(final Activity activity, BeeWorksTab beeWorksTab) {
        if (null == beeWorksTab || activity == null || TextUtils.isEmpty(beeWorksTab.url)) {
            return;
        }
        if (beeWorksTab.mNativeJson != null) {
//            ((MainActivity)activity).onNavigationUpdate(beeWorksTab, true);
            return;
        }
        BeeWorksNetService.getInstance().queryDefinitionViews(beeWorksTab.url, new BeeWorksNetService.BeeWorksDefinitionViewsListener() {
            @Override
            public void success(String data) {
                NativeJson nativeJson = NativeJson.createInstanceFromString(data);
                if (beeWorksTab != null) {
                    beeWorksTab.mNativeJson = nativeJson;
                }
//                ((MainActivity)activity).onNavigationUpdate(beeWorksTab, true);
            }

            @Override
            public void fail() {
                AtworkToast.showToast("获取数据失败: url = " + beeWorksTab.url);
            }
        });
    }

    /**
     * 获取自定义控件
     * @param activity
     * @param linearLayout
     */
    public void getNativeDefineView(Activity activity, LinearLayout linearLayout, BeeWorksTab beeWorksTab) {
        if (beeWorksTab == null) {
            Logger.e(TAG, "beeworksTab is null");
            return;
        }
        mActivity = activity;
        getDefinitionViews(linearLayout, beeWorksTab);
    }

    /**
     * 获取H5页面
     * @param webView
     */
    public void getH5DefineView(Activity context, CordovaWebView webView, final H5WebViewFragment fragment, final BeeWorksTab beeWorksTab) {
        if (beeWorksTab == null) {
            return;
        }

        if (TextUtils.isEmpty(beeWorksTab.url)) {
            return;
        }
        BeeWorksNetService.getInstance().queryDefinitionViews(beeWorksTab.url, new BeeWorksNetService.BeeWorksDefinitionViewsListener() {
            @Override
            public void success(String data) {
                H5Json h5Json = new Gson().fromJson(data, H5Json.class);
                NativeJson nativeJson =  new NativeJson();
                nativeJson.mNavigation = h5Json.mNavigation;
                nativeJson.mShowNavi = h5Json.mShowNavigation;
                beeWorksTab.mNativeJson = nativeJson;
//                fragment.mLoadUrl = h5Json.mUrl;
                webView.loadUrl(h5Json.mUrl.startsWith("http://") ? h5Json.mUrl : "http://" + h5Json.mUrl);
//                ((MainActivity)context).onNavigationUpdate(beeWorksTab, false);
            }

            @Override
            public void fail() {
                AtworkToast.showToast("网络请求失败，请稍后重试");
            }
        });

    }

    public void refreshMap(String tabId) {
        List<View> refreshList = mRefreshMap.get(tabId);
        if (refreshList == null) {
            return;
        }
        for (View view : refreshList) {
            if (view == null) {
                continue;
            }
            if (view instanceof BeeWorksGridView) {
                ((BeeWorksGridView) view).refreshAdapter();
            }
            if (view instanceof ListGroupView) {
                ((ListGroupView)view).refreshAdapter();
            }
        }
    }

    private void getDefinitionViews(final LinearLayout linearLayout, final BeeWorksTab beeWorksTab) {
        String defineNativeUrl = "";
        for (BeeWorksTab tab : BeeWorks.getInstance().tabs) {
            if (tab == null) {
                continue;
            }
            if (beeWorksTab.id.equalsIgnoreCase(tab.id)) {
                defineNativeUrl = tab.url;
                break;
            }
        }

        if (TextUtils.isEmpty(defineNativeUrl)) {
//            AtworkToast.showToast("无自定义url地址");
            return;
        }

        BeeWorksNetService.getInstance().queryDefinitionViews(defineNativeUrl, new BeeWorksNetService.BeeWorksDefinitionViewsListener() {
            @Override
            public void success(String data) {

                NativeJson nativeJson = NativeJson.createInstanceFromString(data);
                if (nativeJson == null) {
                    Logger.e("beeworks", "fail data =" + data);
                    AtworkToast.showToast("数据请求失败，请稍后重试");
                    return;
                }
                if (beeWorksTab != null) {
                    beeWorksTab.mNativeJson = nativeJson;
                }
                refreshUI(linearLayout, nativeJson, beeWorksTab);

            }

            @Override
            public void fail() {
                AtworkToast.showToast("网络请求失败，请稍后重试");
            }
        });

    }



    private void refreshUI(LinearLayout linearLayout, NativeJson nativeJson, BeeWorksTab beeWorksTab){
        List<View> refreshList = new ArrayList<>();
        //本地拉取成功并刷新
        int index = 0;
        if (nativeJson == null || nativeJson.contents == null) {
            return;
        }
        List<View> views = new ArrayList<>();
        boolean hasTitle = false;
        for (NativeContent content : nativeJson.contents){
            if (getCategoryTitle(content) != null) {
                hasTitle = true;
                views.add(getCategoryTitle(content));
            }

            if ("list".equalsIgnoreCase(content.mType)){
                View view = dealWithListView(content, beeWorksTab);
                refreshList.add(view);
                if (!hasTitle) {
                    view.setTag(content.mMarginTop);
                }
                views.add(view);
            }
            if ("grid".equalsIgnoreCase(content.mType)) {
                if (content.mValues.isEmpty()) {
                    continue;
                }
                View view = dealWithGridView(content, beeWorksTab);
                refreshList.add(view);
                if (!hasTitle) {
                    view.setTag(content.mMarginTop);
                }
                views.add(view);
            }
            if ("slider".equalsIgnoreCase(content.mType)) {
                View view = dealWithSliderView(content);
                if (!hasTitle) {
                    view.setTag(content.mMarginTop);
                }
                views.add(view);
            }
            if ("webView".equalsIgnoreCase(content.mType)) {
                View view = dealWithWebview(content);
                if (!hasTitle) {
                    view.setTag(content.mMarginTop);
                }
                views.add(view);
            }
            hasTitle = false;
        }
        mRefreshMap.put(beeWorksTab.id, refreshList);
        for (View view : views) {
            addToLayout(linearLayout, view, index);
            index++;
        }
        addToLayout(linearLayout, getFooterView(), index);

    }

    private View getCategoryTitle(NativeContent content) {
        if (TextUtils.isEmpty(content.mGroupName)) {
            return null;
        }
        TextView textView = new TextView(mActivity);
        textView.setTag(content.mMarginTop);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setTextColor(mActivity.getResources().getColor(R.color.common_text_color_chat_list));
        textView.setPadding(20, 20, 20, 20);
        textView.setBackgroundColor(TextUtils.isEmpty(content.mBackgroundColor) ? mActivity.getResources().getColor(R.color.search_bg) : Color.parseColor(content.mBackgroundColor));
        textView.setText(content.mGroupName);

        if ("Left".equalsIgnoreCase(content.mTitleLayout)) {
            textView.setGravity((Gravity.LEFT | Gravity.CENTER_VERTICAL));
        }
        if ("Center".equalsIgnoreCase(content.mTitleLayout)) {
            textView.setGravity((Gravity.CENTER| Gravity.CENTER_VERTICAL));
        }
        if ("Right".equalsIgnoreCase(content.mTitleLayout)) {
            textView.setGravity((Gravity.RIGHT| Gravity.CENTER_VERTICAL));
        }
        textView.setLayoutParams(layout);
        return textView;
    }

    private View getFooterView() {
        View view = new View(mActivity);
        LinearLayout.LayoutParams layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, DensityUtil.dip2px( 7));
        view.setBackgroundColor(mActivity.getResources().getColor(R.color.black));
        view.setLayoutParams(layout);
        return view;
    }

    private void addToLayout(LinearLayout linearLayout, View view, int index) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Object object = view.getTag();
        if (object != null && object instanceof Integer) {
            int marginTop = (Integer)object;
            if (marginTop != 0) {
                layoutParams.setMargins(0, DensityUtil.dip2px(marginTop), 0, 0);
            }
        }
        linearLayout.addView(view,index++,layoutParams);
    }

    /**
     * 处理listview渲染
     * @param content
     */
    private View dealWithListView(final NativeContent content, BeeWorksTab beeWorksTab) {
        ListGroupView listGroupView = new ListGroupView(mActivity, content, beeWorksTab.id);
        listGroupView.setOnItemClickListener((parent, view, position, id) -> {
            NativeItem item = content.mValues.get(position);
            if ("URL".equalsIgnoreCase(item.mActionType)) {
                boolean hideTitle = "FULL_SCREEN".equalsIgnoreCase(item.mDisplayMode);
                WebViewControlAction webViewControlAction = WebViewControlAction.newAction()
                                                                                .setUrl(item.mValue)
                                                                                .setTitle(item.mTitle)
                                                                                .setHideTitle(hideTitle);
                Intent intent = WebViewActivity.getIntent(mActivity, webViewControlAction);

                return;
            }
        });

        return listGroupView;
    }

    /**
     * 处理gridview 渲染
     * @param content
     */
    private View dealWithGridView(NativeContent content, BeeWorksTab beeWorksTab) {
        BeeWorksGridView gridView = new BeeWorksGridView(mActivity);
        List<BeeWorksGrid> list = new ArrayList<>();
        for (int i = 0; i < content.mValues.size(); i++) {
            NativeItem nativeItem = content.mValues.get(i);
            BeeWorksGrid beeWorksGrid = new BeeWorksGrid();
            beeWorksGrid.mNavi = BeeWorksGrid.Navigation.NORTH;
            beeWorksGrid.mActionType = nativeItem.mActionType;
            beeWorksGrid.mDisplayMode = nativeItem.mDisplayMode;
            beeWorksGrid.mFontColor = nativeItem.mFontColor;
            beeWorksGrid.mIcon = nativeItem.mIcon;
            beeWorksGrid.mTipUrl = nativeItem.mTipUrl;
            beeWorksGrid.mTitle = nativeItem.mTitle;
            beeWorksGrid.mValue = nativeItem.mValue;
            list.add(beeWorksGrid);
        }

        final int PageCount = (int)Math.ceil(list.size()/Float.valueOf(content.mRows * content.mColumns));

        gridView.setGridViews(list, PageCount, content, beeWorksTab);
        return gridView;
    }

    private View dealWithSliderView(NativeContent content) {
        List<BeeWorksImages> imagelist = new ArrayList<>();
        for (int i = 0; i < content.mValues.size(); i++) {
            BeeWorksImages images = new BeeWorksImages();
            NativeItem nativeItem = content.mValues.get(i);
            images.mActionType = nativeItem.mActionType;
            images.mDisplayMode = nativeItem.mDisplayMode;
            images.mFontColor = nativeItem.mFontColor;
            images.mIcon = nativeItem.mIcon;
            images.mTipUrl = nativeItem.mTipUrl;
            images.mTitle = nativeItem.mTitle;
            images.mValue = nativeItem.mValue;
            imagelist.add(images);
        }
        BeeWorksImageSwitcher switcher = new BeeWorksImageSwitcher(mActivity);
        switcher.setImages(imagelist, content);
        return  switcher;
    }

    private View dealWithWebview(NativeContent content) {
        BeeWorksWebview webview = new BeeWorksWebview(mActivity);
        BeeWorksWebViewItem item = new BeeWorksWebViewItem();
        item.mHeight = content.mHeight;
        item.mUrl = content.mUrl;
        item.mName = content.mGroupName;
        webview.setWebViewParams(item);
        return webview;
    }

    /**
     * 统一beeworks字体颜色设置
     * @param textView
     * @param value
     * @param fontColor
     */
    public void setBeeText(TextView textView, String value, String fontColor) {
        try {
            if (!TextUtils.isEmpty(fontColor)) {
                textView.setTextColor(Color.parseColor(fontColor));
            }
            textView.setText(value);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 统一设置图片
     * @param imageView
     * @param value
     */
    public void setBeeImage(ImageView imageView, String value, int option) {
        //如果图片资源确实没有
        if (TextUtils.isEmpty(value)) {
            imageView.setImageResource(R.mipmap.beeworks_default_icon);
            return;
        }
        int resId = -1;
        resId = ImageViewUtil.getResourceInt(value);

        if (resId != -1) {
            imageView.setImageResource(resId);
            return;
        }
        //本地没有，可能是beeworks的，需要加上 _
        resId = ImageViewUtil.getResourceInt("_" + value.toLowerCase());
        if (resId != -1) {
            imageView.setImageResource(resId);
            return;
        }

        //如果本地没有，去查一下网络

        ImageCacheHelper.displayImageByMediaIdNotNeedToken(value, imageView, option == 0 ? ImageCacheHelper.getBeeworksLoginIconOptions() : ImageCacheHelper.getBeeworksDefaultIconOptions(), null);
    }




}
