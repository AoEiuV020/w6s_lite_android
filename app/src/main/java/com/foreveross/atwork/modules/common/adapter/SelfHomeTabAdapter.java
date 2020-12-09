package com.foreveross.atwork.modules.common.adapter;/**
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 __           __
 .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 |________|_____|__|  |__|__|   __||__||_____|_____|
 |__|
 */


import android.content.Context;
import android.content.res.TypedArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.foreveross.atwork.R;
import com.foreveross.atwork.api.sdk.app.model.LightNoticeData;
import com.foreveross.atwork.modules.aboutme.component.MeFunctionsItemView;
import com.foreveross.atwork.modules.main.data.TabNoticeManager;

/**
 * Created by reyzhang22 on 15/11/23.
 */
public class SelfHomeTabAdapter extends BaseAdapter {

    private TypedArray mFunctionsImagesRes;
    private String[] mFunctionsNameArr;
    private Context mContext;

    public SelfHomeTabAdapter(Context context) {
        mContext = context;
        mFunctionsNameArr = context.getResources().getStringArray(R.array.self_define_functions_name_array);
        mFunctionsImagesRes = context.getResources().obtainTypedArray(R.array.self_define_functions_img_array);
    }

    @Override
    public int getCount() {
        return mFunctionsNameArr == null ? 0 : mFunctionsNameArr.length;
    }

    @Override
    public Object getItem(int position) {
        return mFunctionsNameArr[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView==null){
            convertView = new MeFunctionsItemView(mContext);

        }

        final MeFunctionsItemView meFunctionsItemView = (MeFunctionsItemView)convertView;
//        meFunctionsItemView.refreshItemView(mFunctionsNameArr[position], mFunctionsImagesRes.getResourceId(position, R.mipmap.icon_me_mes));


        if(mContext.getString(R.string.college_circle).equals(mFunctionsNameArr[position])) {
            LightNoticeData lightNoticeJson = TabNoticeManager.getInstance().getLightNoticeData(TabNoticeManager.getInstance().getCircleAppId(mContext));
            meFunctionsItemView.refreshLightNotice(lightNoticeJson);
        } else {
            meFunctionsItemView.showNothing();
        }


        return convertView;
    }
}
