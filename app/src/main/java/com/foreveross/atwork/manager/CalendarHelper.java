package com.foreveross.atwork.manager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.foreveross.atwork.infrastructure.shared.EmailSettingInfo;
import com.foreveross.atwork.infrastructure.utils.ListUtil;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import microsoft.exchange.webservices.data.core.enumeration.property.MeetingResponseType;
import microsoft.exchange.webservices.data.core.exception.service.local.ServiceLocalException;
import microsoft.exchange.webservices.data.core.service.item.Appointment;
import microsoft.exchange.webservices.data.core.service.item.Item;
import microsoft.exchange.webservices.data.property.complex.Attendee;
import microsoft.exchange.webservices.data.property.complex.AttendeeCollection;

/**
 * Created by reyzhang22 on 2017/12/13.
 */

public class CalendarHelper {

    private static final String QUERY_LOCAL_CALENDAR = "((" + CalendarContract.Calendars.ACCOUNT_NAME + " = ?) AND (" + CalendarContract.Calendars.ACCOUNT_TYPE + " = ?))";

    private static final String UPDATE_EVENT_SELECTIONS = "((" + CalendarContract.Events.TITLE + " = ?) AND (" + CalendarContract.Events.EVENT_LOCATION + " = ?) AND ("+ CalendarContract.Events.ALL_DAY +" = ?))";

    private static final String UPDATE_REMINDER_SELECTIONS = "((" + CalendarContract.Reminders.EVENT_ID + " = ?))";


    private static final String QUERY_LOCAL_EVENTS = "(" + CalendarContract.Events.SYNC_DATA2 + " = ? )";

