package com.example.jh.dutch;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;

public class StateActivity extends AppCompatActivity {
    String userID;
    String userRate;
    RatingBar rate;
    TextView tv_rent;
    TextView tv_loan;

    AlarmManager am;

    String link="http://35.194.232.116/dutch/state.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_state);

        rate = (RatingBar)findViewById(R.id.user_rate);

        // get user id and user's rate
        Intent intent = getIntent();
        userID = intent.getExtras().getString("id");
        // Show user ID In title bar
        this.setTitle(userID);

        tv_rent = (TextView)findViewById(R.id.tv_rent);
        tv_loan = (TextView)findViewById(R.id.tv_loan);
        getStateFromDB gs = new getStateFromDB();
        gs.execute(userID);

        am = (AlarmManager) getSystemService(ALARM_SERVICE); //알람 서비스 등록

        AlarmHATT alarmHATT = new AlarmHATT(getApplicationContext());
        alarmHATT.Alarm();
    }
    public void myRent (View v) {
        Intent intent = new Intent(getApplicationContext(), RentActivity.class);
        intent.putExtra( "id", userID );
        startActivity( intent );
    }
    public void myLoan(View v) {
        Intent intent = new Intent(getApplicationContext(), LoanActivity.class);
        intent.putExtra( "id", userID );
        startActivity(intent);
    }
    public void myDutch(View v) {
        Intent intent = new Intent(getApplicationContext(), DutchActivity.class);
        intent.putExtra( "id", userID );
        intent.putExtra("rate", userRate);
        startActivity(intent);
    }

    class getStateFromDB extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            try{
                String id = strings[0];

                String data  = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write( data );
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } catch(Exception e){
                Log.e("Error","Exception: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            String[] val = s.split("/");
            rate.setRating(Float.parseFloat(val[0]));
            tv_loan.setText(val[1]);
            tv_rent.setText(val[2]);
        }
    }

    public class AlarmHATT{
        private Context context;

        AlarmHATT(Context context) {
            this.context = context;
        }

        void Alarm() {
            Intent intent = new Intent( context.getApplicationContext(), BroadcastAlarm.class);
            intent.putExtra("id",userID);

            PendingIntent sender = PendingIntent.getBroadcast(StateActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();

            //알람을 울릴 시간
            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DATE), 11, 10, 0);

            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 24*60*60*1000, sender);
        }
    }
}
