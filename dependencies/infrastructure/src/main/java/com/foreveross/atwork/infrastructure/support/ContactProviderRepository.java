package com.foreveross.atwork.infrastructure.support;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import android.text.TextUtils;

import com.foreveross.atwork.infrastructure.model.MobileContact;
import com.foreveross.atwork.infrastructure.model.Employee;
import com.foreveross.atwork.infrastructure.model.ShowListItem;
import com.foreveross.atwork.infrastructure.model.user.User;
import com.foreveross.atwork.infrastructure.utils.ListUtil;
import com.foreveross.atwork.infrastructure.utils.StringUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Pattern;

import static com.foreveross.atwork.infrastructure.utils.spare.pinyin.HanziToPinyin.getPinyin;

/**
 * Created by lingen on 15/5/12.
 * Description:
 * 手机联系人读取写入辅助类
 */
public class ContactProviderRepository {

    private static final String DISPLAY_NAME = ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME;

    private static final String PHONE_NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

    private static final String PHONE_TYPE = ContactsContract.CommonDataKinds.Phone.TYPE;

    private static final String PHONE_LABEL = ContactsContract.CommonDataKinds.Phone.LABEL;

    private static final String EMAIL_DATA = ContactsContract.CommonDataKinds.Email.DATA;

    private static final String EMAIL_LABEL = ContactsContract.CommonDataKinds.Email.LABEL;

    private static final String EMAIL_TYPE = ContactsContract.CommonDataKinds.Email.TYPE;

    private static String ACCOUNT_TYPE = ContactsContract.RawContacts.ACCOUNT_TYPE;

    private static String ACCOUNT_NAME = ContactsContract.RawContacts.ACCOUNT_NAME;

    private static Uri CONTENT_URI = ContactsContract.Data.CONTENT_URI;

    private static Uri RAW_CONTENT_URI = ContactsContract.RawContacts.CONTENT_URI;

    private static String RAW_CONTACT_ID = ContactsContract.Data.RAW_CONTACT_ID;

    private static String MIMETYPE = ContactsContract.Data.MIMETYPE;

    private static String CONTENT_ITEM_TYPE = ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE;

    private static String emailPatternReg = "[A-Z0-9a-z\\._%+-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}";

    private ContentResolver contentResolver;

    public ContactProviderRepository(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    public boolean syncUserToMobile(ShowListItem contactSync) {
        MobileContact contact = new MobileContact();
        if(contactSync instanceof User) {
            User user = (User) contactSync;
            contact.mName = user.getShowName();
            contact.setMobile(getNonNullText(user.mPhone));
            contact.setEmail(getNonNullText(user.mEmail));

        } else if(contactSync instanceof Employee) {
            Employee employee = (Employee) contactSync;
            contact.mName = employee.getShowName();
            contact.setMobile(getNonNullText(employee.mobile));
            contact.setEmail(getNonNullText(employee.email));
        }


        return syncContactToMobile(contact);
    }

    @NonNull
    private String getNonNullText(String email) {
        if(null == email) {
            email = StringUtils.EMPTY;
        }
        return email;
    }

    public boolean syncContactToMobile(MobileContact contact) {
        if (ListUtil.isEmpty(contact.mMobileList)) {
            return false;
        }

        int result_id = isContactExists(contact);
        if (-1 == result_id) {
            return insertContact(contact);
        } else {
            return updateContact(contact, result_id);
        }
    }

    public boolean syncContactToMobileByEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        MobileContact contact = new MobileContact();
        contact.mName = email;
        contact.setEmail(email);
        boolean result = isContactExistsByEmail(contact);

        if (!result && isEmail(email)) {
            return insertContact(contact);
        }
        return false;

    }

    public boolean updateContact(MobileContact contact, int raw_contact_id) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        for (String mobile : contact.mMobileList) {
            //更新号码
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, raw_contact_id)
                    .withValue(MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER )
                    .withValue(PHONE_NUMBER, mobile)
                    .build());
        }

        for(String email : contact.mEmailList) {
            //更新 email
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, raw_contact_id)
                    .withValue(MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_OTHER )
                    .withValue(EMAIL_DATA, email)
                    .build());
        }


        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean insertContact(MobileContact contact) {

        if (contact == null) {
            return false;
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();

        int rawContactInsertIndex = 0;

        ops.add(ContentProviderOperation.newInsert(RAW_CONTENT_URI)
                .withValue(ACCOUNT_TYPE, null)
                .withValue(ACCOUNT_NAME, null)
                .build());

        ops.add(ContentProviderOperation.newInsert(CONTENT_URI)
                .withValueBackReference(RAW_CONTACT_ID, rawContactInsertIndex)
                .withValue(MIMETYPE, CONTENT_ITEM_TYPE)
                .withValue(DISPLAY_NAME, contact.mName)
                .build());

        for (String mobile : contact.mMobileList) {
            ops.add(ContentProviderOperation.newInsert(CONTENT_URI)
                    .withValueBackReference(RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(PHONE_NUMBER, mobile)
                    .withValue(PHONE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER)
                    .build());

        }


        for (String email : contact.mEmailList) {
            ops.add(ContentProviderOperation.newInsert(CONTENT_URI)
                    .withValueBackReference(RAW_CONTACT_ID, rawContactInsertIndex)
                    .withValue(MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE)
                    .withValue(EMAIL_DATA, email)
                    .withValue(EMAIL_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_OTHER)
                    .build());

        }



        try {
            contentResolver.applyBatch(ContactsContract.AUTHORITY, ops);
            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
            return false;
        } catch (OperationApplicationException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断联系人是否存在
     *
     * @return int
     * 存在则返回raw_id, 不存在则返回-1
     */
    public int isContactExists(MobileContact contact) {
        int rawId = -1;
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{RAW_CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.CONTACT_ID},
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=?",
                    new String[]{contact.mName}, null);

            if (null != cursor) {

                while (cursor.moveToNext()) {
                    rawId = cursor.getInt(cursor.getColumnIndex(RAW_CONTACT_ID));
                    break;
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return rawId;
    }


    public boolean isContactExistsByEmail(MobileContact contact) {
        boolean result = false;
        Cursor cursor = null;
        try {
            cursor = contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{RAW_CONTACT_ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.CONTACT_ID},
                    null,
                    null,
                    null);

            if (null != cursor) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    name = name.replace(" ", ".");
                    if (name.equalsIgnoreCase(contact.mName)) {
                        result = true;
                        break;
                    }
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public boolean isEmail(String text) {
        return Pattern.matches(emailPatternReg, text);
    }

    public JSONObject getMobileContacts() {
        JSONObject contactObject = new JSONObject();

        Cursor cursor = null;
        try {

            cursor = contentResolver.query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DATA1},
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

            if (cursor.moveToFirst()) {
                do {
                    JSONObject contact = new JSONObject();
                    String tel = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    if (TextUtils.isEmpty(tel)) {
                        continue;
                    }
                    String name = cursor.getString(0);
                    String sortKey = getSortKey(cursor.getString(1));
                    contact.put("name", name);
                    contact.put("tel", tel);

                    contactObject.accumulate(sortKey, contact);

                } while (cursor.moveToNext());
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return contactObject;
    }

    /**
     * 获取sort key的首个字符，如果是英文字母就直接返回，否则返回#。
     *
     * @param sortKeyString 数据库中读取出的sort key
     * @return 英文字母或者#
     */
    private static String getSortKey(String sortKeyString) {
        sortKeyString = getPinyin(sortKeyString);
        String key = sortKeyString.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        }
        return "#";
    }



}
