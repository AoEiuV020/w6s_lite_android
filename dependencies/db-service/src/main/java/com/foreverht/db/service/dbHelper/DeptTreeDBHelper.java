package com.foreverht.db.service.dbHelper;

import android.content.ContentValues;

import com.foreverht.db.service.W6sBaseRepository;
import com.foreveross.atwork.infrastructure.model.orgization.DepartmentTree;
import com.foreveross.db.BaseDatabaseHelper;
import com.foreveross.db.SQLiteDatabase;

public class DeptTreeDBHelper implements DBHelper {

    private static final String TAG = DeptTreeDBHelper.class.getSimpleName();

    public static final String TABLE_NAME = "dept_tree_";

    /**
     * create table dept_tree_ (
     * id_ text, parent_id_ text ,type_ text ,
     * sort_ integer, query_ integer,
     * primary key  ( id_, parent_id_, query_)  )
     */

    private static final String SQL_EXEC = CREATE_TABLE + TABLE_NAME + LEFT_BRACKET +
            DBColumn.ID + TEXT + COMMA +
            DBColumn.PARENT_ID + TEXT + COMMA +
            DBColumn.TYPE + TEXT + COMMA +
            DBColumn.SORT + INTEGER + COMMA +
            DBColumn.QUERY+ INTEGER + COMMA +
            PRIMARY_KEY + LEFT_BRACKET +
            DBColumn.ID + COMMA + DBColumn.PARENT_ID + COMMA + DBColumn.QUERY +
            RIGHT_BRACKET + RIGHT_BRACKET;



    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_EXEC);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if(500 > oldVersion) {
            BaseDatabaseHelper.createTable(db, SQL_EXEC);
            oldVersion = 500;
        }
    }

    public static ContentValues getContentValue(DepartmentTree departmentTree) {
        ContentValues cv = new ContentValues();
        cv.put(DBColumn.ID, departmentTree.getId());
        cv.put(DBColumn.PARENT_ID, departmentTree.getParentId());
        cv.put(DBColumn.TYPE, departmentTree.getType());
        cv.put(DBColumn.SORT, departmentTree.getSort());
        cv.put(DBColumn.QUERY, departmentTree.getQuery());

        return cv;
    }

    public static String getDetailDBColumn(String column) {
        return W6sBaseRepository.getDetailDBColumn(TABLE_NAME, column);
    }


    public class DBColumn {

        public static final String ID = "id_";

        public static final String PARENT_ID = "parent_id_";

        public static final String TYPE = "type_";

        public static final String SORT = "sort_";

        public static final String QUERY = "query_";



    }
}
