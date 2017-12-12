package com.example.jh.dutch;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    private EditText _id;
    private EditText _pwd;
    // for auto login
    SharedPreferences setting;
    SharedPreferences.Editor editor;
    CheckBox auto_login;
    // for connect db link
    String link="http://35.194.232.116/dutch/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        auto_login = (CheckBox)findViewById(R.id.auto_login);
        this._id = (EditText)findViewById(R.id.et_id);
        this._pwd = (EditText)findViewById(R.id.et_pwd);

        setting = getSharedPreferences("setting",0);
        editor = setting.edit();

        if(setting.getBoolean("Auto_Login", false)){
            _id.setText(setting.getString("ID",""));
            _pwd.setText(setting.getString("PW",""));
            auto_login.setChecked(true);
        }

        auto_login.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    String id = _id.getText().toString();
                    String pwd = _pwd.getText().toString();

                    editor.putString("ID",id);
                    editor.putString("PW", pwd);
                    editor.putBoolean("Auto_Login",true);
                    editor.commit();
                }else{
                    editor.clear();
                    editor.commit();
                }
            }
        });
    }
    // 메인화면에서 회원가입을 눌렀을 경우 회원 가입 액티비티로 이동
    public void join(View v){
        Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
        startActivity(intent);
    }

    // 메인화면에서 id와 비밀번호를 입력한 후 로그인을 눌렀을 경우
    public void login(View v) {
        // get id and password from editText
        String id   = this._id.getText().toString();
        String pwd  = this._pwd.getText().toString();
        // check id and password from DB
        if (id.getBytes().length > 0 && pwd.getBytes().length >0 ){
            checkFromDB cfd = new checkFromDB();
            cfd.execute(id, pwd );
        }else {
            Toast.makeText(this,"ID와 비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    class checkFromDB extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... strings) {
            try{
                String id = strings[0];
                String pwd = strings[1];

                String data  = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                data += "&" + URLEncoder.encode("pwd", "UTF-8") + "=" + URLEncoder.encode(pwd, "UTF-8");

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
                    Intent intent = new Intent( getApplicationContext(), StateActivity.class );
                    intent.putExtra("id", _id.getText().toString() );
                    startActivity( intent );
                }else{
                    showAlert();
                }
            }catch (Exception e){
                Log.e("Error","Exception: " + e.getMessage());
            }
        }
    }
    public void showAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // 알림창 제목 셋팅
        alertDialogBuilder.setTitle("로그인 실패");

        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage( "아이디나 비밀번호를 확인해주세요." )
                .setCancelable( false )
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
        // 다이얼로그 생성
        AlertDialog alertDialog = alertDialogBuilder.create();
        // 다이얼로그 보여주기
        alertDialog.show();
    }
}
