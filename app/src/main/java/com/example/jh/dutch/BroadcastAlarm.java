package com.example.jh.dutch;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


public class BroadcastAlarm extends BroadcastReceiver {
    String link = "http://35.194.232.116/dutch/alarm.php";
    Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

        String userID = intent.getExtras().getString("id");

        ReadID readID = new ReadID();
        readID.execute(userID);
    }

    //일정시간 알람 noti 설정
    public void showNotification() {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent
                .getActivity(context, 0, new Intent(context, MainActivity.class),
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

        builder.setSmallIcon(R.drawable.won_white)
                .setTicker("더치론")
                .setContentTitle("더치론")
                .setContentText("돈을 갚으셨나요?")
                .setWhen(System.currentTimeMillis())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        notificationManager.notify(1, builder.build());
    }

    class ReadID extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                String id = params[0];

                String data = URLEncoder.encode("id", "UTF-8")
                        + "=" + URLEncoder.encode(id, "UTF-8");
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                while( (line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();

            } catch (Exception e) {
                Log.e("Error", "Exception: " + e.getMessage());
            }
            return null;
        }

        protected void onPostExecute(String a) {
            super.onPostExecute(a);
            if (a.equalsIgnoreCase("exist")){
                    showNotification();
            }
        }
    }
}
