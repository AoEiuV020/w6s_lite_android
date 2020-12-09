package com.foreveross.atwork.utils;

import android.content.Context;

import com.foreverht.db.service.EmpIncomingCallDatabaseHelper;
import com.foreverht.db.service.W6sDatabaseHelper;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.shared.LoginUserInfo;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.AtWorkDirUtils;
import com.foreveross.atwork.infrastructure.utils.FileUtil;
import com.foreveross.atwork.infrastructure.utils.LogUtil;
import com.foreveross.atwork.infrastructure.utils.encryption.EncryptHelper;

import java.io.File;

/**
 * Created by dasunsy on 2017/1/4.
 */

public class UserRemoveHelper {
    public static void clean(Context context) {

        //如果是用户不存在 则删除相关文件夹
        File file = new File(AtWorkDirUtils.getInstance().getUserDicPath(LoginUserInfo.getInstance().getLoginUserUserName(context)));
        if (file.exists()) {
            FileUtil.deleteFile(file, true);
        }

        W6sDatabaseHelper.deleteDb(context);
        EmpIncomingCallDatabaseHelper.deleteDb(context);

    }

    /**
     * 加密模式检查时, 用于清理旧的文件或者数据库
     * */
    public static void cleanAllUsers(Context context) {

        long startTime = System.currentTimeMillis();

        if (isDiskEncryptModeChange()) {
            File file = new File(AtWorkDirUtils.getInstance().getUserRootDicPath());
            if (file.exists()) {
                FileUtil.deleteFile(file, true);
            }

            LogUtil.e(EncryptHelper.TAG, "DELETE DISK");
        }

        if (isDbEncryptModeChange()) {
            String[] dbNameList = context.databaseList();
            for(String dbName : dbNameList) {
                context.deleteDatabase(dbName);
            }

            LogUtil.e(EncryptHelper.TAG, "DELETE DB");

        }

        long endTime = System.currentTimeMillis();

        LogUtil.e(EncryptHelper.TAG, "clean all data duration -> " + (endTime - startTime));


    }

    private static boolean isDbEncryptModeChange() {
        int encryptMode = CommonShareInfo.getEncryptMode(BaseApplicationLike.baseContext);
        return AtworkConfig.getDbEncryptionConfig() != (encryptMode & AtworkConfig.FLAG_OPEN_DB_ENCRYPTION);
    }

    private static boolean isDiskEncryptModeChange() {
        int encryptMode = CommonShareInfo.getEncryptMode(BaseApplicationLike.baseContext);
        return AtworkConfig.getDiskEncryptionConfig() != (encryptMode & AtworkConfig.FLAG_OPEN_DISK_ENCRYPTION);
    }


    public static boolean isEncryptModeMatch(Context ctx) {
        return AtworkConfig.getEncryptModeConfig() == CommonShareInfo.getEncryptMode(ctx);
    }

    public static boolean isUserEncryptModeMatch(Context context, String username) {
        return AtworkConfig.getEncryptModeConfig() == PersonalShareInfo.getInstance().getEncryptMode(context, username);
    }

    public static boolean isCurrentUserEncryptModeMatch(Context context) {
        return isUserEncryptModeMatch(context, LoginUserInfo.getInstance().getLoginUserUserName(context));
    }
}
