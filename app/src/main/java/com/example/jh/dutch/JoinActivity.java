package com.example.jh.dutch;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class JoinActivity extends AppCompatActivity {
    private static final String TAG = "MyFirebaseIDService";
    private EditText _id;
    private EditText _pwd;
    private EditText _check;
    // for DB connect
    String link="http://35.194.232.116/dutch/join.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        this._id = (EditText)findViewById(R.id.et_inputID);
        this._pwd = (EditText)findViewById(R.id.et_inputPwd);
        this._check = (EditText)findViewById(R.id.et_pwdCheck);
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


    // 확인 버튼을 누를 경우
    public void join(View v){
        String id   = this._id.getText().toString();
        String pwd  = this._pwd.getText().toString();
        String chk = this._check.getText().toString();

        // check id and password from DB
        if (pwd.equals(chk)){
            if(id.getBytes().length >0 && pwd.getBytes().length >0){
                inputMember2DB i2d = new inputMember2DB();
                i2d.execute(id,pwd);
            }else{
                Toast.makeText(this,"ID와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this,"비밀번호 확인이 다릅니다.", Toast.LENGTH_SHORT).show();
        }
    }
    /*
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token : " + token);

        sendRegistrationToServer(token);
    }
    */
    class inputMember2DB extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            try{
                String id = strings[0];
                String pwd = strings[1];
                String token = FirebaseInstanceId.getInstance().getToken();

                String data  = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                data += "&" + URLEncoder.encode("pwd", "UTF-8") + "=" + URLEncoder.encode(pwd, "UTF-8");
                data += "&" + URLEncoder.encode("token", "UTF-8") + "=" + URLEncoder.encode(token, "UTF-8");

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
            try{
                if(s.equalsIgnoreCase("success")){
                    showAlert("회원가입 성공");
                }else{
                    Toast.makeText(JoinActivity.this,"이미 존재하는 ID 입니다.", Toast.LENGTH_LONG).show();
                }
            }catch (Exception e){
                Log.e("Error","Exception: " + e.getMessage());
            }
        }
    }
    public void showAlert(String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // 알림창 제목 셋팅
        alertDialogBuilder.setTitle("회원가입");

        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage( message )
                .setCancelable( false )
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();
        // 다이얼로그 보여주기
        alertDialog.show();
    }
}
