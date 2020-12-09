package com.foreveross.atwork.modules.file.activity;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.foreveross.atwork.infrastructure.model.file.FileData;
import com.foreveross.atwork.infrastructure.newmessage.post.chat.FileTransferChatMessage;
import com.foreveross.atwork.modules.file.fragement.OfficeViewFragment;
import com.foreveross.atwork.support.SingleFragmentActivity;

/**
 * Created by dasunsy on 2018/1/9.
 */

public class OfficeViewActivity extends SingleFragmentActivity {

    public static final int VIEW_FROM_FILE_DETAIL = 1;
    public static final int VIEW_FROM_CHAT_LIST = 0;
    public static final int VIEW_FROM_OTHER = 2;
    public static final int VIEW_FROM_FILE_DATA_LIST = 3;

    public static final String DATA_FILE_PATH = "DATA_FILE_PATH";
    public static final String DATA_FROM = "DATA_FROM";
    public static final String SESSION_ID = "SESSION_ID";
    public static final String FILE_TRANSFER_CHAT_MESSAGE = "FILE_TRANSFER_CHAT_MESSAGE";
    public static final String FILE_DATA = "FILE_DATA";
    public static final String INTENT_TYPE = "intent_type";



    public static Intent getIntent(Context context, String filePath, String from) {
        Intent intent = new Intent(context, OfficeViewActivity.class);
        intent.putExtra(DATA_FILE_PATH, filePath);
        intent.putExtra(DATA_FROM, from);
        return intent;
    }
    public static Intent getIntent(Context context, String filePath, String from, String sessionId, FileTransferChatMessage fileTransferChatMessage, int intentType) {
        Intent intent = new Intent(context, OfficeViewActivity.class);
        intent.putExtra(DATA_FILE_PATH, filePath);
        intent.putExtra(DATA_FROM, from);
        intent.putExtra(SESSION_ID, sessionId);
        intent.putExtra(FILE_TRANSFER_CHAT_MESSAGE, fileTransferChatMessage);
        intent.putExtra(INTENT_TYPE, intentType);
        return intent;
    }
    public static Intent getIntent(Context context, String filePath, String from, FileData fileData, int intentType) {
        Intent intent = new Intent(context, OfficeViewActivity.class);
        intent.putExtra(DATA_FILE_PATH, filePath);
        intent.putExtra(DATA_FROM, from);
        intent.putExtra(FILE_DATA, fileData);
        intent.putExtra(INTENT_TYPE, intentType);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        return new OfficeViewFragment();
    }
}
