package com.foreveross.atwork.modules.contact.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.utils.AvatarHelper;

import java.util.ArrayList;

/**
 * Created by dasunsy on 2015/6/29 0029.
 */
public class SyncContactFailedAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<ShowListItem> mUserList;

    public SyncContactFailedAdapter(Context context, ArrayList<ShowListItem> contactList) {
        this.mContext = context;
        this.mUserList = contactList;
    }

    @Override
    public int getCount() {
        return mUserList.size();
    }

    @Override
    public Object getItem(int position) {
        return mUserList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_contact_failed, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ShowListItem contact = mUserList.get(position);
        AvatarHelper.setUserAvatarById(viewHolder.tvAvatar, contact.getId(), contact.getDomainId(), false, true);
        viewHolder.tvName.setText(contact.getTitle());

        if(contact instanceof User) {
            viewHolder.tvMobile.setText(((User) contact).mPhone);

        } else if(contact instanceof Employee){
            viewHolder.tvMobile.setText(((Employee) contact).mobile);

        }

        return convertView;
    }

    public static class ViewHolder {
        public final ImageView tvAvatar;
        public final TextView tvName;
        public final TextView tvMobile;
        public final View root;

        public ViewHolder(View root) {
            tvAvatar = (ImageView) root.findViewById(R.id.iv_avatar);
            tvName = (TextView) root.findViewById(R.id.tv_name);
            tvMobile = (TextView) root.findViewById(R.id.tv_mobile);
            this.root = root;
        }
    }
}
