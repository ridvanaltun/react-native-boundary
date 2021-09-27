package com.eddieowens.receivers;

import static com.eddieowens.RNBoundaryModule.TAG;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.JobIntentService;

import com.eddieowens.services.BoundaryBootJobIntentService;
import com.eddieowens.services.BoundaryEventJobIntentService;
import com.facebook.react.HeadlessJsTaskService;

import java.util.List;

public class BoundaryBootBroadcastReceiver extends BroadcastReceiver {

    public BoundaryBootBroadcastReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Boundary device boot event");
        if("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            JobIntentService.enqueueWork(context, BoundaryBootJobIntentService.class, 0, intent);
        }
    }
}
