package com.foreveross.atwork.modules.friend.utils;

import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.utils.FirstLetterUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import java.util.LinkedHashSet;
import java.util.List;

/**
 * Created by dasunsy on 2016/11/7.
 */

public class FriendLetterListHelper {


    public static String[] getFirstLetterLinkedSet(List<? extends ShowListItem> contactList) {
        LinkedHashSet<String> letterSet = new LinkedHashSet<>();

        boolean hasUnLetter = false;

        for (ShowListItem contact : contactList) {
            String firstLetter;

            if (!(StringUtils.isEmpty(contact.getTitlePinyin()))) {
                firstLetter = (String) contact.getTitlePinyin().subSequence(0, 1);

            } else {
                firstLetter = FirstLetterUtil.getFirstLetter(contact.getTitle());
            }

            //如果非字母, 则用 "#"
            if (!FirstLetterUtil.isLetter(firstLetter)) {
                hasUnLetter = true;

            } else {
                letterSet.add(firstLetter.toUpperCase());

            }


        }

        if (hasUnLetter) {
            letterSet.add("#");
        }

        return letterSet.toArray(new String[]{});
    }

}
