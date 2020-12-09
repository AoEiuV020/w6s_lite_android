package com.foreveross.atwork.infrastructure.newmessage.post.chat;

import android.content.Context;

public interface IAtContactMessage {

    boolean isAtMe(Context context);

    boolean containAtMe(Context context);

    String getContent();
}
