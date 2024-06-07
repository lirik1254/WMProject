package org.hse.mylaundryapplication;

import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import static org.hse.mylaundryapplication.SettingsActivity.PERMISSION;

import static org.hse.mylaundryapplication.SettingsActivity.PERMISSION1;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "CHANNEL_ID";

    private NotificationManager notificationManager1;
    @Override
    public void onReceive(Context context, Intent intent) {
        int notificationId = intent.getIntExtra("notificationId", 0);
        String message = intent.getStringExtra("notificationMessage");
        // Создаем уведомление
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "CHANNEL_ID")
                .setSmallIcon(R.drawable.notificon)
                .setContentTitle("Ваша стиральная машинка")
                .setContentText("Скоро пора стираться!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Создаем интент для запуска при нажатии на уведомление
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(contentIntent);

        // Отправляем уведомление
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        notificationManager1 = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int permissionCheck = ActivityCompat.checkSelfPermission(context, PERMISSION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {}
        else {

            createChannelIfNeeded(notificationManager1);
            notificationManager.notify(notificationId, builder.build());
    Log.d("tag", "hey wey");
        }
    }

    public static void createChannelIfNeeded(NotificationManager manager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(notificationChannel);
        }
    }

}