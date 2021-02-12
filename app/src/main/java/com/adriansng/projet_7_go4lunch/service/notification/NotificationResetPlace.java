package com.adriansng.projet_7_go4lunch.service.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.adriansng.projet_7_go4lunch.R;
import com.adriansng.projet_7_go4lunch.model.Workmate;
import com.adriansng.projet_7_go4lunch.repositories.WorkmatesRepository;
import com.adriansng.projet_7_go4lunch.service.helper.HelperWorkmate;
import com.adriansng.projet_7_go4lunch.view.mainActivity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class NotificationResetPlace extends Worker {

    // --- FOR DATA ---

    private WorkmatesRepository workmatesRepository;
    private Workmate workmate;

    // Notification ID.
    private static final int NOTIFICATION_ID = 1;
    // Notification TAG.
    private static final String NOTIFICATION_TAG = "WORK_REQUEST_TAG_SELECT";
    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";

    // ------------------
    // SUPER CONSTRUCTOR
    // ------------------

    public NotificationResetPlace(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    // ------------------
    // DO WORK
    // ------------------

    @NonNull
    @Override
    public Result doWork() {
        this.workmatesRepository = new WorkmatesRepository();
        getWorkmate(getApplicationContext());
        return Result.success();
    }

    // ------------------
    // INIT
    // ------------------

    // --- WORKMATE ---

    private void getWorkmate(Context context) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        HelperWorkmate.getWorkmateCollection(Objects.requireNonNull(user).getUid()).addOnCompleteListener(task -> {
            workmate = task.getResult().toObject(Workmate.class);
            workmatesRepository.updateWorkmateChooseRestaurant(Objects.requireNonNull(workmate).getUid(), null, null, null);
            sendVisualNotification(context);
        });
    }

    // ------------------
    // NOTIFICATION
    // ------------------

    // --- NOTIFICATION BUILDER ---

    private void sendVisualNotification(Context context) {
        // Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(context.getString(R.string.title_notification));
        inboxStyle.addLine(workmate.getNameWorkmate() + context.getString(R.string.notificationResetPlace_1));
        inboxStyle.addLine(context.getString(R.string.notificationResetPlace_2));

        // Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context, PRIMARY_CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_baseline_restaurant_menu_24)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(context.getString(R.string.title_notification))
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);


        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message provenant GO4Lunch";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(PRIMARY_CHANNEL_ID, channelName, importance);
            mNotificationManager.createNotificationChannel(mChannel);
        }

        // Show notification
        mNotificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }
}
