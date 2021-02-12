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
import com.adriansng.projet_7_go4lunch.service.helper.HelperWorkmate;
import com.adriansng.projet_7_go4lunch.view.mainActivity.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NotificationSelectedPlace extends Worker {

    // --- FOR DATA ---

    private FirebaseUser user;
    private Workmate workmate;
    private final List<Workmate> workmates = new ArrayList<>();
    private String idRestaurant, nameRestaurant, addressRestaurant;

    // Notification ID.
    private static final int NOTIFICATION_ID = 0;
    // Notification TAG.
    private static final String NOTIFICATION_TAG = "WORK_REQUEST_TAG_SELECT";
    // Notification channel ID.
    private static final String PRIMARY_CHANNEL_ID =
            "primary_notification_channel";

    private final NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

    // ------------------
    // SUPER CONSTRUCTOR
    // ------------------

    public NotificationSelectedPlace(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    // ------------------
    // DO WORK
    // ------------------

    @NonNull
    @Override
    public Result doWork() { getWorkmate(getApplicationContext());
        return Result.success();
    }

    // ------------------
    // INIT
    // ------------------

    // --- WORKMATE ---

    private void getWorkmate(Context context) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        HelperWorkmate.getWorkmateCollection(Objects.requireNonNull(user).getUid()).addOnCompleteListener(task -> {
            workmate = task.getResult().toObject(Workmate.class);
            if (Objects.requireNonNull(workmate).getChooseRestaurant() != null) {
                idRestaurant = workmate.getChooseRestaurant();
                nameRestaurant = workmate.getNameChooseRestaurant();
                addressRestaurant = workmate.getAddressChooseRestaurant();
                getWorkmateForYourRestaurant(idRestaurant, context);
            } else {
                sendVisualNotification(context);
            }
        });
    }

    // --- WORKMATES ---

    private void getWorkmateForYourRestaurant(String idRestaurant, Context context) {
        HelperWorkmate.getListWorkmates().addSnapshotListener((queryDocumentSnapShots, e) -> {
            if (queryDocumentSnapShots != null) {
                List<DocumentSnapshot> workmateList = queryDocumentSnapShots.getDocuments();
                int size = workmateList.size();
                for (int i = 0; i < size; i++) {
                    Workmate workmate = workmateList.get(i).toObject(Workmate.class);
                    if (Objects.requireNonNull(workmate).getChooseRestaurant() != null) {
                        if (workmate.getChooseRestaurant().matches(idRestaurant)) {
                            workmates.add(workmate);
                        }
                    }
                }
                sendVisualNotification(context);
            }
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
        if (Objects.requireNonNull(workmate).getChooseRestaurant() != null) {
            getNotificationWithRestaurant(context);
        } else {
            getNotificationWithoutRestaurant(context);
        }

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

        // Deliver the notification
        mNotificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }

    // --- TEXT IF THE WORKMATE NOT CHOSEN ---

    private void getNotificationWithoutRestaurant(Context context) {
        inboxStyle.setBigContentTitle(context.getString(R.string.title_notification));
        inboxStyle.addLine(context.getString(R.string.notification_not_chosen_1));
        inboxStyle.addLine(context.getString(R.string.notification_not_chosen_2));
        inboxStyle.addLine(context.getString(R.string.notification_not_chosen_3));
    }

    // --- TEXT IF THE WORKMATE CHOSEN ---

    private void getNotificationWithRestaurant(Context context) {
        inboxStyle.setBigContentTitle(context.getString(R.string.title_notification));
        inboxStyle.addLine(context.getString(R.string.notification_eatAt) + nameRestaurant);
        inboxStyle.addLine(addressRestaurant);

        if (workmates.size() > 2) {
            inboxStyle.addLine(context.getString(R.string.notification_workmate_eating));
            int size = workmates.size();
            for (int i = 0; i < size; i++) {
                if (!Objects.equals(workmates.get(i).getUid(), user.getUid())) {
                    inboxStyle.addLine(workmates.get(i).getNameWorkmate());
                }
            }
        }
    }
}