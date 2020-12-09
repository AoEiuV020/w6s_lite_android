package com.foreverht.db.service.repository;

import android.content.Context;
import android.database.Cursor;

import com.foreverht.db.service.dbHelper.MessageDBHelper;
import com.foreveross.atwork.infrastructure.newmessage.Message;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;

import java.util.HashMap;
import java.util.List;

/**
 * Created by dasunsy on 2017/9/8.
 */

public class EmergencyMessageUnconfirmedRepository extends MessageRepository {

    public static final String EMERGENCY_MSG_UNCONFIRMED_IDENTIFIER = "emergency_message_unconfirmed";

    private static final EmergencyMessageUnconfirmedRepository sEmergencyMessageRepository = new EmergencyMessageUnconfirmedRepository();

    public static EmergencyMessageUnconfirmedRepository getInstance() {
        return sEmergencyMessageRepository;
    }


    public boolean insertEmergencyMessage(Context context, ChatPostMessage chatPostMessage) {
        return insertOrUpdateMessage(context, EMERGENCY_MSG_UNCONFIRMED_IDENTIFIER, chatPostMessage);
    }


    public boolean deleteEmergencyMessages(List<String> msgIds) {
        HashMap<String, List<String>> deletedMap = new HashMap();
        deletedMap.put(EMERGENCY_MSG_UNCONFIRMED_IDENTIFIER, msgIds);
        return batchRemoveMessage(deletedMap);
    }

    public int queryCount(String from) {
        int count = 0;
        String messageTable = Message.getMessageTableName(EMERGENCY_MSG_UNCONFIRMED_IDENTIFIER);
        if (tableExists(Message.MESSAGE_TABLE_PRE + EMERGENCY_MSG_UNCONFIRMED_IDENTIFIER)) {
            String querySQL = "select count(*) as count from " + messageTable + " where " + MessageDBHelper.DBColumn.FROM + " = ?";

            Cursor cursor = null;
            try {
                cursor = getReadableDatabase().rawQuery(querySQL, new String[]{from});
                if (cursor.moveToNext()) {
                    count = cursor.getInt(cursor.getColumnIndex("count"));
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }

        return count;
    }

    /**
     * 当前会话是否有夺命 call(紧急呼)
     * */
    public boolean hasEmergencyDirtyCall(String sessionId) {
        return 0 != queryCount(sessionId);
    }

    /**
     * 查询紧急呼未确认列表
     * */
    public List<ChatPostMessage> queryUnconfirmedEmergencyMsgList(Context context, String from) {
        return queryMsgListByFrom(context, EMERGENCY_MSG_UNCONFIRMED_IDENTIFIER, from);
    }
}
