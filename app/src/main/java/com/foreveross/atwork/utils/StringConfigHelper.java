package com.foreveross.atwork.utils;

import android.content.Context;

import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.manager.DomainSettingsManager;
import com.foreveross.atwork.infrastructure.shared.CommonShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dasunsy on 16/8/30.
 */
public class StringConfigHelper {
    public static String getMsgNotForward(Context context, boolean isItemByItem) {

        if(isItemByItem) {
            if(DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
                return context.getString(R.string.msg_not_forward_item_by_item_file_permission_mode);
            }

            return context.getString(R.string.msg_not_forward_item_by_item_file_common_mode);

        }

        if(DomainSettingsManager.getInstance().handleChatFileTransferEnabled()) {
            return context.getString(R.string.msg_not_forward_multipart_file_permission_mode);
        }

        return context.getString(R.string.msg_not_forward_multipart_file_common_mode);

    }


    public static String getAuthCameraFunction(Context context) {
        if(AtworkConfig.OPEN_VOIP) {
            return context.getString(R.string.auth_camera_function, "、" + context.getString(R.string.voip_voice_meeting));

        } else {
            return context.getString(R.string.auth_camera_function, StringUtils.EMPTY);

        }
    }

    public static String getAuthRecordFunction(Context context) {
        if(AtworkConfig.OPEN_VOIP) {
            return context.getString(R.string.auth_record_function, "、" + context.getString(R.string.voip_voice_meeting));

        } else {
            return context.getString(R.string.auth_record_function, StringUtils.EMPTY);

        }
    }


    public static String getAuthPhotoStateFunction(Context context) {
        List<String> functionNameList = new ArrayList<>();
        StringBuilder stringBuilder = new StringBuilder();

        if(AtworkConfig.OPEN_VOIP) {
            functionNameList.add(context.getString(R.string.voip_voice_meeting));
        }

        if(CommonShareInfo.getVpnPermissionHasShown(context)) {
            functionNameList.add(context.getString(R.string.auth_function_vpn));
        }

        for(int i = 0; i < functionNameList.size(); i++) {
            if(0 != i) {
                stringBuilder.append("、");
            }

            stringBuilder.append(functionNameList.get(i));

        }

        return stringBuilder.toString();
    }
}
