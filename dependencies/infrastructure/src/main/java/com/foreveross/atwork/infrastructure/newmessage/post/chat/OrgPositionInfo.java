package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.utils.ArrayUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.google.gson.annotations.Expose;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class OrgPositionInfo implements Serializable {
    @Expose
    public String jobTitle = StringUtils.EMPTY;

    @Expose
    public String[] orgDeptInfos = new String[0];

    public boolean isLegal() {
        return !StringUtils.isEmpty(jobTitle) || !ArrayUtil.isEmpty(orgDeptInfos);
    }

    @Nullable
    public static OrgPositionInfo parse(Map<String, Object> bodyMap) {
        if (bodyMap.containsKey(ChatPostMessage.ORG_POSITION)) {
            OrgPositionInfo orgPositionInfo = new OrgPositionInfo();
            orgPositionInfo.jobTitle = ChatPostMessage.getString(bodyMap, ChatPostMessage.ORG_POSITION);

            if(bodyMap.containsKey(ChatPostMessage.ORG_DEPT_INFOS)) {
                List<String> orgDeptInfoList = ChatPostMessage.getStringList(bodyMap, ChatPostMessage.ORG_DEPT_INFOS);
                if(null != orgDeptInfoList) {
                    orgPositionInfo.orgDeptInfos = orgDeptInfoList.toArray(new String[0]);

                }
            }


            return orgPositionInfo;
        }

        return null;
    }
}
