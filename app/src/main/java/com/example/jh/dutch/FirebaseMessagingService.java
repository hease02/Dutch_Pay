package com.example.jh.dutch;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService  {
    public void onMessageReceived(RemoteMessage remoteMessage) {
        sendPushNotification(remoteMessage.getData().get("message"));
    }

    //리스트 클릭 noti 설정
    private void sendPushNotification(String messageBody) {
        // 사용자가 받은 노티피케이션을 클릭시
        Intent intent = new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder.setSmallIcon(R.drawable.won_white)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.coin_color))
                .setTicker("더치론")
                .setContentTitle("더치론")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
