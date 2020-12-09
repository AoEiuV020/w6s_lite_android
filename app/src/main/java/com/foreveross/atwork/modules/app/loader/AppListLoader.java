package com.foreveross.atwork.modules.app.loader;


import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.foreverht.db.service.repository.AppRepository;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.app.App;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;

import java.util.ArrayList;
import java.util.List;


public class AppListLoader extends AsyncTaskLoader<List<App>> {

    public AppListLoader(Context context) {
        super(context);
    }

    @Override
    public List<App> loadInBackground() {
        List<App> allServiceAapList = AppRepository.getInstance().queryAccessServiceApps(PersonalShareInfo.getInstance().getCurrentOrg(BaseApplicationLike.baseContext));

        //过滤掉"隐藏"的 app
        List<App> showServiceAppList = new ArrayList<>();
        for(App app : allServiceAapList) {
            if(app.isShowInMarket()) {
                showServiceAppList.add(app);
            }
        }

        return showServiceAppList;
    }
}
