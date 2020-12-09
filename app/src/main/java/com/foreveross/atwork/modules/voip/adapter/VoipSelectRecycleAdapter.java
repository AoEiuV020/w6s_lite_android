package com.foreveross.atwork.modules.voip.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.modules.contact.util.ContactInfoViewUtil;

import java.util.List;

/**
 * Created by dasunsy on 16/7/11.
 */
public class VoipSelectRecycleAdapter extends RecyclerView.Adapter {

    private Context mContext;

    private List<? extends ShowListItem> mContactSelectedList;
    private final LayoutInflater mInflater;

    private OnHandleClickAddOrRemoveUserListener mOnHandleClickAddOrRemoveUserListener;
    private int mMode = Mode.CAN_ADD_AND_REMOVE;

    /**
     * 点击删除按钮进入 删除 member 的模式
     */
    public boolean mRemoveMode = false;


    public VoipSelectRecycleAdapter(Context context, List<? extends ShowListItem> contactSelectedList, OnHandleClickAddOrRemoveUserListener onHandleClickAddOrRemoveUserListener) {
        this.mContext = context;

        this.mInflater = LayoutInflater.from(context);
        this.mContactSelectedList = contactSelectedList;
        this.mOnHandleClickAddOrRemoveUserListener = onHandleClickAddOrRemoveUserListener;
    }

    public int getMode() {
        return mMode;
    }

    /**
     * 切换 mode
     */
    public void setMode(int mode) {
        this.mMode = mode;
    }


    @Override
    public int getItemCount() {
        if (Mode.CAN_ADD_AND_REMOVE == mMode) {
            return mContactSelectedList.size() + 2;

        } else if (Mode.CAN_ONLY_REMOVE == mMode || Mode.CAN_ONLY_ADD == mMode) {
            return mContactSelectedList.size() + 1;

        }

        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (ViewType.ADD == viewType) {
            AddOrRemoveViewHolder viewHolder = new AddOrRemoveViewHolder(mInflater.inflate(R.layout.item_voip_select_add_or_remove, parent, false));
            viewHolder.mIvAddOrRemove.setOnClickListener((v -> {
                mOnHandleClickAddOrRemoveUserListener.onClickAdd();
            }));
            return viewHolder;

        } else if (ViewType.REMOVE == viewType) {

            AddOrRemoveViewHolder viewHolder = new AddOrRemoveViewHolder(mInflater.inflate(R.layout.item_voip_select_add_or_remove, parent, false));
            viewHolder.mIvAddOrRemove.setOnClickListener(v -> {
                mRemoveMode = !mRemoveMode;

                notifyDataSetChanged();
            });

            return viewHolder;
        } else {

            return new VoipSelectViewHolder(mInflater.inflate(R.layout.item_voip_select_child, parent, false), position -> {
                if (mRemoveMode) {
                    mOnHandleClickAddOrRemoveUserListener.onClickRemoveUser(position);

                }

            }, position -> {
                if (mRemoveMode) {
                    mOnHandleClickAddOrRemoveUserListener.onClickRemoveUser(position);
                }
            });
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (ViewType.ADD == type) {
            AddOrRemoveViewHolder addOrRemoveViewHolder = (AddOrRemoveViewHolder) holder;
            addOrRemoveViewHolder.mIvAddOrRemove.setImageResource(R.mipmap.icon_voip_select_add);
            addOrRemoveViewHolder.mTvLabel.setText(mContext.getString(R.string.label_voip_can_add_count_left, AtworkConfig.VOIP_MEMBER_COUNT_MAX - mContactSelectedList.size()));

        } else if (ViewType.REMOVE == type) {
            AddOrRemoveViewHolder addOrRemoveViewHolder = (AddOrRemoveViewHolder) holder;
            addOrRemoveViewHolder.mIvAddOrRemove.setImageResource(R.mipmap.icon_voip_select_remove);
            addOrRemoveViewHolder.mTvLabel.setText(R.string.label_remove);

        } else {
            ShowListItem contactSelected = mContactSelectedList.get(position);

            VoipSelectViewHolder selectViewHolder = ((VoipSelectViewHolder) holder);

            ContactInfoViewUtil.dealWithContactInitializedStatus(selectViewHolder.mIvAvatar, selectViewHolder.mTvName, contactSelected, false, true);

            if(User.isYou(mContext, contactSelected.getId())) {
                selectViewHolder.mTvName.setText(R.string.item_about_me);

            }

            if (mRemoveMode && !User.isYou(mContext, contactSelected.getId())) {
                selectViewHolder.mVRemove.setVisibility(View.VISIBLE);

            } else {
                selectViewHolder.mVRemove.setVisibility(View.GONE);

            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (Mode.CAN_ADD_AND_REMOVE == mMode) {
            if (getItemCount() - 2 == position) {
                return ViewType.ADD;

            } else if (getItemCount() - 1 == position) {
                return ViewType.REMOVE;

            }
        } else if (Mode.CAN_ONLY_REMOVE == mMode) {
            if (getItemCount() - 1 == position) {
                return ViewType.REMOVE;
            }

        } else if (Mode.CAN_ONLY_ADD == mMode) {
            if (getItemCount() - 1 == position) {
                return ViewType.ADD;
            }
        }

        return ViewType.NORMAL;

    }


    static class VoipSelectViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView mIvAvatar;
        public TextView mTvName;
        public View mVRemove;

        public OnClickItemListener mOnClickItemListener;
        public OnClickRemoveListener mOnClickRemoveListener;

        public VoipSelectViewHolder(View itemView, OnClickItemListener onClickItemListener, OnClickRemoveListener onClickRemoveListener) {
            super(itemView);
            mIvAvatar = itemView.findViewById(R.id.iv_avatar);
            mTvName = itemView.findViewById(R.id.tv_name);
            mVRemove = itemView.findViewById(R.id.fl_user_remove);


            itemView.setOnClickListener(this);
            mIvAvatar.setOnClickListener(this);
            mVRemove.setOnClickListener(this);

            mOnClickItemListener = onClickItemListener;
            mOnClickRemoveListener = onClickRemoveListener;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.rl_root:
                case R.id.iv_avatar:

                    if (null != mOnClickItemListener) {
                        mOnClickItemListener.onClick(getAdapterPosition());
                    }
                    break;

                case R.id.fl_user_remove:
                    if (null != mOnClickRemoveListener) {
                        mOnClickRemoveListener.onClick(getAdapterPosition());
                    }

                    break;
            }
        }

        public interface OnClickItemListener {
            void onClick(int position);
        }

        public interface OnClickRemoveListener {
            void onClick(int position);
        }
    }

    static class AddOrRemoveViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIvAddOrRemove;
        public TextView mTvLabel;

        public AddOrRemoveViewHolder(View itemView) {
            super(itemView);
            mIvAddOrRemove = itemView.findViewById(R.id.iv_add_or_remove);
            mTvLabel = itemView.findViewById(R.id.tv_discussion_label_in_basic_info_area);
        }
    }

    /**
     * 点击"添加"或者"删除"member 按钮的监听事件
     */
    public interface OnHandleClickAddOrRemoveUserListener {
        void onClickAdd();

        void onClickRemoveUser(int position);
    }


    final class ViewType {
        public final static int ADD = 0;
        public final static int REMOVE = 1;
        public final static int NORMAL = 2;
    }

    public static final class Mode {
        public final static int CAN_ADD_AND_REMOVE = 0;
        public final static int CAN_ONLY_REMOVE = 1;
        public final static int CAN_ONLY_ADD = 2;

    }
}
