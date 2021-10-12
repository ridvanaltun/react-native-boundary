package com.eddieowens.services;

import static com.eddieowens.RNBoundaryModule.TAG;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.facebook.react.HeadlessJsTaskService;

import java.util.List;

public class BoundaryBootJobIntentService extends JobIntentService {

    public BoundaryBootJobIntentService() {
        super();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i(TAG, "Handling geofencing event");
        if (!isAppOnForeground((this.getApplicationContext()))) {
            sendEvent(this.getApplicationContext());
        }
    }

    private void sendEvent(Context context) {

        Intent headlessBoundaryIntent = new Intent(context, BoundaryBootHeadlessTaskService.class);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O || isAppOnForeground(context)) {
            context.startService(headlessBoundaryIntent);
            HeadlessJsTaskService.acquireWakeLockNow(context);
        } else {
            // Since Oreo (8.0) and up they have restricted starting background services, and it will crash the app
            // But we can create a foreground service and bring an notification to the front
            // http://stackoverflow.com/questions/8489993/check-android-application-is-in-foreground-or-not
            context.startForegroundService(headlessBoundaryIntent);
        }
    }

    private boolean isAppOnForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses =
                activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance ==
                    ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&
                    appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}