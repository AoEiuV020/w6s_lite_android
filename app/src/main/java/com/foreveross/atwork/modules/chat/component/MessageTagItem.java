package com.foreveross.atwork.modules.chat.component;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.R;

public class MessageTagItem extends FrameLayout {

    private TextView mItemName;

    private ImageView mSelectIndex;

    public MessageTagItem(@NonNull Context context) {
        this(context, null);
    }

    public MessageTagItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageTagItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initItem(context);
    }

    private void initItem(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.view_message_tag_item, this, true);
        mItemName = view.findViewById(R.id.item_name);
        mSelectIndex = view.findViewById(R.id.selected_flag);
    }

    public void setText(String text) {
        mItemName.setText(text);
    }

    public void isSelected(boolean isSelected) {
        mSelectIndex.setVisibility(isSelected ? View.VISIBLE : View.GONE);
        mItemName.setTextColor(isSelected ? getResources().getColor(R.color.blue_lock) : getResources().getColor(R.color.common_text_color_999));
    }
}
