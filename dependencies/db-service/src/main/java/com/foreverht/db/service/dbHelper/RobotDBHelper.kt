package com.foreverht.db.service.dbHelper

import android.content.ContentValues
import android.database.Cursor
import com.foreveross.atwork.infrastructure.model.robot.RobotData
import com.foreveross.db.BaseDatabaseHelper
import com.foreveross.db.SQLiteDatabase

class RobotDBHelper : DBHelper{

    private val TAG = RobotDBHelper::class.java.simpleName

    companion object {
        val TABLE_NAME = "robot_"
        /**
         * 获取数据库中discussionCategoryInfo的对象
         * @param cursor
         *
         * @return
         */
        fun fromCursor(cursor : Cursor) : RobotData {
            val discussionCategoryInfo = RobotData()
            val idInt = cursor.getColumnIndex(DBColumn.ORDER_ID)
            if (idInt != -1) {
                discussionCategoryInfo.id = cursor.getString(idInt)
            }
            val domainIdInt = cursor.getColumnIndex(DBColumn.DOMAIN_ID)
            if (domainIdInt != -1) {
                discussionCategoryInfo.domainId = cursor.getString(domainIdInt)
            }
            val keyInt = cursor.getColumnIndex(DBColumn.KEY)
            if (keyInt != -1) {
                discussionCategoryInfo.key = cursor.getString(keyInt)
            }
            val instructionInt = cursor.getColumnIndex(DBColumn.INSTRUCTION)
            if (instructionInt != -1) {
                discussionCategoryInfo.instruction = cursor.getString(instructionInt)
            }
            val ownerIdInt = cursor.getColumnIndex(DBColumn.OWNER_ID)
            if (ownerIdInt != -1) {
                discussionCategoryInfo.ownerId = cursor.getString(ownerIdInt)
            }
            val ownerNameInt = cursor.getColumnIndex(DBColumn.OWNER_NAME)
            if (ownerNameInt != -1) {
                discussionCategoryInfo.ownerName = cursor.getString(ownerNameInt)
            }
            val createTimeInt = cursor.getColumnIndex(DBColumn.CREATE_TIME)
            if (createTimeInt != -1) {
                discussionCategoryInfo.createTime = cursor.getLong(createTimeInt)
            }
            val modifyTimeInt = cursor.getColumnIndex(DBColumn.MODIFY_TIME)
            if (modifyTimeInt != -1) {
                discussionCategoryInfo.modifyTime = cursor.getLong(modifyTimeInt)
            }
            val disabledInt = cursor.getColumnIndex(DBColumn.DISABLED)
            if (disabledInt != -1) {
                discussionCategoryInfo.disabled = cursor.getString(disabledInt)
            }
            val deletedInt = cursor.getColumnIndex(DBColumn.DELETED)
            if (deletedInt != -1) {
                discussionCategoryInfo.deleted = cursor.getString(deletedInt)
            }
            val prefixInt = cursor.getColumnIndex(DBColumn.PREFIX)
            if (deletedInt != -1) {
                discussionCategoryInfo.prefix = cursor.getString(prefixInt)
            }
            val suffixInt = cursor.getColumnIndex(DBColumn.SUFFIX)
            if (deletedInt != -1) {
                discussionCategoryInfo.suffix = cursor.getString(suffixInt)
            }
            return discussionCategoryInfo
        }

        /**
         * 组建存数据库字段
         * @param discussionCategoryInfo
         * @return
         */
        fun getContentValues(discussionCategoryInfo: RobotData): ContentValues {
            val cv = ContentValues()
            cv.put(DBColumn.ORDER_ID, discussionCategoryInfo.id)
            cv.put(DBColumn.DOMAIN_ID, discussionCategoryInfo.domainId)
            cv.put(DBColumn.KEY, discussionCategoryInfo.key)
            cv.put(DBColumn.INSTRUCTION, discussionCategoryInfo.instruction)
            cv.put(DBColumn.OWNER_ID, discussionCategoryInfo.ownerId)
            cv.put(DBColumn.OWNER_NAME, discussionCategoryInfo.ownerName)
            cv.put(DBColumn.CREATE_TIME, discussionCategoryInfo.createTime)
            cv.put(DBColumn.MODIFY_TIME, discussionCategoryInfo.modifyTime)
            cv.put(DBColumn.DISABLED, discussionCategoryInfo.disabled)
            cv.put(DBColumn.DELETED, discussionCategoryInfo.deleted)
            cv.put(DBColumn.PREFIX, discussionCategoryInfo.prefix)
            cv.put(DBColumn.SUFFIX, discussionCategoryInfo.suffix)
            return cv
        }
    }
    /**
     * create table discussion_category_info_
     * (category_id_ text  primary key ,category_name_ text ,sort_ text ,type_ text )
     */
    private val SQL_EXEC = DBHelper.CREATE_TABLE + TABLE_NAME + DBHelper.LEFT_BRACKET +
            DBColumn.ORDER_ID + DBHelper.TEXT + DBHelper.PRIMARY_KEY + DBHelper.COMMA +
            DBColumn.DOMAIN_ID + DBHelper.TEXT + DBHelper.COMMA +
            DBColumn.KEY + DBHelper.TEXT + DBHelper.COMMA +
            DBColumn.INSTRUCTION + DBHelper.TEXT + DBHelper.COMMA +
            DBColumn.OWNER_ID + DBHelper.TEXT + DBHelper.COMMA +
            DBColumn.OWNER_NAME + DBHelper.TEXT + DBHelper.COMMA +
            DBColumn.CREATE_TIME + DBHelper.TEXT + DBHelper.COMMA +
            DBColumn.MODIFY_TIME + DBHelper.TEXT + DBHelper.COMMA +
            DBColumn.DISABLED + DBHelper.TEXT + DBHelper.COMMA +
            DBColumn.PREFIX + DBHelper.TEXT + DBHelper.COMMA +
            DBColumn.SUFFIX + DBHelper.TEXT + DBHelper.COMMA +
            DBColumn.DELETED + DBHelper.TEXT + DBHelper.RIGHT_BRACKET

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_EXEC)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        var version = oldVersion
        if (200 > version) {
            BaseDatabaseHelper.createTable(db, SQL_EXEC)

            version = 200
        }
    }

    object DBColumn {
        //orderId
        const val ORDER_ID = "order_id_"
        //域id
        const val DOMAIN_ID = "domain_id_"
        //关键字
        const val KEY = "key_"
        //路由地址
        const val INSTRUCTION = "instruction_"
        //应用ID
        const val OWNER_ID = "owner_id_"
        //应用名称
        const val OWNER_NAME = "owner_name_"
        //创建时间
        const val CREATE_TIME = "create_time_"
        //修改时间
        const val MODIFY_TIME = "modify_time_"
        //是否失效
        const val DISABLED = "disabled_"
        //是否删除
        const val DELETED = "deleted_"
        //搜索前缀
        const val PREFIX = "prefix_"
        //搜索后缀
        const val SUFFIX = "suffix_"
    }
}