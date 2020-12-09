package com.foreveross.atwork.modules.contact.loader;

import android.content.Context;

import androidx.loader.content.AsyncTaskLoader;

import com.foreverht.db.service.repository.UserRepository;
import com.foreveross.atwork.infrastructure.model.user.User;

import java.util.List;

/**
 * Created by lingen on 15/4/14.
 * Description:
 */
public class UserListLoader extends AsyncTaskLoader<List<User>> {

    public UserListLoader(Context context) {
        super(context);
    }

    @Override
    public List<User> loadInBackground() {
        return UserRepository.getInstance().queryStarFlagUsers();
    }

}
