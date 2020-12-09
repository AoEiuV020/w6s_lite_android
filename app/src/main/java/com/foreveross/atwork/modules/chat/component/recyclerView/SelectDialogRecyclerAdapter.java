package com.foreveross.atwork.modules.chat.component.recyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.foreveross.atwork.R;
import com.foreveross.atwork.modules.chat.util.MessageItemPopUpHelp;

import java.util.ArrayList;

public class SelectDialogRecyclerAdapter extends RecyclerView.Adapter<SelectDialogRecyclerAdapter.ViewHolder> {
    private ArrayList<String> mItemsList = null;
    private SelectDialogRecyclerView.OnDialogItemClickListener mOnRecyclerItemClickListener;

    public SelectDialogRecyclerAdapter(ArrayList<String> itemsList, SelectDialogRecyclerView.OnDialogItemClickListener onRecyclerItemClickListener) {
        mItemsList = itemsList;
        mOnRecyclerItemClickListener = onRecyclerItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_chat_select_dialog, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.mLlItemChatSelectDialog.setOnClickListener(v -> mOnRecyclerItemClickListener.onItemSelect(holder.TvDialogIteml.getText().toString()));

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        String item = mItemsList.get(i);
        viewHolder.TvDialogIteml.setText(item);

        setSelectImageResource(item,viewHolder);
    }

    @Override
    public int getItemCount() {
        return mItemsList.size();
    }

    private void setSelectImageResource(String value,ViewHolder holder) {
        //听筒
        if (MessageItemPopUpHelp.VOICE_PHONE.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_voice_phone);
        }
        //扬声器
        else if (MessageItemPopUpHelp.VOICE_SPEAK.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_voice_speak);
        }
        //语音转换成文字
        else if (MessageItemPopUpHelp.VOICE_TRANSLATE.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_translate);
        }
        //语音转换成文字后收回文字
        else if (MessageItemPopUpHelp.VOICE_SHOW_ORIGINAL.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_translate);
        }
        //复制功能
        else if (MessageItemPopUpHelp.COPY_ITEM.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_copy_message);
        }

        //更多
        else if (MessageItemPopUpHelp.MORE_ITEM.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_select_more);
        }

        //删除
        else if (MessageItemPopUpHelp.DELETE_ITEM.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_delete_message);
        }
        //转发
        else if (MessageItemPopUpHelp.FORWARDING_ITEM.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_forwarding);
        }
        //翻译
        else if (MessageItemPopUpHelp.TEXT_TRANSLATE.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_text_translate);
        }
        //已翻译的文字显示原文
        else if (MessageItemPopUpHelp.TEXT_SHOW_ORIGINAL.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_text_translate);
        }

        //撤回
        else if (MessageItemPopUpHelp.CHAT_UNDO.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_use_undo_message);
        }

        //群聊已读未读
        else if (MessageItemPopUpHelp.CHECK_UNREAD_READ.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_check_unread);
        }
        //引用回复
        else if (MessageItemPopUpHelp.MESSAGE_REFERENCE.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_message_quote);
        }

        //单聊已读
        else if (MessageItemPopUpHelp.USER_READ.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_check_read);
        }
        //单聊未读
        else if (MessageItemPopUpHelp.USER_UN_READ.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_check_unread);
        }
        //分享
        else if (MessageItemPopUpHelp.SHARE_DROPBOX.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_message_share);
        }

        //重发
        else if (MessageItemPopUpHelp.RESEND.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_message_resend);
        }

        //调试使用, 克隆一定数量消息
        else if(MessageItemPopUpHelp.DEBUG_TEST_CLONE_MESSAGE.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_debug_test_clone);
        }
        //调试使用, 当前会话消息总数量
        else if(MessageItemPopUpHelp.DEBUG_TEST_QUERY_MESSAGE_COUNT.equals(value)) {
            holder.IvChatSelectDialog.setImageResource(R.mipmap.icon_debug_test_query_message_count);
        }
     }


    //内部类（用于最外部的泛型指定）
    static class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout mLlItemChatSelectDialog;
        ImageView IvChatSelectDialog;
        TextView TvDialogIteml;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mLlItemChatSelectDialog = itemView.findViewById(R.id.item_linear_chat_select_dialog);
            IvChatSelectDialog = itemView.findViewById(R.id.iv_dialog_item);
            TvDialogIteml = itemView.findViewById(R.id.dialog_item);

        }
    }
}
