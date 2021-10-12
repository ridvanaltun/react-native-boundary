package com.eddieowens.services;

import static com.eddieowens.RNBoundaryModule.TAG;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.eddieowens.R;
import com.facebook.react.HeadlessJsTaskService;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.jstasks.HeadlessJsTaskConfig;

import java.util.Random;

public class BoundaryBootHeadlessTaskService extends HeadlessJsTaskService {

    public static final String NOTIFICATION_CHANNEL_ID = "com.eddieowens.BOOT_SERVICE_CHANNEL";
    private static final String KEY_NOTIFICATION_TITLE = "rnboundary.notification_title";
    private static final String KEY_NOTIFICATION_TEXT = "rnboundary.notification_text";
    private static final String KEY_NOTIFICATION_ICON = "rnboundary.notification_icon";


    @Nullable
    protected HeadlessJsTaskConfig getTaskConfig(Intent intent) {
        Bundle extras = intent.getExtras();
        return new HeadlessJsTaskConfig(
                "OnBoundaryBoot",
                extras != null ? Arguments.fromBundle(extras) : Arguments.createMap(),
                5000,
                true);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundServiceNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        startForegroundServiceNotification();
        return result;
    }

    private void startForegroundServiceNotification() {
        Context context = this.getApplicationContext();

        // Channel for the foreground service notification
        createChannel(context);

        NotificationCompat.Builder builder = getNotificationBuilder();
        Notification notification = builder.build();

        Random rand = new Random();
        int notificationId = rand.nextInt(100000);

        startForeground(notificationId, notification);
        HeadlessJsTaskService.acquireWakeLockNow(context);
    }

    private void createChannel(Context context) {
        String NOTIFICATION_CHANNEL_NAME = "Geofence initialization";
        String NOTIFICATION_CHANNEL_DESCRIPTION = "Only used to add your geofences again after a reboot.";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(NOTIFICATION_CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public NotificationCompat.Builder getNotificationBuilder() {
        Context context = getApplicationContext();
        String title = "Geofences will be added again";
        String text = "You're have restarted you device.";
        int iconResource = -1;

        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            title = bundle.getString(KEY_NOTIFICATION_TITLE, title);
            text = bundle.getString(KEY_NOTIFICATION_TEXT, text);
            iconResource = bundle.getInt(KEY_NOTIFICATION_ICON, -1);
        } catch (Exception e) {
            Log.e(TAG, "Cannot get application Bundle " + e.toString());
        }


        // Notification for the foreground service
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(text)
                .setOngoing(true)
                .setColor(ContextCompat.getColor(context, R.color.accent_material_light));

        if (iconResource > -1) {
            builder.setSmallIcon(iconResource);
        }

        return builder;
    }
}
