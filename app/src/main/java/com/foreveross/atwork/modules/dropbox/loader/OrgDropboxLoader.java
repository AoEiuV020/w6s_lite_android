package com.foreveross.atwork.modules.dropbox.loader;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.foreverht.db.service.repository.DiscussionRepository;
import com.foreverht.db.service.repository.OrganizationRepository;
import com.foreveross.atwork.R;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.google.gson.internal.LinkedTreeMap;

import java.util.List;
import java.util.Map;

/**
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@%((((((((#&@@@((#((#@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@%(((((((((((((%@*((((@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@%(((/,/(((((((((@@@*#@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@#(((@@@((((((((((@@((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@(((((((((((((((((((((@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@(((((((((((((((((((@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@(((((((((((((((*@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
 * @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ __           __
 * .--.--.--.-----.----.|  |--.-----.|  |.--.--.-----.
 * |  |  |  |  _  |   _||    <|  _  ||  ||  |  |__ --|
 * |________|_____|__|  |__|__|   __||__||_____|_____|
 * |__|
 * Created by reyzhang22 on 2016/10/1.
 */

public class OrgDropboxLoader extends AsyncTaskLoader<Map<String, List<Discussion>>> {

    public static final String PUBLIC_ID = "PUBLIC_ID";

    private Context mContext;

    public OrgDropboxLoader(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public Map<String, List<Discussion>> loadInBackground() {
        Map<String, List<Discussion>> orgDropboxMapping = new LinkedTreeMap<>();
        //先查询所有的组织id
        List<String> orgCodeList = OrganizationRepository.getInstance().queryAllOrgCodeList();
        for (String orgCode : orgCodeList) {
            Discussion discussion = new Discussion();
            discussion.mName = mContext.getString(R.string.public_area);
            discussion.mDiscussionId = PUBLIC_ID;
            discussion.setOrgId(orgCode);
            List<Discussion> discussionList = DiscussionRepository.getInstance().queryDiscussionsByOrgId(orgCode);
            discussionList.add(0, discussion);
            orgDropboxMapping.put(orgCode, discussionList);
        }
        return orgDropboxMapping;
    }
}
