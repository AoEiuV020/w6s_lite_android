package com.foreveross.atwork.modules.chat.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.component.ProgressDialogHelper;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.modules.chat.fragment.ChatInfoFragment;
import com.foreveross.atwork.modules.contact.component.ChatInfoContactItemView;

import java.util.ArrayList;
import java.util.List;


public class ChatInfoUserListBaseAdapter extends BaseAdapter {

    /**
     * 群详情的gridView初始化显示人数  4人为一行
     * */
    public static final int BASE_COUNT = 8;

    /**
     * 点击更多成员时添加的人数
     * */
    public static final int ADD_COUNT = 40;

    public ChatInfoFragment.AddOrRemoveListener addOrRemoveListener;
    private List<ShowListItem> userContactItemList;
    private Activity activity;
    private CanOperationType canOperationType;
    private boolean remove;

    /**
     * 添加人员
     */
    private View addView;
    /**
     * 删除人员
     */
    private View removeView;

    private boolean smallModel = true;

    private int smallCount = 0;

    public ChatInfoUserListBaseAdapter(Activity activity, CanOperationType canOperationType,
                                       ChatInfoFragment.AddOrRemoveListener addOrRemoveListener) {
        this.activity = activity;
        this.userContactItemList = new ArrayList<>();
        this.canOperationType = canOperationType;
        this.addOrRemoveListener = addOrRemoveListener;

    }

    public void removeClick() {
        this.remove = !remove;
        notifyDataSetChanged();
    }

    public void resetRemoveMode() {
        remove = false;
        notifyDataSetChanged();
    }

    public void smallModel() {
        smallCount = 0;
        smallModel = true;
        notifyDataSetChanged();
    }

    public void notSmallModel() {
        smallCount++;
        smallModel = false;
        notifyDataSetChanged();
    }

    public void updateOperationType(CanOperationType canOperationType) {
        this.canOperationType = canOperationType;
        notifyDataSetChanged();
    }

    public void setUserContactItemList(CanOperationType canOperationType, List<? extends ShowListItem> contacts) {
        this.canOperationType = canOperationType;
        setUserContactItemList(contacts);
    }

    public void setUserContactItemList(List<? extends ShowListItem> contacts) {
        userContactItemList.clear();
        userContactItemList.addAll(contacts);
        notifyDataSetChanged();
    }

    private View getAddView() {
        if (addView == null) {
            addView = getAddOrRemoveView(R.mipmap.icon_add_discussion_member);
        }
        return addView;
    }

    private View getRemoveView() {
        if (removeView == null) {
            removeView = getAddOrRemoveView(R.mipmap.icon_remove_discussion_member);
        }
        return removeView;
    }

    private View getAddOrRemoveView(int resId) {
        View view = activity.getLayoutInflater().inflate(R.layout.chat_info_user_list, null);
        ImageView avatar = view.findViewById(R.id.user_list_avatar);
        TextView name = view.findViewById(R.id.user_list_name);
        view.findViewById(R.id.user_remove).setVisibility(View.GONE);
        avatar.setImageResource(resId);
        name.setVisibility(View.INVISIBLE);
        return view;
    }

    public boolean hasMore() {
        int count = BASE_COUNT;
        if (smallCount > 0) {
            count = BASE_COUNT  + smallCount * ADD_COUNT;
        }

        return canOperationType.getCount(userContactItemList.size()) > count;
    }

    @Override
    public int getCount() {
        int count = BASE_COUNT;
        if (smallCount > 0) {
            count = BASE_COUNT  + smallCount * ADD_COUNT;
        }
        if (canOperationType.getCount(userContactItemList.size()) >= count) {
            return count;
        }
        return canOperationType.getCount(userContactItemList.size());
    }

    @Override
    public Object getItem(int position) {
        int fixedPosition = canOperationType.getFixedPosition(position);
        return userContactItemList.get(fixedPosition);
    }

    @Override
    public long getItemId(int position) {
        return canOperationType.getFixedPosition(position);
    }

    @Override
    public int getItemViewType(int position) {
        if (canOperationType == CanOperationType.CanAddAndRemove) {
            if (position == 0) {
                return ViewType.ADD;
            } else if (position == 1) {
                return ViewType.REMOVE;
            }
        } else if (canOperationType == CanOperationType.OnlyCanAdd) {
            if (position == 0) {
                return ViewType.ADD;
            }
        }

        return ViewType.NORMAL;

    }

    @Override
    public int getViewTypeCount() {
        return ViewType.COUNT;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (canOperationType == CanOperationType.CanAddAndRemove) {
            if (position == 0) {
                return getAddView();
            } else if (position == 1) {
                return getRemoveView();
            }
        } else if (canOperationType == CanOperationType.OnlyCanAdd) {
            if (position == 0) {
                return getAddView();
            }
        }

        if(null == convertView) {
            convertView = new ChatInfoContactItemView(activity);
        }

        int fixed = canOperationType.getFixedPosition(position);

        if (fixed >= 0) {
            ShowListItem contact = userContactItemList.get(fixed);
            ChatInfoContactItemView chatInfoContactItemView = (ChatInfoContactItemView) convertView;
            chatInfoContactItemView.refreshView(contact, remove);
            chatInfoContactItemView.setAddOrRemoveListener(addOrRemoveListener);
        }
        return convertView;
    }

    public boolean isRemoveMode(){
        return remove;
    }




    final class ViewType{
        public final static int COUNT = 3;
        public final static int ADD = 0;
        public final static int REMOVE = 1;
        public final static int NORMAL = 2;
    }

}