    public static void syncEwsCalendar(Context context, String accountName, String accountType, List<Item> appointmentList) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        List<Long>  calendarIds = queryAllLocalCalendars(context, accountName, accountType);
        Uri calUri;
        if (ListUtil.isEmpty(calendarIds)) {
            calUri = createLocalCalendar(context, accountName, accountType);
        } else {
            calUri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, calendarIds.get(0));
        }

        if (calUri == null) {
            Log.e("calendarHelper", "cal Uri is null");
            return;
        }
        Map<String, String> localEwsEvents = queryAllLocalEwsEvents(context);
        Log.e("calendarHelper", "server ews size = " + appointmentList.size());
        Log.e("calendarHelper", "localEwsEvents size = " + localEwsEvents.size());
        String calId = calUri.getLastPathSegment();
        if (localEwsEvents.isEmpty()) {
            Log.e("calendarHelper", "local ews events is empty");
            createLocalEwsEvents(context, accountName, accountType, calUri, appointmentList);
            return;
        }

        List<Item> unSyncList = new ArrayList<>();
        List<Appointment> updateList = new ArrayList<>();
        List<String> ids = new ArrayList<>();
        for (Item appointment : appointmentList) {
            if (!EmailSettingInfo.getInstance().getSyncCalendar(context)) {
                break;
            }
            try {
                String serverId = appointment.getId().toString();
                ids.add(serverId);
                if (TextUtils.isEmpty(localEwsEvents.get(serverId))) {
                    Log.e("calendarHelper", "add unsynced list id " + serverId);
                    unSyncList.add(appointment);
                    continue;
                }
                updateList.add((Appointment) appointment);
            } catch (ServiceLocalException e) {
                e.printStackTrace();
            }
        }
        if (!ListUtil.isEmpty(unSyncList)) {
            Log.e("calendarHelper", "create unsynced list " + unSyncList.size());
            createLocalEwsEvents(context, accountName, accountType, calUri, unSyncList);
        }
        if (!ListUtil.isEmpty(updateList)) {
            Log.e("calendarHelper", "update list " + updateList.size());
            updateLocalEwsEvents(context, localEwsEvents, accountName, accountType, calUri, updateList);
        }
        Set<String> keys = localEwsEvents.keySet();
        for (String key : keys) {
            if (!EmailSettingInfo.getInstance().getSyncCalendar(context)) {
                break;
            }
            if (!ids.contains(key)) {
                Log.e("calendarHelper", "delete list = " + key);
                deleteEventsById(context, Long.valueOf(localEwsEvents.get(key)));
            }
        }

    }

    private static void createLocalEwsEvents(Context context, String accountName, String accountType, Uri calUri, List<Item> appointmentList) {
        for (Item appointment : appointmentList) {
            try {
                if (!EmailSettingInfo.getInstance().getSyncCalendar(context)) {
                    break;
                }
                createLocalEvent(context, accountName, accountType, calUri, (Appointment) appointment);

            } catch (ServiceLocalException e) {
                Log.e("calendarHelper", "exception on 71");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("calendarHelper", "exception on 75");
            }
        }
    }

    private static void updateLocalEwsEvents(Context context, Map<String, String> localMap, String accountName, String accountType, Uri calUri, List<Appointment> appointmentList) {
        for (Appointment appointment : appointmentList) {
            try {

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Log.e("calendarHelper", "update local Event " + appointment.getId().toString());
                String eventId = localMap.get(appointment.getId().toString());
                if(!TextUtils.isEmpty(eventId)) {
                    Log.e("calendarHelper", "start delete local Event before update " + eventId);
                    deleteEventsById(context, Long.valueOf(eventId));
                    Log.e("calendarHelper", "start recreate event ");
                    createLocalEvent(context, accountName, accountType, calUri, appointment);
                }

            } catch (ServiceLocalException e) {
                Log.e("calendarHelper", "exception on 158");
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("calendarHelper", "exception on 162");
            }
        }
    }

    private static Uri createLocalCalendar(Context context, String accountName, String accountType) {
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Calendars.ACCOUNT_NAME, accountName);
        cv.put(CalendarContract.Calendars.ACCOUNT_TYPE, accountType);
        cv.put(CalendarContract.Calendars.NAME, accountName);
        cv.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, "700");
        cv.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        cv.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        cv.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, accountName);
        cv.put(CalendarContract.Calendars.OWNER_ACCOUNT, accountName);

        Uri uri = CalendarContract.Calendars.CONTENT_URI.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType)
                .build();
        return context.getContentResolver().insert(uri, cv);
    }



    private static List<Long> queryAllLocalCalendars(Context context, String accountName, String accountType) {
        String[] selectionArgs = new String[]{accountName, accountType};
        List<Long> localCalendars = new ArrayList<>();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return localCalendars;
        }
        Cursor cursor = context.getContentResolver().query(CalendarContract.Calendars.CONTENT_URI, null, QUERY_LOCAL_CALENDAR, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                localCalendars.add(cursor.getLong(cursor.getColumnIndex(CalendarContract.Calendars._ID)));
            }
        }
        cursor.close();
        return localCalendars;
    }

    private static Map<String, String> queryAllLocalEwsEvents(Context context) {
        Log.e("calendarHelper", "start query local events");
        String[] selectionArgs = new String[]{"ews"};
        Map<String, String> localEvents = new HashMap<>();
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return localEvents;
        }
        Cursor cursor = context.getContentResolver().query(CalendarContract.Events.CONTENT_URI, null, QUERY_LOCAL_EVENTS, selectionArgs, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                localEvents.put(cursor.getString(cursor.getColumnIndex(CalendarContract.Events.SYNC_DATA1)), cursor.getString(cursor.getColumnIndex(CalendarContract.Events._ID)));
            }
        }
        cursor.close();
        return localEvents;
    }

    private static long createLocalEvent(Context activity, String accountName, String accountType, Uri calendarUri, Appointment appointment) throws ServiceLocalException {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            return -1;
        }
        Log.e("calendarHelper", "creating local Event " + appointment.getId().toString());

        long calId = Long.parseLong(calendarUri.getLastPathSegment());
        ContentValues cv = getEventContentValues(calId, appointment);

        Uri uri = CalendarContract.Events.CONTENT_URI.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, accountName)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, accountType)
                .build();

        Uri eventUri = activity.getContentResolver().insert(uri, cv);
        if (eventUri == null ) {
            return -1;
        }
        if (appointment.getIsReminderSet()) {
            long eventId = ContentUris.parseId(eventUri);
            ContentValues reminderCv = getReminderContentValues(eventId, appointment);
            activity.getContentResolver().insert(CalendarContract.Reminders.CONTENT_URI, reminderCv);
        }
        if (appointment.getOptionalAttendees() != null) {
            AttendeeCollection attendeeCollection = appointment.getOptionalAttendees();
            setAttendee(activity, eventUri, attendeeCollection.getItems(), true);
        }

        if (appointment.getRequiredAttendees() != null) {
            AttendeeCollection attendeeCollection = appointment.getRequiredAttendees();
            setAttendee(activity, eventUri, attendeeCollection.getItems(), false);
        }
        return 1;
    }


    private static ContentValues getEventContentValues(long calendarId, Appointment appointment) throws ServiceLocalException {
        ContentValues cv = new ContentValues();
        cv.put(CalendarContract.Events.SYNC_DATA1, appointment.getId().toString());
        cv.put(CalendarContract.Events.SYNC_DATA2, "ews");
        cv.put(CalendarContract.Events.MUTATORS, "");
        cv.put(CalendarContract.Events.STATUS, 1);
        cv.put(CalendarContract.Events.TITLE, appointment.getSubject());
        cv.put(CalendarContract.Events.EVENT_LOCATION, appointment.getLocation());
        boolean allDay = appointment.getIsAllDayEvent();
        cv.put(CalendarContract.Events.ALL_DAY, allDay ? 1 : 0);
        cv.put(CalendarContract.Events.DTSTART, getDay(appointment.getStart(), allDay).getTime());
        cv.put(CalendarContract.Events.DTEND, getDay(appointment.getEnd(), allDay).getTime());
        cv.put(CalendarContract.Events.CALENDAR_ID, calendarId);
        cv.put(CalendarContract.Events.EVENT_LOCATION, appointment.getLocation());
        cv.put(CalendarContract.Events.TITLE, appointment.getSubject());
        cv.put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai");
        return cv;
    }

    private static ContentValues getReminderContentValues(long eventId, Appointment appointment) throws ServiceLocalException {
        ContentValues reminderCv = new ContentValues();
        reminderCv.put(CalendarContract.Reminders.EVENT_ID, eventId);
        reminderCv.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        reminderCv.put(CalendarContract.Reminders.MINUTES, appointment.getReminderMinutesBeforeStart());
        return reminderCv;
    }

    private static Date getDay(Date date, boolean isAllDay) {
        if (!isAllDay) {
            return date;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    @SuppressLint("MissingPermission")
    private static void setAttendee(Context activity, Uri eventUri, List<Attendee> attendeeList, boolean optionalAttended) {
        long eventId = ContentUris.parseId(eventUri);
        for (Attendee attendee : attendeeList) {
            ContentValues values = getAttendeeContentValues(eventId, attendee, optionalAttended);
            activity.getContentResolver().insert(CalendarContract.Attendees.CONTENT_URI, values);
        }
    }

    @SuppressLint("MissingPermission")
    private static void updateAttendee(Context context, long eventId, List<Attendee> attendeeList, boolean optionalAttended) {
        for (Attendee attendee : attendeeList) {
            ContentValues values = getAttendeeContentValues(eventId, attendee, optionalAttended);
            context.getContentResolver().insert(CalendarContract.Attendees.CONTENT_URI, values);
        }
    }

    private static ContentValues getAttendeeContentValues(long eventId, Attendee attendee, boolean optionalAttended) {
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Attendees.ATTENDEE_NAME, attendee.getName());
        values.put(CalendarContract.Attendees.ATTENDEE_EMAIL, attendee.getAddress());
        values.put(CalendarContract.Attendees.ATTENDEE_RELATIONSHIP, CalendarContract.Attendees.RELATIONSHIP_SPEAKER);
        values.put(CalendarContract.Attendees.ATTENDEE_TYPE, optionalAttended ? CalendarContract.Attendees.TYPE_OPTIONAL : CalendarContract.Attendees.TYPE_REQUIRED);
        values.put(CalendarContract.Attendees.ATTENDEE_STATUS, getAttendeeResponse(attendee.getResponseType()));
        values.put(CalendarContract.Attendees.EVENT_ID, eventId);
        return values;
    }

    private static int getAttendeeResponse(MeetingResponseType meetingResponseType) {
        switch (meetingResponseType) {
            case Tentative:
                return CalendarContract.Attendees.ATTENDEE_STATUS_TENTATIVE;
            case Accept:
                return CalendarContract.Attendees.ATTENDEE_STATUS_ACCEPTED;
            case Decline:
                return CalendarContract.Attendees.ATTENDEE_STATUS_DECLINED;
        }
        return CalendarContract.Attendees.ATTENDEE_STATUS_NONE;
    }


    private static void deleteLocalCalendar(Context context, long calId) {
        Uri calUri = ContentUris.withAppendedId(CalendarContract.Calendars.CONTENT_URI, calId);
        ContentResolver cv =  context.getContentResolver();
        cv.delete(calUri, null, null);
        Uri eventUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, calId);
        cv.delete(eventUri, null, null);
        if (eventUri == null) {
            return;
        }
        Uri attendedUri = ContentUris.withAppendedId(CalendarContract.Attendees.CONTENT_URI, ContentUris.parseId(eventUri));
        cv.delete(attendedUri, null, null);
        Uri reminderUri = ContentUris.withAppendedId(CalendarContract.Reminders.CONTENT_URI, ContentUris.parseId(eventUri));
        cv.delete(reminderUri, null, null);
    }

    private static void deleteEventsById(Context context, long eventId) {
        ContentResolver cv =  context.getContentResolver();
        Uri eventUri = ContentUris.withAppendedId(CalendarContract.Events.CONTENT_URI, eventId);
        cv.delete(eventUri, null, null);
        if (eventUri == null) {
            return;
        }
        Uri attendedUri = ContentUris.withAppendedId(CalendarContract.Attendees.CONTENT_URI, ContentUris.parseId(eventUri));
        cv.delete(attendedUri, null, null);
        Uri reminderUri = ContentUris.withAppendedId(CalendarContract.Reminders.CONTENT_URI, ContentUris.parseId(eventUri));
        cv.delete(reminderUri, null, null);
    }

    public static boolean clearLocalCalendars(Activity activity, String accountName, String accountType) {
        List<Long>  calendarIds = queryAllLocalCalendars(activity, accountName, accountType);
        if (ListUtil.isEmpty(calendarIds)) {
            return true;
        }
        Map<String, String> localEwsEvents = queryAllLocalEwsEvents(activity);
        Set<String> keys = localEwsEvents.keySet();
        for (String key : keys) {
            deleteEventsById(activity, Long.valueOf(localEwsEvents.get(key)));
        }
        return true;
    }

}
