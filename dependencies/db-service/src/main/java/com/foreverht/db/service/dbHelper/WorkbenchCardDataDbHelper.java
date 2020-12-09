package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;
import android.database.Cursor;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.model.workbench.Workbench;
import com.foreveross.atwork.infrastructure.model.workbench.WorkbenchCard;
import com.foreveross.atwork.infrastructure.model.workbench.content.IWorkbenchCardContent;
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchListContent;
import com.foreveross.atwork.infrastructure.model.workbench.content.WorkbenchShortcutCardContent;
import com.foreveross.atwork.infrastructure.utils.ByteArrayToBase64TypeAdapter;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.Logger;
import com.foreveross.atwork.infrastructure.utils.LongUtil;
import com.foreveross.db.SQLiteDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

public class WorkbenchCardDataDbHelper implements DBHelper {

    private static final String TAG = WorkbenchCardDataDbHelper.class.getName();

    public static final String TABLE_NAME = "workbench_card_data_";



    /**
     * create table workbench_card_data_
     * ( widgets_id_ text,
     * details_ blob,
     * update_times_ integer,
     * primary key  ( widgets_id_) )
     */
    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.WIDGETS_ID + TEXT + COMMA +
            DBColumn.DETAILS + BLOB + COMMA +
            DBColumn.UPDATE_TIMES + INTEGER + COMMA +

            PRIMARY_KEY + LEFT_BRACKET +
            DBColumn.WIDGETS_ID +
            RIGHT_BRACKET + RIGHT_BRACKET;

    @Override
    public void onCreate(SQLiteDatabase db) {
        Logger.d(TAG, "sql = " + SQL_EXEC);
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(oldVersion < 20){
            try {
                db.execSQL(SQL_EXEC);
                oldVersion = 20;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    public static ContentValues getContentValue(IWorkbenchCardContent workbenchCardContent) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.WIDGETS_ID, workbenchCardContent.getWidgetsId());
        cv.put(DBColumn.UPDATE_TIMES, System.currentTimeMillis());

        Gson gson = new GsonBuilder().registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter()).create();
        byte[] data = gson.toJson(workbenchCardContent).getBytes();
        cv.put(DBColumn.DETAILS, data);

        return cv;
    }


    @Nullable
    public static IWorkbenchCardContent fromCursor(Cursor cursor, Workbench workbench) {

        IWorkbenchCardContent cardContent = null;
        long widgetsId = -1;
        Class<? extends IWorkbenchCardContent> classT = null;

        int index = -1;

        if((index = cursor.getColumnIndex(DBColumn.WIDGETS_ID)) != -1) {
            String widgetsIdStr = cursor.getString(index);
            widgetsId = LongUtil.parseLong(widgetsIdStr);

            List<WorkbenchCard> workbenchCards = workbench.findWorkbenchCard(widgetsId);

            if (!ListUtil.isEmpty(workbenchCards)) {
                switch (workbenchCards.get(0).getType()) {
                    case SHORTCUT_0:
                    case SHORTCUT_1:
                        classT = WorkbenchShortcutCardContent.class;
                        break;

                    case LIST_0:
                    case LIST_1:
                    case NEWS_0:
                    case NEWS_1:
                    case NEWS_2:
                    case NEWS_3:
                        classT = WorkbenchListContent.class;
                        break;


                }
            }
        }


        if(null == classT) {
            return null;
        }

        if ((index = cursor.getColumnIndex(DBColumn.DETAILS)) != -1) {
            byte[] data = cursor.getBlob(index);
            String dataStr = new String(data);

            Gson gson = new GsonBuilder().registerTypeAdapter(byte[].class, new ByteArrayToBase64TypeAdapter()).create();
            cardContent = gson.fromJson(dataStr, classT);

        }


        if (null != cardContent) {
            cardContent.setWidgetsId(widgetsId);
        }


        return cardContent;


    }


    public class DBColumn {

        static final String WIDGETS_ID = "widgets_id_";

        static final String DETAILS = "details_";

        static final String UPDATE_TIMES = "update_times_";



    }
}
