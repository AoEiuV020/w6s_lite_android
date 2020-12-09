package com.foreveross.atwork.modules.voip.adapter.qsy;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.voip.support.qsy.interfaces.OnMenuClickListener;

import java.util.List;

/**
 * Created by RocXu on 2015/12/16.
 */
public class PopMenuListAdapter extends BaseAdapter {
    private Dialog dialog;
    private Context context;
    private List<String> menuIdList;
    private OnMenuClickListener listener;

    public PopMenuListAdapter(Dialog dialog, List<String> menuIdList, OnMenuClickListener listener) {
        this.dialog = dialog;
        this.menuIdList = menuIdList;
        this.listener = listener;
        if (null != dialog) {
            this.context = dialog.getContext();
        }
    }

    @Override
    public int getCount() {
        return menuIdList == null ? 0 : menuIdList.size();
    }

    @Override
    public Object getItem(int position) {
        if (menuIdList != null && menuIdList.size() > 0) {
            return menuIdList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (null == convertView) {
            convertView = createItemView(position);
        }
        ChatMagMenuHolder holder = (ChatMagMenuHolder) convertView.getTag();
        setItemcontent(holder, position);
        return convertView;
    }

    private void setItemcontent(ChatMagMenuHolder holder, int position) {
        String menuName = menuIdList.get(position);
        if (null != holder.menuItemRadioBtn) {
            holder.menuItemRadioBtn.setText(menuName);
        }
    }

    private View createItemView(int position) {
        View convertView = null;
        ChatMagMenuHolder holder = new ChatMagMenuHolder();
        String menu = (String)getItem(position);
        if (menu.equals(context.getString(R.string.tangsdk_cancel_btn_title))) {
            convertView = LayoutInflater.from(context).inflate(R.layout.tangsdk_menu_cancel_item, null);
        } else {
            convertView = LayoutInflater.from(context).inflate(R.layout.tangsdk_menu_item, null);
        }
        holder.menuItemRadioBtn = (RadioButton) convertView.findViewById(R.id.menu_item_btn);
        if (null != holder.menuItemRadioBtn) {
            holder.menuItemRadioBtn.setOnClickListener(new OnMenuClick(position, listener, dialog));
        }
        convertView.setTag(holder);
        return convertView;
    }

    class OnMenuClick implements View.OnClickListener {
        private int which;
        private OnMenuClickListener listener;
        private Dialog dialog;

        public OnMenuClick(int which, OnMenuClickListener listener, Dialog dialog) {
            this.which = which;
            this.listener = listener;
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            if (null != listener) {
                listener.onClick(dialog, which);
            }
        }

    }

    final class ChatMagMenuHolder {
        RadioButton menuItemRadioBtn;
    }

    public void setMenuIdList(List<String> menuIdList) {
        this.menuIdList = menuIdList;
    }

}

