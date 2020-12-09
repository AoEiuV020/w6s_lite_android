package com.foreverht.db.service.repository;

import android.database.Cursor;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.SessionDBHelper;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.SessionTop;
import com.foreveross.atwork.infrastructure.newmessage.Message;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lingen on 15/4/15.
 * Description:
 * 对聊天的session的Repository
 */
public class SessionRepository extends W6sBaseRepository {


    private static SessionRepository sessionRepository = new SessionRepository();

    private SessionRepository() {

    }

    public static SessionRepository getInstance() {
        return sessionRepository;
    }

    /**
     * 更新一个SESSION
     *
     * @param session
     * @return
     */
    public boolean updateSession(Session session) {
        long result = getWritableDatabase().insertWithOnConflict(
                SessionDBHelper.DBColumn.TABLE_NAME,
                null,
                SessionDBHelper.getContentValues(session), SQLiteDatabase.CONFLICT_REPLACE);

        //clear unread
        if (0 != session.getUnread()) {
            Set<String> unreadMessageIdInsertSet = new HashSet<>(session.unreadMessageIdSet);
            unreadMessageIdInsertSet.removeAll(session.unreadInDbSet);

            boolean batchInsertUnreadResult = UnreadMessageRepository.getInstance().batchInsertUnread(session.identifier, new ArrayList<>(unreadMessageIdInsertSet), session.orgId);
            if(batchInsertUnreadResult) {
                session.unreadInDbSet.addAll(unreadMessageIdInsertSet);
            }
        }

        if(!ListUtil.isEmpty(session.unreadTransferIdSet)){
            UnreadMessageRepository.getInstance().batchRemoveUnread(session.identifier, session.unreadTransferIdSet);
        }

        session.unreadTransferIdSet.clear();

        if (result == -1) {
            return false;
        }
        session.savedToDb = true;
        return true;
    }

    public boolean batchUpdateSession(List<Session> sessionList) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        boolean result = false;

        try {
            sqLiteDatabase.beginTransaction();


            for (Session session : sessionList) {
                updateSession(session);
            }

            sqLiteDatabase.setTransactionSuccessful();

            result = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            sqLiteDatabase.endTransaction();

        }

        return result;

    }



    public boolean batchRemoveSessions(List<String> sessionIdentifiers, boolean removeMessageTable) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();


            for(String sessionId : sessionIdentifiers) {
                doRemoveSession(sessionId, removeMessageTable, sqLiteDatabase);
            }


            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

        return true;

    }


    /**
     * 删除一个SESSION会话
     *
     * @param sessionIdentifier
     * @param removeMessageTable
     * @return
     */
    public boolean removeSession(String sessionIdentifier, boolean removeMessageTable) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        try {
            sqLiteDatabase.beginTransaction();
            doRemoveSession(sessionIdentifier, removeMessageTable, sqLiteDatabase);


            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

        return true;
    }

    private void doRemoveSession(String sessionIdentifier, boolean removeMessageTable, SQLiteDatabase sqLiteDatabase) {
        //清除SESSION
        String sql = "delete from session_ where identifier_ = ?";
        sqLiteDatabase.execSQL(sql, new String[]{sessionIdentifier});

        //清除 unread
        UnreadMessageRepository.getInstance().removeUnread(sessionIdentifier, null, sqLiteDatabase);

        if(removeMessageTable){
            String messageTable = Message.getMessageTableName(sessionIdentifier);
            if (tableExists("message_" + sessionIdentifier)) {
                String deleteAllMessages = "delete  from " + messageTable;
                sqLiteDatabase.execSQL(deleteAllMessages);
            }
        }
    }


    public List<Session> querySessions(String sql) {
        long startTime = System.currentTimeMillis();

        List<Session> sessions = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{});
            while (cursor.moveToNext()) {
                Session session = SessionDBHelper.fromCursor(cursor);
                sessions.add(session);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        refreshUnreadSet(sessions);

        long endTime = System.currentTimeMillis();

        LogUtil.e("querySessions duration -> " + (endTime - startTime));

        return sessions;
    }

    public List<Session> querySessionsLocalTopOrShield(List<String> shieldIds) {
        String sql = "select * from session_ where " + SessionDBHelper.DBColumn.TOP + " = " + SessionTop.LOCAL_TOP + " or " + SessionDBHelper.DBColumn.IDENTIFIER + " in ( " + getInStringParams(shieldIds) + ")";
        return querySessions(sql);
    }

    private void refreshUnreadSet(List<Session> sessions) {
        Set<String> sessionIdSet = new HashSet<>();
        for(Session session : sessions) {
            sessionIdSet.add(session.identifier);
        }

        HashMap<String, Set<String>> unreadSetInMap = UnreadMessageRepository.getInstance().queryUnreadSetInMap(sessionIdSet);

        for(Session session: sessions) {
            Set<String> unreadSet = unreadSetInMap.get(session.identifier);
            if (null != unreadSet) {
                session.refreshUnreadSetTotally(unreadSet, true);
            }
        }
    }

    public Session queryEmailSessionByEntryTye(Session.EntryType entryType) {
        String sql = "select * from session_ where entry_type_ = " + entryType.intValue();
        Session session = null;
        Cursor  cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[] {});
            cursor.moveToFirst();
            session = SessionDBHelper.fromCursor(cursor);
            session.refreshUnreadSetTotally(UnreadMessageRepository.getInstance().queryUnreadSet(session.identifier), true);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if(null != cursor) {
                cursor.close();
            }
        }
        return session;
    }

    /**
     * session 消息是否存在
     * */
    public boolean isSessionExist(String identifier) {
        String sql = "select count(*) as count from " + SessionDBHelper.DBColumn.TABLE_NAME + " where " + SessionDBHelper.DBColumn.IDENTIFIER + "=?";

        int count = 0;
        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{identifier});
            if (cursor.moveToNext()) {
                count = cursor.getInt(cursor.getColumnIndex("count"));
                if (count > 0) {
                    return true;
                }
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return false;

    }

}
