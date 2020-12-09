package com.foreveross.atwork.modules.friend.loader;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.foreverht.db.service.repository.UserRepository;
import com.foreveross.atwork.infrastructure.model.user.User;

import java.util.List;

/**
 * Created by lingen on 15/4/24.
 * Description:
 */
public class FriendListLoader extends AsyncTaskLoader<List<User>> {
    public FriendListLoader(Context context) {
        super(context);
    }

    @Override
    public List<User> loadInBackground() {
        return UserRepository.getInstance().queryFriendUsers();
    }
}
