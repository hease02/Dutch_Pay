package com.example.jh.dutch;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class LoanActivity extends AppCompatActivity {
    String link="http://35.194.232.116/dutch/loan.php";
    String link2="http://35.194.232.116/dutch/send.php";
    String userID;
    LoanParser jp = new LoanParser();
    ExpandableListView elv;
    ExpandedAdapter ea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        userID = intent.getExtras().getString("id");

        elv = (ExpandableListView)findViewById(R.id.expandableListView);

        getList gl = new getList();
        gl.execute();
    }

    //액션 바 뒤로 가기 버튼 활성화
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean dateCompare(String d1){
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd", Locale.KOREA);
        Date day1 = null, day2 = null;
        Date curTime = new Date();
        String current = format.format(curTime);
        try {
            day1 = format.parse(d1);
            day2 = format.parse(current);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int compare = day1.compareTo(day2);
        if( compare > 0 ) return true;
        else if(compare < 0 ) return false;
        else return true;
    }
    class elvListener implements ExpandableListView.OnChildClickListener{
        @Override
        public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
            String time = ea.getChild(i,i1).get(2);
            String id = ea.getGroup(i).toString();
            if(!dateCompare(time)) {
                showAlert(id);
            }else{
                Toast.makeText(getApplicationContext(),"아직 날짜가 지나지 않았습니다.",Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    }
    class inputId2DB extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                //리스트에서 선택하여 푸시를 받을 사용자 ID
                String id = params[0];
                //String id = "A";

                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");

                URL url = new URL(link2);
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
            }catch (Exception e) {
                Log.e("Error", "Exception: " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Toast.makeText(getApplicationContext(),"알림 전송이 완료되었습니다.",Toast.LENGTH_SHORT).show();
        }
    }

    public void showAlert(final String id) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("더치페이");

        alertDialogBuilder
                .setMessage( "사용자에게 알림을 보내시겠습니까?" )
                .setCancelable( false )
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                inputId2DB i2d = new inputId2DB();
                                i2d.execute(id);
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class getList extends AsyncTask<String, String, ArrayList<DutchMember>>{
        @Override
        protected ArrayList<DutchMember> doInBackground(String... strings) {
            return jp.getList(link,userID);
        }

        @Override
        protected void onPostExecute(ArrayList<DutchMember> dutchMembers) {
            super.onPostExecute(dutchMembers);
            String type = "loan";
            ea = new ExpandedAdapter(getApplicationContext(),type, dutchMembers);
            elv.setAdapter(ea);
            elv.setOnChildClickListener(new elvListener());
        }
    }
}