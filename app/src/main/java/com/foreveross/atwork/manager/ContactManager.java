package com.foreveross.atwork.manager;

import android.content.Context;

import com.foreverht.db.service.repository.RelationshipRepository;
import com.foreveross.atwork.api.sdk.users.UserAsyncNetService;
import com.foreveross.atwork.db.daoService.UserDaoService;
import com.foreveross.atwork.infrastructure.BaseApplicationLike;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.post.notify.ContactNotifyMessage;
import com.foreveross.atwork.modules.contact.fragment.ContactFragment;
import com.foreveross.atwork.utils.ErrorHandleUtil;

/**
 * Created by dasunsy on 16/7/6.
 */
public class ContactManager {

    public static void receiveContactNotify(ContactNotifyMessage contactNotifyMessage, boolean isCameFromOnline) {
        Context context = BaseApplicationLike.baseContext;

        if(ContactNotifyMessage.Operation.ADDED.equals(contactNotifyMessage.mOperation)) {
            RelationshipRepository.getInstance().addFlagRelationShip(contactNotifyMessage.mContact.mUserId);

            UserManager.getInstance().asyncQueryUserInfoFromRemote(context, contactNotifyMessage.mContact.mUserId, contactNotifyMessage.mContact.mDomainId, new UserAsyncNetService.OnUserCallBackListener() {
                @Override
                public void onFetchUserDataSuccess(Object... object) {
                    User user = (User) object[0];
                    if(null != user) {
                        UserDaoService.getInstance().insertUser(user);

                        //update online status
                        OnlineManager.getInstance().setOnlineStatus(user.mUserId, user.isOnline());

                        ContactFragment.contactsDataChanged(context);
                    }

                }

                @Override
                public void networkFail(int errorCode, String errorMsg) {
                    ErrorHandleUtil.handleTokenError(errorCode, errorMsg);
                }
            });

        } else if(ContactNotifyMessage.Operation.REMOVED.equals(contactNotifyMessage.mOperation)) {
            RelationshipRepository.getInstance().removeFlagRelationShip(contactNotifyMessage.mContact.mUserId);
            ContactFragment.contactsDataChanged(context);

        }

    }
}
