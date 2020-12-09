package com.foreverht.db.service.repository;

import android.database.Cursor;
import androidx.annotation.NonNull;
import android.util.Log;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.CustomerMessageNoticeDBHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ReyZhang on 2015/5/13.
 */
public class CustomerMessageNoticeRepository extends W6sBaseRepository {

    private static final String TAG = CustomerMessageNoticeRepository.class.getSimpleName();

    private static CustomerMessageNoticeRepository sInstance = new CustomerMessageNoticeRepository();

    public static final String SELECT_CUSTOMER_MESSAGE_NOTICE_SQL = "select * from " + CustomerMessageNoticeDBHelper.TABLE_NAME + " where " + CustomerMessageNoticeDBHelper.DBColumn._SHIELD_ID + "=?";

    public static CustomerMessageNoticeRepository getsInstance() {
        synchronized (TAG) {
            if (sInstance == null) {
                sInstance = new CustomerMessageNoticeRepository();
            }
            return sInstance;
        }
    }


    /**
     * 查询所有被屏蔽的ID
     * @return
     */
    @NonNull
    public List<String> getShieldIds(){
        List<String> shieldIds = new ArrayList<>();

        String sql = "select shield_id_ from customer_message_notice_";

        Cursor cursor = null;
        try{
            cursor = getReadableDatabase().rawQuery(sql, new String[] {});
            while(cursor.moveToNext()){
                String shieldId = cursor.getString(cursor.getColumnIndex("shield_id_"));
                shieldIds.add(shieldId);
            }

        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return shieldIds;
    }
    /**
     * 查询该id是否在屏蔽列表里
     * @param id
     * @return
     */
    public boolean getCustomerMessageNoticeShield(String id) {
       boolean result = false;
        Log.i(TAG, SELECT_CUSTOMER_MESSAGE_NOTICE_SQL);
        Cursor cursor = null;
        try{
            cursor = getReadableDatabase().rawQuery(SELECT_CUSTOMER_MESSAGE_NOTICE_SQL, new String[] {id});
            while(cursor.moveToNext()){
                result = true;
            }
        }finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public boolean insertShieldId(String id) {
        boolean result = false;
        long i = getWritableDatabase().insert(CustomerMessageNoticeDBHelper.TABLE_NAME, CustomerMessageNoticeDBHelper.DBColumn._SHIELD_ID, CustomerMessageNoticeDBHelper.getContentValues(id));
        if (i != -1) {
            result = true;
        }
        return result;
    }

    public boolean removeShieldId(String id) {
        boolean result = false;
        int i = getWritableDatabase().delete(CustomerMessageNoticeDBHelper.TABLE_NAME, CustomerMessageNoticeDBHelper.DBColumn._SHIELD_ID + "=?", new String[] {id});
        if (i != -1) {
            result = true;
        }
        return result;
    }
}
