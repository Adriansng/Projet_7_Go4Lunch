package com.adriansng.projet_7_go4lunch.service.notification;

import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public abstract class ControllerWorkerManager {

    private static final String WORK_TAG_SELECT = "WORK_REQUEST_TAG_SELECT";
    private static final String WORK_TAG_RESET = "WORK_REQUEST_TAG_RESET";

    // ------------------
    // INIT NOTIFICATION
    // ------------------

    public static void initNotification() {
        notificationSelectWork();
        notificationResetWork();
    }

    // ------------------
    // NOTIFICATIONS
    // ------------------

    // --- SETUP --

    private static long setUpInitialDelay(int hour, int minute) {
        // TIME NOW
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();

        // if time >= 12 then DAY_OF_DAY +1
        if (calendar.get(Calendar.HOUR_OF_DAY) > hour
                || (calendar.get(Calendar.HOUR_OF_DAY) == hour && calendar.get(Calendar.MINUTE) > minute)) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // TIME OF NOTIFICATION
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // DIFF between TIME now and TIME of notification
        return calendar.getTimeInMillis() - time;
    }

    // --- SELECT PLACE ---

    public static void notificationSelectWork() {
        // CONSTRAINTS
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // ONE TIME WORK REQUEST
        WorkManager workManager = WorkManager.getInstance();
        workManager.cancelAllWorkByTag(WORK_TAG_SELECT);
        OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(NotificationSelectedPlace.class)
                .setConstraints(constraints)
                .setInitialDelay(setUpInitialDelay(12, 0), TimeUnit.MILLISECONDS)
                .addTag(WORK_TAG_SELECT)
                .build();
        workManager.enqueue(mRequest);

    }

    // --- RESET PLACE ---

    public static void notificationResetWork() {
        // CONSTRAINTS
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        WorkManager workManager = WorkManager.getInstance();
        workManager.cancelAllWorkByTag(WORK_TAG_RESET);
        OneTimeWorkRequest mRequest = new OneTimeWorkRequest.Builder(NotificationResetPlace.class)
                .setConstraints(constraints)
                .setInitialDelay(setUpInitialDelay(16, 0), TimeUnit.MILLISECONDS)
                .addTag(WORK_TAG_RESET)
                .build();
        workManager.enqueue(mRequest);
    }
}
