package com.foreverht.db.service.repository;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.MessageDBHelper;
import com.foreverht.db.service.dbHelper.UnreadMessageDbHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.newmessage.Message;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.OrgNotifyMessage;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class UnreadMessageRepository extends W6sBaseRepository {

    private static final String TAG = UnreadMessageRepository.class.getSimpleName();

    private static UnreadMessageRepository sInstance = new UnreadMessageRepository();

    public static UnreadMessageRepository getInstance() {
        return sInstance;
    }


    public boolean batchInsertUnread(String chatId, List<String> messageIdList, String orgId) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        boolean result = true;
        try {
            sqLiteDatabase.beginTransaction();

            for (String messageId : messageIdList) {
                insertUnread(chatId, messageId, orgId);
            }
            sqLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return result;
    }

    public boolean insertNoticeFakeUnread(String chatId, String orgCode) {
        return insertUnread(chatId, Session.FAKE_UNREAD_ID_TO_NOTICE, orgCode);
    }


    public boolean insertUnread(String chatId, String messageId, String orgCode) {
        boolean result;
        long insertResult = getWritableDatabase().insertWithOnConflict(
                UnreadMessageDbHelper.TABLE_NAME,
                null,
                UnreadMessageDbHelper.getContentValue(chatId, messageId, orgCode),
                SQLiteDatabase.CONFLICT_IGNORE
        );

        result = -1 != insertResult;
        return result;
    }


    public long queryMinTime(String chatId, @Nullable Set<String> msgIdSet) {
        String messageTable = Message.getMessageTableName(chatId);

        String messageDbMsgIdColumn = W6sBaseRepository.getDetailDBColumn(messageTable, MessageDBHelper.DBColumn.MSG_ID);
        String messageDbMsgTimeColumn = W6sBaseRepository.getDetailDBColumn(messageTable, MessageDBHelper.DBColumn.DELIVERY_TIME);
        String unreadDBMsgIdColumn = W6sBaseRepository.getDetailDBColumn(UnreadMessageDbHelper.TABLE_NAME, UnreadMessageDbHelper.DBColumn.MSG_ID);

        String selectMinTime = "select min(" + messageDbMsgTimeColumn + ") from " + messageTable
                + " inner join " + UnreadMessageDbHelper.TABLE_NAME + " on " + messageDbMsgIdColumn + " = " + unreadDBMsgIdColumn;


        if (null != msgIdSet) {
            selectMinTime += " where " + messageDbMsgIdColumn + " in (" + getInStringParams(new ArrayList<>(msgIdSet)) + ")";
        }

        long minTime = -1;

        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().rawQuery(selectMinTime, new String[]{});
            while (cursor.moveToNext()) {
                minTime = cursor.getLong(0);

            }


        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return minTime;
    }


    public long queryMaxTime(String chatId, @Nullable Set<String> msgIdSet) {
        String messageTable = Message.getMessageTableName(chatId);

        String messageDbMsgIdColumn = W6sBaseRepository.getDetailDBColumn(messageTable, MessageDBHelper.DBColumn.MSG_ID);
        String messageDbMsgTimeColumn = W6sBaseRepository.getDetailDBColumn(messageTable, MessageDBHelper.DBColumn.DELIVERY_TIME);
        String unreadDBMsgIdColumn = W6sBaseRepository.getDetailDBColumn(UnreadMessageDbHelper.TABLE_NAME, UnreadMessageDbHelper.DBColumn.MSG_ID);

        String selectMaxTime = "select max(" + messageDbMsgTimeColumn + ") from " + messageTable
                + " inner join " + UnreadMessageDbHelper.TABLE_NAME + " on " + messageDbMsgIdColumn + " = " + unreadDBMsgIdColumn;

        if (null != msgIdSet) {
            selectMaxTime += " where " + messageDbMsgIdColumn + " in (" + getInStringParams(new ArrayList<>(msgIdSet)) + ")";
        }

        long maxTime = -1;

        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().rawQuery(selectMaxTime, new String[]{});
            while (cursor.moveToNext()) {
                maxTime = cursor.getLong(0);

            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return maxTime;
    }


    /**
     * 获取未读消息 set
     */
    @NonNull
    public Set<String> queryUnreadSet(String chatId) {
        String querySQL = "select * from " + UnreadMessageDbHelper.TABLE_NAME + " where " + UnreadMessageDbHelper.DBColumn.CHAT_ID + " = ?";

        if (OrgNotifyMessage.FROM.equals(chatId)) {
            List<String> adminOrgCodeList = OrganizationRepository.getInstance().queryLoginAdminOrgSync(BaseApplicationLike.baseContext);
            if (!ListUtil.isEmpty(adminOrgCodeList)) {
                querySQL += " and " + UnreadMessageDbHelper.DBColumn.ORG_CODE + " in (" + getInStringParams(adminOrgCodeList) + ")";

            }

        }

        Set<String> unreadSet = new HashSet<>();
        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().rawQuery(querySQL, new String[]{chatId});
            while (cursor.moveToNext()) {
                String msgId = cursor.getString(cursor.getColumnIndex(UnreadMessageDbHelper.DBColumn.MSG_ID));
                unreadSet.add(msgId);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return unreadSet;
    }


    public HashMap<String, Set<String>> queryUnreadSetInMap(Set<String> chatIdSet) {
        HashMap<String, Set<String>> unreadSetInMap = new HashMap<>();

        if (chatIdSet.contains(OrgNotifyMessage.FROM)) {
            chatIdSet.remove(OrgNotifyMessage.FROM);
            unreadSetInMap.put(OrgNotifyMessage.FROM, queryUnreadSet(OrgNotifyMessage.FROM));
        }


        String querySQL = "select * from " + UnreadMessageDbHelper.TABLE_NAME + " where " + UnreadMessageDbHelper.DBColumn.CHAT_ID + " in (" + getInStringParams(new ArrayList<>(chatIdSet)) + ")";

        Cursor cursor = null;

        try {
            cursor = getReadableDatabase().rawQuery(querySQL, new String[]{});
            while (cursor.moveToNext()) {
                String msgId = cursor.getString(cursor.getColumnIndex(UnreadMessageDbHelper.DBColumn.MSG_ID));
                String chatId = cursor.getString(cursor.getColumnIndex(UnreadMessageDbHelper.DBColumn.CHAT_ID));

                Set<String> unreadSet = unreadSetInMap.get(chatId);
                if (null == unreadSet) {
                    unreadSet = new HashSet<>();
                    unreadSetInMap.put(chatId, unreadSet);
                }

                unreadSet.add(msgId);


            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return unreadSetInMap;

    }

    /**
     * 获取当前未读数
     */
    public int queryUnreadCount(String chatId) {
        String querySQL = "select count(*) as count from " + UnreadMessageDbHelper.TABLE_NAME + " where " + UnreadMessageDbHelper.DBColumn.CHAT_ID + " = ?";

        int count = 0;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(querySQL, new String[]{chatId});
            if (cursor.moveToNext()) {
                count = cursor.getInt(cursor.getColumnIndex("count"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;
    }

    public boolean batchRemoveUnread(String chatId, Set<String> unreadSet) {
        boolean result = true;
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();

            for (String msgId : unreadSet) {
                removeUnread(chatId, msgId);
            }
            sqLiteDatabase.setTransactionSuccessful();

        } catch (Exception e) {
            e.printStackTrace();
            result = false;

        } finally {
            sqLiteDatabase.endTransaction();
        }

        return result;

    }

    public boolean removeChatUnread(String chatId) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();

        int deletedCount = sqLiteDatabase.delete(UnreadMessageDbHelper.TABLE_NAME, UnreadMessageDbHelper.DBColumn.CHAT_ID + " = ?", new String[]{chatId});

        return 0 != deletedCount;
    }


    public boolean removeUnreadOrg(String orgCode) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        int deletedCount;
        if (StringUtils.isEmpty(orgCode)) {
            deletedCount = sqLiteDatabase.delete(UnreadMessageDbHelper.TABLE_NAME, UnreadMessageDbHelper.DBColumn.CHAT_ID + " = ?", new String[]{OrgNotifyMessage.FROM});

        } else {
            deletedCount = sqLiteDatabase.delete(UnreadMessageDbHelper.TABLE_NAME, UnreadMessageDbHelper.DBColumn.CHAT_ID + " = ? and " + UnreadMessageDbHelper.DBColumn.ORG_CODE + " = ?", new String[]{OrgNotifyMessage.FROM, orgCode});

        }

        return 0 != deletedCount;
    }

    /**
     * 获取组织的未读数
     */
    public int queryOrgUnreadCount(String orgCode) {
        String querySQL = "select count(*) as count from " + UnreadMessageDbHelper.TABLE_NAME + " where " + UnreadMessageDbHelper.DBColumn.ORG_CODE + " = ?";

        int count = 0;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(querySQL, new String[]{orgCode});
            if (cursor.moveToNext()) {
                count = cursor.getInt(cursor.getColumnIndex("count"));
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return count;
    }


    public List<String> queryUnreadOrgApplyCountByOrgCode(String orgCode) {
        String msgId = "";
        String querySQL = "select  " + UnreadMessageDbHelper.DBColumn.MSG_ID +
                "  from " + UnreadMessageDbHelper.TABLE_NAME +
                " where " + UnreadMessageDbHelper.DBColumn.CHAT_ID + " = ?" +
                " and " + UnreadMessageDbHelper.DBColumn.ORG_CODE + " = ?";
        Cursor cursor = null;
        List<String> msgIds = new ArrayList<>();

        try {
            cursor = getReadableDatabase().rawQuery(querySQL, new String[]{OrgNotifyMessage.FROM, orgCode});
            while (cursor.moveToNext()) {
                msgId = cursor.getString(0);
                msgIds.add(msgId);
            }

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return msgIds;
    }


    /**
     * @see #removeUnread(String, String, SQLiteDatabase)
     * */
    public boolean removeUnread(String chatId, @Nullable String messageId) {
        return removeUnread(chatId, messageId, null);
    }

    /**
     * 删除未读消息
     *
     * @param chatId
     * @param messageId 若 messageId 为空 则全部删除
     */
    public boolean removeUnread(String chatId, @Nullable String messageId, @Nullable SQLiteDatabase sqLiteDatabase) {
        if (null == sqLiteDatabase) {
            sqLiteDatabase = getWritableDatabase();
        }

        int deletedCount;
        if (StringUtils.isEmpty(messageId)) {
            deletedCount = sqLiteDatabase.delete(UnreadMessageDbHelper.TABLE_NAME, UnreadMessageDbHelper.DBColumn.CHAT_ID + " = ?", new String[]{chatId});

        } else {
            deletedCount = sqLiteDatabase.delete(UnreadMessageDbHelper.TABLE_NAME, UnreadMessageDbHelper.DBColumn.CHAT_ID + " = ? and " + UnreadMessageDbHelper.DBColumn.MSG_ID + " = ?", new String[]{chatId, messageId});

        }

        return 0 != deletedCount;
    }

}
