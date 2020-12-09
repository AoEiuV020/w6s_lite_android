package com.foreveross.atwork.infrastructure.utils;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.Session;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.discussion.Discussion;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.model.user.UserHandleInfo;
import com.foreveross.atwork.infrastructure.model.voip.VoipMeetingMember;
import com.foreveross.atwork.infrastructure.shared.PersonalShareInfo;
import com.foreveross.atwork.infrastructure.support.AtworkConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by dasunsy on 16/6/22.
 */
public class ContactHelper {

    public static int getContactViewMode(Context context) {
        if(-1 != AtworkConfig.CONTACT_CONFIG.getOnlyVersionContactViewMode()) {
            return AtworkConfig.CONTACT_CONFIG.getOnlyVersionContactViewMode();
        }

        return PersonalShareInfo.getInstance().getContactViewMode(context);

    }


    public static boolean isDiscussionType(ShowListItem contact) {
        if(contact instanceof Discussion) {
            return true;
        }

        if(contact instanceof Session && ((Session) contact).isDiscussionType()) {
            return true;
        }

        return false;
    }

    public static void sortContactWithReadTime(List<? extends ShowListItem> contacts) {
        Collections.sort(contacts, new Comparator<ShowListItem>() {
            @Override
            public int compare(ShowListItem o1, ShowListItem o2) {
                long readTime1 = ContactHelper.getReadTime(o1);
                long readTime2 = ContactHelper.getReadTime(o2);

                if(readTime1 == readTime2) {
                    return 0;
                }

                if(0 < readTime1 - readTime2) {
                    return -1;
                }

                return 1;

            }
        });
    }


    public static long getReadTime(ShowListItem contact) {
        if (contact instanceof User) {
            return ((User) contact).readTime;

        } else if (contact instanceof Employee) {
            return ((Employee) contact).mReadTime;

        }

        return -1;
    }

    public static String getUserName(ShowListItem contact) {
        if (contact instanceof User) {
            return ((User) contact).mUsername;

        } else if (contact instanceof Employee) {
            return ((Employee) contact).username;

        }

        return StringUtils.EMPTY;
    }

    public static void sort(List<? extends ShowListItem> contactList) {

        Collections.sort(contactList, new Comparator<ShowListItem>() {
            @Override
            public int compare(ShowListItem o1, ShowListItem o2) {
                if (StringUtils.isEmpty(o1.getTitlePinyin()) && StringUtils.isEmpty(o2.getTitlePinyin())) {
                    return o1.getTitle().compareTo(o2.getTitle());
                }

                if (StringUtils.isEmpty(o1.getTitlePinyin()) && !StringUtils.isEmpty(o2.getTitlePinyin())) {
                    return 1;
                }

                if (!StringUtils.isEmpty(o1.getTitlePinyin()) && StringUtils.isEmpty(o2.getTitlePinyin())) {
                    return -1;
                }

                return o1.getTitlePinyin().compareTo(o2.getTitlePinyin());

            }
        });

    }

    public static List<String> toIdList(List<? extends ShowListItem> contactList) {
        List<String> userIdList = new ArrayList<>();

        for (ShowListItem contact : contactList) {
            if (null != contact) {
                userIdList.add(contact.getId());
            }
        }

        return userIdList;
    }


    @NonNull
    public static List<UserHandleInfo> transferContactList(List<? extends ShowListItem> contactList) {
        List<UserHandleInfo> userHandleInfoList = new ArrayList<>();
        for (ShowListItem contact : contactList) {
            userHandleInfoList.add(ContactHelper.toUserHandleInfo(contact));
        }

        return userHandleInfoList;
    }


    /**
     * 转换 附带基本信息 的voip member
     */
    @NonNull
    public static VoipMeetingMember toBasicVoipMeetingMember(ShowListItem contact) {
        VoipMeetingMember basicVoipMeetingMember = new VoipMeetingMember();
        basicVoipMeetingMember.mUserId = contact.getId();
        basicVoipMeetingMember.mDomainId = contact.getDomainId();
        basicVoipMeetingMember.mShowName = contact.getParticipantTitle();
        basicVoipMeetingMember.mAvatar = contact.getAvatar();

        return basicVoipMeetingMember;
    }


    /**
     * 转换基本的 userHandleInfo
     */
    @NonNull
    public static UserHandleInfo toUserHandleInfo(ShowListItem contact) {
        UserHandleInfo userHandleInfo = new UserHandleInfo();
        userHandleInfo.mUserId = contact.getId();
        userHandleInfo.mDomainId = contact.getDomainId();
        userHandleInfo.mShowName = contact.getParticipantTitle();
        userHandleInfo.mAvatar = contact.getAvatar();
        userHandleInfo.mStatus = contact.getStatus();

        return userHandleInfo;
    }


    /**
     * 查找当前登录用户
     * */
    @Nullable
    public static ShowListItem findLoginUserContact(Context context, ArrayList<ShowListItem> contactList) {
        for(ShowListItem contact : contactList) {
            if(User.isYou(context, contact.getId())) {
                return contact;
            }
        }

        return null;
    }

    @Nullable
    public static UserHandleInfo findLoginUserHandleInfo(Context context, ArrayList<ShowListItem> contactList) {
        ShowListItem contact = findLoginUserContact(context, contactList);
        if(null != contact) {
            return toUserHandleInfo(contact);
        }

        return null;
    }


    public static boolean containsContact(List<? extends ShowListItem> contactList, ShowListItem contact) {
        for(ShowListItem contactIn : contactList) {
            if(contactIn.getId().equals(contact.getId())) {
                return true;
            }
        }


        return false;
    }




    public static void addContact(List<ShowListItem> contactList, ShowListItem contact) {

        if(!containsContact(contactList, contact)) {
            contactList.add(contact);
        }
    }


    public static void addContacts(List<ShowListItem> contactList, List<ShowListItem> contactAddList) {

        for(ShowListItem contactAdd : contactAddList) {
            addContact(contactList, contactAdd);
        }
    }


    public static void removeContact(List<? extends ShowListItem> contactList, ShowListItem contactRemove) {
        removeContacts(contactList, ListUtil.makeSingleList(contactRemove));
    }

    public static void removeContacts(List<? extends ShowListItem> contactList, List<? extends ShowListItem> contactRemoveList) {


        List<ShowListItem> contactReallyRemovedList = new ArrayList<>();


        for(ShowListItem contactRemove : contactRemoveList) {

            for(ShowListItem contactIn : contactList) {
                if(contactIn.getId().equals(contactRemove.getId())) {
                    contactReallyRemovedList.add(contactIn);
                    break;
                }
            }

        }

        contactList.removeAll(contactReallyRemovedList);
    }


    public static List<ShowListItem> filterDuplicated(List<? extends ShowListItem> contactList) {

        List<ShowListItem> bucketList = new ArrayList<>();

        for(int i = 0; i < contactList.size(); i++) {
            ShowListItem contact = contactList.get(i);

            if (!bucketList.contains(contact)) {
                bucketList.add(contact);
            }
        }

        return bucketList;

    }
}
