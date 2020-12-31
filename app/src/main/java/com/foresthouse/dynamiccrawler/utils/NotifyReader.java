package com.foresthouse.dynamiccrawler.utils;

import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;


import java.util.Set;

import androidx.core.app.NotificationManagerCompat;

public class NotifyReader extends NotificationListenerService {
    public final static String TAG = "[ MyNotificationListener ]";

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);

        Log.d(TAG, "onNotificationRemoved ~ " + " packageName: " + sbn.getPackageName() + " id: " + sbn.getId());
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        Notification notification = sbn.getNotification();
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString(Notification.EXTRA_TITLE);
        CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
        CharSequence subText = extras.getCharSequence(Notification.EXTRA_SUB_TEXT);
        Icon smallIcon = notification.getSmallIcon();
        Icon largeIcon = notification.getLargeIcon();

        Log.d(TAG, "onNotificationPosted ~ " + " packageName: " + sbn.getPackageName() + " id: " + sbn.getId() + " postTime: " + sbn
                .getPostTime() + " title: " + title + " text : " + text + " subText: " + subText);
    }

    public void checkNotifyAccessPermissionGranted() {
        Set<String> sets = NotificationManagerCompat.getEnabledListenerPackages(getApplicationContext());
        Log.d(TAG, "checkNotifyAccessPermissionGranted: " + sets.toString());
        try {
            if (!sets.contains("")) { //TODO 이게 뭔 코드임...?
                displayNotifyAccessSetting();
            }
        } catch (NullPointerException e) {
            displayNotifyAccessSetting();
        }
    }

    public void displayNotifyAccessSetting() {
        Generator.makeYNDialog(getApplicationContext(), "알림 접근권한 설정", "현재 알림 접근이 불가합니다.\n알림 접근을 허용해야 알림을 읽을 수 있습니다.\n설정창으로 이동할까요?", "이동", "취소", null,
                               new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   getApplicationContext().startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                               }
                           }, null, null, null);
    }
}