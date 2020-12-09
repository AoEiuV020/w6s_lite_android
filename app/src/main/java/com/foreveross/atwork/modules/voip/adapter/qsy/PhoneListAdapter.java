package com.foreveross.atwork.modules.voip.adapter.qsy;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreveross.atwork.R;

import java.util.ArrayList;

/**
 * Created by RocXu on 2015/12/25.
 */
public class PhoneListAdapter extends BaseAdapter {

    public static final String TAG = "PhoneListAdapter";

    private ArrayList<String> userPhoneList;
    private String currentVoiceValue; ///< 如果是Voip,则此处是"网络语音"；如果是电话呼叫，则此处是外呼的电话号码
    private Context mContext;

    public PhoneListAdapter(Context context, ArrayList<String> phoneList, String currentVoiceValue) {
        mContext = context;
        this.userPhoneList = new ArrayList<>();
        this.userPhoneList.add(mContext.getString(R.string.tangsdk_voice_voip_type));
        if (phoneList != null) {
            for (String phone : phoneList) {
                if (TextUtils.isEmpty(phone)) {
                    continue;
                }
                this.userPhoneList.add(phone);
            }
        }
        this.currentVoiceValue = currentVoiceValue;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        if (convertView == null) {
            convertView = createItemView();
        }
        ViewCache cache = (ViewCache) convertView.getTag();
        String itemData = getItem(position);
        if (itemData.equals(mContext.getString(R.string.tangsdk_voice_voip_type))) {
            cache.voiceTypeIV.setImageResource(R.mipmap.tangsdk_dialpad_voip_icon);
        } else {
            cache.voiceTypeIV.setImageResource(R.mipmap.tangsdk_dialpad_phone_icon);
        }
        cache.voiceValueTV.setText(itemData);
        if (currentVoiceValue.equals(itemData)) {
            cache.checkItemIV.setVisibility(View.VISIBLE);
        } else {
            cache.checkItemIV.setVisibility(View.GONE);
        }
        return convertView;
    }


    private View createItemView() {
        final View view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.tangsdk_phone_list_item, null);
        ViewCache cache = new ViewCache();
        cache.voiceTypeIV = (ImageView) view.findViewById(R.id.voice_type_iv);
        cache.voiceValueTV = (TextView) view.findViewById(R.id.value_tv);
        cache.checkItemIV = (ImageView) view.findViewById(R.id.check_item_iv);
        view.setTag(cache);
        return view;
    }

    @Override
    public int getCount() {
        return userPhoneList != null ? userPhoneList.size() : 0;
    }

    @Override
    public String getItem(int position) {
        if (position >= 0 && position < getCount()) {
            return userPhoneList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void clear() {
        mContext = null;
    }


    public final class ViewCache {

        public ImageView voiceTypeIV;
        public TextView voiceValueTV;
        public ImageView checkItemIV;
    }
}
