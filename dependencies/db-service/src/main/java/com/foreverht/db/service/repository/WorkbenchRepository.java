package com.foreverht.db.service.repository;

import android.database.Cursor;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreverht.db.service.dbHelper.WorkbenchCardDataDbHelper;
import com.foreveross.atwork.infrastructure.model.workbench.Workbench;
import com.foreveross.atwork.infrastructure.model.workbench.content.IWorkbenchCardContent;
import com.foreveross.db.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class WorkbenchRepository extends W6sBaseRepository {


    public static List<? extends IWorkbenchCardContent> queryWorkbenchCardContents(Workbench workbench) {

        String sql = "select * from " + WorkbenchCardDataDbHelper.TABLE_NAME;

        List<IWorkbenchCardContent> workbenchCardContents = new ArrayList<>();

        Cursor cursor = null;
        try {
            cursor = getReadableDatabase().rawQuery(sql, new String[]{});
            while (cursor.moveToNext()) {
                IWorkbenchCardContent workbenchCardContent = WorkbenchCardDataDbHelper.fromCursor(cursor, workbench);
                if (null != workbenchCardContent) {
                    workbenchCardContents.add(workbenchCardContent);
                }

            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return workbenchCardContents;
    }


    public static boolean insertOrUpdateWorkbenchCardContent(IWorkbenchCardContent workbenchCardContent) {
        long result = getWritableDatabase().insertWithOnConflict(
                WorkbenchCardDataDbHelper.TABLE_NAME,
                null,
                WorkbenchCardDataDbHelper.getContentValue(workbenchCardContent), SQLiteDatabase.CONFLICT_REPLACE);

        if (result == -1) {
            return false;
        }

        return true;

    }

}
