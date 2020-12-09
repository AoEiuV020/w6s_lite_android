package com.foreveross.atwork.modules.discussion.loader;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.foreverht.db.service.repository.DiscussionRepository;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;

import java.util.List;

/**
 * Created by lingen on 15/4/24.
 * Description:
 */
public class DiscussionListLoader extends AsyncTaskLoader<List<Discussion>> {
    public DiscussionListLoader(Context context) {
        super(context);
    }

    @Override
    public List<Discussion> loadInBackground() {
        return DiscussionRepository.getInstance().queryAllDiscussions();
    }
}
