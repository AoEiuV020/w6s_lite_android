package com.foreveross.atwork.infrastructure.model;


import android.content.Context;
import android.os.Parcel;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.foreveross.atwork.infrastructure.model.chat.SystemChatMessage;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.newmessage.ChatStatus;
import com.foreveross.atwork.infrastructure.newmessage.post.ChatPostMessage;
import com.foreveross.atwork.infrastructure.utils.StringUtils;
import com.foreveross.atwork.infrastructure.utils.TimeUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class Session implements ShowListItem {


    /**
     * 系统通知
     */
    public static final String WORKPLUS_SYSTEM = "workplus_system";

    /**
     * 交易通知
     */
    public static final String ASSET_NOTIFY_SYSTEM = "real_workplus_system";

    /**
     * 会议通知(umeeting/ qsy)
     */
    public static final String WORKPLUS_MEETING = "workplus_meeting";


    /**
     * 群聊助手
     * */
    public static final String WORKPLUS_DISCUSSION_HELPER = "discussion_conversations_helper";

    /**
     * 服务号汇总
     * */
    public static final String WORKPLUS_SUMMARY_HELPER = "news_summary_helper";

    /**
     * 内宣号
     * */
    public static final String COMPONENT_ANNOUNCE_APP = "component_announce_app";

    /**
     * 邮箱
     * */
    public static final String EMAIL_APP_ID = "workplus_email_id";

    /**
     * 网盘过期提醒
     */
    public static final String DROPBOX_OVERDUE_REMIND = "dropbox_overdue_remind";

    public static final String FAKE_UNREAD_ID_TO_NOTICE = "notice_unread";

    /**
     * ID
     */
    public String identifier;

    public String mDomainId;

    /**
     * 聊天类型
     */
    public SessionType type;


    /**
     * 名称
     */
    public String name;

    public String avatar;

    /**
     * 组织 id
     */
    public String orgId;


    public String lastMessageId;

    public String atMessageId;

    public String lastAtMessageText;

    /**
     * 最后一条消息类型
     */
    public String lastMessageText;

    /**
     * 最后一条消息状态
     */
    public ChatStatus lastMessageStatus;

    /**
     * 消息显示类型
     */
    public ShowType lastMessageShowType;


    public long lastTimestamp;

    /**
     * 是否置顶
     */
    public int top;

    /**
     * 设置的智能翻译语种
     */
    public String language;


    /**
     * 是否屏蔽通知
     */
    public int shield;

    /**
     * 草稿
     */
    public String draft;


    /**
     * 未读消息 id  set
     */
    public Set<String> unreadMessageIdSet = Collections.synchronizedSet(new HashSet<String>());
    /**
     * 未读消息被转换成已读
     */
    public Set<String> unreadTransferIdSet = Collections.synchronizedSet(new HashSet<String>());

    /**
     * 未读消息 id set, 标记已经处于数据库的
     * */
    public Set<String> unreadInDbSet = Collections.synchronizedSet(new HashSet<String>());

    /**
     * 消息跳转方式
     */
    public EntryType entryType;

    /**
     * 跳转值
     */
    public String entryValue;

    public String entryId;

    /**
     * 消息提醒类型
     */
    public NewMessageType newMessageType;

    /**
     * 提醒内容
     */
    public byte[] newMessageContent;

    /**
     * 是否已存入数据库
     */
    public boolean savedToDb = false;

    public boolean visible = false;

    public boolean mSelect;


    public static int compareToByDraft(@NonNull Session thisSession, @Nullable Long thisLastTimestamp, @NonNull Session otherSession, @Nullable Long anotherLastTimestamp) {
        int thisDraftRank = 0;
        int anotherDraftRank = 0;
        if (!StringUtils.isEmpty(thisSession.draft)) {
            thisDraftRank = 1;
        }

        if (!StringUtils.isEmpty(otherSession.draft)) {
            anotherDraftRank = 1;
        }

        if (thisDraftRank == anotherDraftRank) {

            if (null != thisLastTimestamp && null != anotherLastTimestamp) {
                return TimeUtil.compareToReverted(thisLastTimestamp, anotherLastTimestamp);

            }
            return TimeUtil.compareToReverted(thisSession.lastTimestamp, otherSession.lastTimestamp);
        }

        return anotherDraftRank - thisDraftRank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Session session = (Session) o;

        return identifier != null ? identifier.equals(session.identifier) : session.identifier == null;
    }

    @Override
    public int hashCode() {
        return identifier != null ? identifier.hashCode() : 0;
    }


    public boolean isSessionsFold() {
        switch (identifier){
            case COMPONENT_ANNOUNCE_APP:
                return true;
        }

        return false;

    }

    public boolean havingUnread() {
        return 0 < getUnread();
    }


    public int getUnread() {
        if (1 < unreadMessageIdSet.size() && unreadMessageIdSet.contains(FAKE_UNREAD_ID_TO_NOTICE)) {
            return unreadMessageIdSet.size() - 1;

        }
        return unreadMessageIdSet.size();
    }

    public void clearUnread() {

        unreadTransferIdSet.addAll(unreadMessageIdSet);

        unreadMessageIdSet.clear();


    }

    public void refreshUnreadSetTotally(Set<String> unreadSet, boolean fromDb) {
        unreadMessageIdSet.clear();
        unreadMessageIdSet.addAll(unreadSet);

        if(fromDb) {
            unreadInDbSet.clear();
            unreadInDbSet.addAll(unreadSet);
        }
    }

    /**
     * 插入非消息的未读, 消息 ID 用 uuid 代替, 如邮件推送这些不依赖 IM 消息的场景
     */
    public void addRandomUnread() {
        unreadMessageIdSet.add(UUID.randomUUID().toString());
    }


    /**
     * 插入未读提醒, 消息 ID 使用 {@link #FAKE_UNREAD_ID_TO_NOTICE}
     */
    public void addFakeNoticeUnread() {
        unreadMessageIdSet.add(FAKE_UNREAD_ID_TO_NOTICE);
    }

    public void addUnread(String msgId) {
        //正常渠道增加的未读, 若存在"手动的未读提醒", 需清除处理
        dismissUnread(FAKE_UNREAD_ID_TO_NOTICE);

        unreadMessageIdSet.add(msgId);
    }

    public void dismissUnread(String checkMsgId) {
        if (unreadMessageIdSet.contains(checkMsgId)) {
            unreadMessageIdSet.remove(checkMsgId);
            unreadTransferIdSet.add(checkMsgId);
        }
    }


    public boolean isUserType() {
        return SessionType.User.equals(type);

    }

    public boolean isDiscussionType() {
        return SessionType.Discussion.equals(type);

    }

    public boolean isNoticeType(){
        return SessionType.Notice.equals(type);
    }

    public boolean isAppType() {
        return (SessionType.LightApp.equals(type)
                || SessionType.Service.equals(type)
                || SessionType.NativeApp.equals(type)
                || SessionType.SystemApp.equals(type)
                || SessionType.Local.equals(type));
    }


    public boolean isRemoteTop() {
        return SessionTop.REMOTE_TOP == top;
    }

    public boolean isNoneTop() {
        return SessionTop.NONE == top;
    }

    public boolean isMeetingSession() {
        return SessionType.Notice.equals(type) && Session.WORKPLUS_MEETING.equals(identifier);
    }

    public void setLastMessageStatus(Context context, ChatPostMessage message) {
        if (message instanceof SystemChatMessage) {
            lastMessageStatus = ChatStatus.Sended;
            return;
        }


        if(message.chatStatus == ChatStatus.Sended
                && User.isYou(context, message.from)) {

            if (ChatStatus.Peer_Read != lastMessageStatus) {
                lastMessageStatus = ChatStatus.Self_Send;
            }

        } else {
            lastMessageStatus = message.chatStatus;

        }

    }

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public String getTitleI18n(Context context) {
        return getTitle();
    }

    @Override
    public String getTitlePinyin() {
        return null;
    }

    @Override
    public String getParticipantTitle() {
        return name;
    }

    @Override
    public String getInfo() {
        return lastMessageText;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }

    @Override
    public String getId() {
        return identifier;
    }

    @Override
    public String getDomainId() {
        return mDomainId;
    }

    @Override
    public String getStatus() {
        return null;
    }

    @Override
    public boolean isSelect() {
        return mSelect;
    }

    @Override
    public void select(boolean isSelect) {
        mSelect = isSelect;
    }

    @Override
    public boolean isOnline() {
        return false;
    }

    public enum ShowType {
        Text {
            @Override
            public int intValue() {
                return 0;
            }
        },

        At {
            @Override
            public int intValue() {
                return 1;
            }
        },

        Draft {
            @Override
            public int intValue() {
                return 2;
            }
        },

        Audio {
            @Override
            public int intValue() {
                return 3;
            }
        },

        Emergency {
            @Override
            public int intValue() {
                return 4;
            }
        },
        RedEnvelope {
            @Override
            public int intValue() {
                return 5;
            }
        };

        public static ShowType valueOf(int value) {
            if (value == 0) {
                return Text;

            } else if (value == 1) {
                return At;

            } else if (value == 2) {
                return Draft;

            } else if (value == 3) {
                return Audio;

            } else if (value == 4) {
                return Emergency;

            } else if (value == 5) {
                return RedEnvelope;
            }
            return null;
        }

        public abstract int intValue();

    }

    public enum EntryType {

        /**
         * 跳转到应用
         */
        To_APP {
            @Override
            public int intValue() {
                return 1;
            }
        },

        /**
         * 跳转到详情
         */
        To_Chat_Detail {
            @Override
            public int intValue() {
                return 0;
            }
        },

        /**
         * 跳转到URL
         */
        To_URL {
            @Override
            public int intValue() {
                return 2;
            }
        },

        /**
         * 跳转到k9邮箱
         */
        To_K9Email {
            @Override
            public int intValue() {
                return 4;
            }
        },

        To_Native {
            @Override
            public int intValue() {
                return 3;
            }
        },

        /**
         * 跳转到组织申请列表
         */
        To_ORG_APPLYING {
            @Override
            public int intValue() {
                return 5;
            }
        },


        /**
         * 群聊助手
         */
        DISCUSSION_HELPER {
            @Override
            public int intValue() {
                return 6;
            }
        },


        /**
         * 内宣号应用
         */
        APP_ANNOUNCE {
            @Override
            public int intValue() {
                return 7;
            }
        },


        /**
         * 服务号消息汇总
         */
        NEWS_SUMMARY {
            @Override
            public int intValue() {
                return 8;
            }
        }
        ;


        public static EntryType valueOfInt(int value) {
            if (value == 0) {
                return To_Chat_Detail;
            } else if (value == 1) {
                return To_APP;
            } else if (value == 2) {
                return To_URL;
            } else if (value == 3) {
                return To_Native;
            } else if (value == 4) {
                return To_K9Email;
            } else if (value == 5) {
                return To_ORG_APPLYING;
            } else if(value == 6) {
                return DISCUSSION_HELPER;
            } else if(value == 7) {
                return APP_ANNOUNCE;
            } else if(value == 8) {
                return NEWS_SUMMARY;
            }

            return To_Chat_Detail;
        }

        public abstract int intValue();
    }

    public enum NewMessageType {

        Num {
            @Override
            public int intValue() {
                return 0;
            }
        },

        RedDot {
            @Override
            public int intValue() {
                return 1;
            }
        },

        Icon {
            @Override
            public int intValue() {
                return 2;
            }
        };

        public static NewMessageType valueOfInt(int value) {
            if (value == 0) {
                return Num;
            } else if (value == 1) {
                return RedDot;
            } else if (value == 2) {
                return Icon;
            }
            return Num;
        }

        public abstract int intValue();

    }

    public Session() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.identifier);
        dest.writeString(this.mDomainId);
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
        dest.writeString(this.name);
        dest.writeString(this.avatar);
        dest.writeString(this.orgId);
        dest.writeString(this.lastMessageId);
        dest.writeString(this.atMessageId);
        dest.writeString(this.lastAtMessageText);
        dest.writeString(this.lastMessageText);
        dest.writeInt(this.lastMessageStatus == null ? -1 : this.lastMessageStatus.ordinal());
        dest.writeInt(this.lastMessageShowType == null ? -1 : this.lastMessageShowType.ordinal());
        dest.writeLong(this.lastTimestamp);
        dest.writeInt(this.top);
        dest.writeString(this.language);
        dest.writeInt(this.shield);
        dest.writeString(this.draft);
        dest.writeList(new ArrayList(this.unreadMessageIdSet));
        dest.writeList(new ArrayList(this.unreadTransferIdSet));
        dest.writeList(new ArrayList(this.unreadInDbSet));
        dest.writeInt(this.entryType == null ? -1 : this.entryType.ordinal());
        dest.writeString(this.entryValue);
        dest.writeString(this.entryId);
        dest.writeInt(this.newMessageType == null ? -1 : this.newMessageType.ordinal());
        dest.writeByteArray(this.newMessageContent);
        dest.writeByte(this.savedToDb ? (byte) 1 : (byte) 0);
        dest.writeByte(this.visible ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mSelect ? (byte) 1 : (byte) 0);
    }

    protected Session(Parcel in) {
        this.identifier = in.readString();
        this.mDomainId = in.readString();
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : SessionType.values()[tmpType];
        this.name = in.readString();
        this.avatar = in.readString();
        this.orgId = in.readString();
        this.lastMessageId = in.readString();
        this.atMessageId = in.readString();
        this.lastAtMessageText = in.readString();
        this.lastMessageText = in.readString();
        int tmpLastMessageStatus = in.readInt();
        this.lastMessageStatus = tmpLastMessageStatus == -1 ? null : ChatStatus.values()[tmpLastMessageStatus];
        int tmpLastMessageShowType = in.readInt();
        this.lastMessageShowType = tmpLastMessageShowType == -1 ? null : ShowType.values()[tmpLastMessageShowType];
        this.lastTimestamp = in.readLong();
        this.top = in.readInt();
        this.language = in.readString();
        this.shield = in.readInt();
        this.draft = in.readString();
        this.unreadMessageIdSet.addAll(in.readArrayList(String.class.getClassLoader()));
        this.unreadTransferIdSet.addAll(in.readArrayList(String.class.getClassLoader()));
        this.unreadInDbSet.addAll(in.readArrayList(String.class.getClassLoader()));
        int tmpEntryType = in.readInt();
        this.entryType = tmpEntryType == -1 ? null : EntryType.values()[tmpEntryType];
        this.entryValue = in.readString();
        this.entryId = in.readString();
        int tmpNewMessageType = in.readInt();
        this.newMessageType = tmpNewMessageType == -1 ? null : NewMessageType.values()[tmpNewMessageType];
        this.newMessageContent = in.createByteArray();
        this.savedToDb = in.readByte() != 0;
        this.visible = in.readByte() != 0;
        this.mSelect = in.readByte() != 0;
    }

    public static final Creator<Session> CREATOR = new Creator<Session>() {
        @Override
        public Session createFromParcel(Parcel source) {
            return new Session(source);
        }

        @Override
        public Session[] newArray(int size) {
            return new Session[size];
        }
    };
}
