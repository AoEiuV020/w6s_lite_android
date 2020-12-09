package com.foreveross.atwork.modules.bing.listener;

import com.foreveross.atwork.infrastructure.model.file.FileStatusInfo;

/**
 * Created by dasunsy on 2017/9/18.
 */

public interface UpdateFileDataListener {
    void update(FileStatusInfo fileStatusInfo);
}


