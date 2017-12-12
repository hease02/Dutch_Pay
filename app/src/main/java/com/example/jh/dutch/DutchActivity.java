package com.example.jh.dutch;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

// 은행 누르면 다이얼로그 나오는거 Bank.java로 빼서 extends해서 사용
public class DutchActivity extends Bank{
    String link = "http://35.194.232.116/dutch/search.php";
    int year, month, day;
    TextView tv_date, tv_id;
    RatingBar ratingBar;

    ListView lv_rentMember;
    EditText et_dutch_money;
    EditText et_findMember;
    EditText et_account;
    String userID;
    String userRate;

    MyAdapter myAdapter;

    ArrayList<Member> rentmembers = new ArrayList<>();
    ArrayList<DutchMember> rentmembers2 = new ArrayList<>() ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dutch);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        userID = intent.getExtras().getString("id");
        userRate = intent.getExtras().getString("rate");

        tv_date = (TextView) findViewById(R.id.tv_date);

        btn_bankChose = (Button) findViewById(R.id.btn_Bank);
        et_dutch_money = (EditText) findViewById(R.id.et_money);
        et_findMember = (EditText) findViewById(R.id.et_find);
        et_account = (EditText)findViewById(R.id.et_account);

        lv_rentMember = (ListView) findViewById(R.id.listView);

        // 은행 선택하는 커스텀 다이얼로그
        LayoutInflater dialog = LayoutInflater.from(this);
        bank_list_dialog = dialog.inflate(R.layout.bank_list_dialog, null);
        myDialog = new Dialog(this);

        // 달력 선택
        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
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


    // Complete all form then send info to DB
    public void DutchComplete(View v) {
        // get all information
        String m = et_dutch_money.getText().toString();
        String d = tv_date.getText().toString();
        ArrayList<DutchMember> member = rentmembers2;
        String b = btn_bankChose.getText().toString();
        String a = et_account.getText().toString();
        // validate all input data
        if( m.getBytes().length > 0  && !d.equalsIgnoreCase("날짜 입력해주세요.") && member.size() > 0
                && !b.equalsIgnoreCase("선택") && a.getBytes().length >0 ){
            // goto state activity and update state
            Intent intent = new Intent(getApplicationContext(), CompleteActivity.class);

            intent.putExtra("loan", userID);
            intent.putExtra("money",m);
            intent.putExtra("date",d);
            intent.putExtra("bank",b);
            intent.putExtra("account",a);
            intent.putExtra("member", rentmembers);

            startActivity(intent);
        }else{
            showAlert("더치페이", "입력되지않은 필수값이 있습니다.");
        }
    }

    public void dateClick(View target) {
        new DatePickerDialog(DutchActivity.this, dateSetListener, year, month, day).show();
    }
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            String msg = String.format("%d/%d/%d", year, month + 1, dayOfMonth);
            tv_date.setText(msg);
        }
    };

    // get rent member and search
    public void searchMember(View v) {
        String member = et_findMember.getText().toString();
        searchDB sd = new searchDB();
        sd.execute(member);
    }
    private class searchDB extends AsyncTask<String, Void, String> {
        String id;
        @Override
        protected String doInBackground(String... strings) {
            try {
                id = strings[0];
                String data = URLEncoder.encode("id", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");

                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                wr.write(data);
                wr.flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                return sb.toString();
            } catch (Exception e) {
                Log.e("Error", "Exception: " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (!s.equalsIgnoreCase("fail")) {
                    Member member = new Member();
                    member.id = id;
                    member.rate = s;

                    rentmembers.add(member);

                    DutchMember dutchMember = new DutchMember(id);
                    dutchMember.info.add(s);

                    rentmembers2.add(dutchMember);

                    myAdapter = new MyAdapter(DutchActivity.this, R.layout.id_ratingbar, rentmembers2) ;

                    lv_rentMember.setAdapter(myAdapter);
                    lv_rentMember.setOnItemClickListener(itemClickListener);
                }else {
                    showAlert("사용자 검색", "해당 사용자가 존재하지 않습니다.");
                }
            } catch (Exception e) {
                Log.e("Error", "Exception: " + e.getMessage());
            }
            et_findMember.setText("");
        }
    }

    public void showAlert(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // 알림창 제목 셋팅
        alertDialogBuilder.setTitle(title);

        // AlertDialog 셋팅
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
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

    private class MyAdapter extends ArrayAdapter<DutchMember> {
        private ArrayList<DutchMember> items;

        public MyAdapter(Context context, int resource, ArrayList<DutchMember> objects) {
            super(context, resource, objects);
            this.items = objects;
        }

        public View getView(int position, View convertView, ViewGroup parent){
            View v = convertView ;

            if(v == null){
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.id_ratingbar, null);
            }

            DutchMember dutchMember = items.get(position);

            if(dutchMember != null) {
                tv_id = (TextView) v.findViewById(R.id.tv_id);
                ratingBar = (RatingBar) v.findViewById(R.id.ratingBar) ;

                tv_id.setText(dutchMember.id);
                ratingBar.setRating(Float.parseFloat(dutchMember.info.get(0)));
            }
            return v;
        }
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(DutchActivity.this) ;

            alertDialog.setTitle("주의");
            alertDialog.setMessage("삭제하시겠습니까?");
            alertDialog.setPositiveButton("예", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //삭제 상황 입력
                    rentmembers.remove(position);
                    rentmembers2.remove(position);

                    myAdapter.notifyDataSetChanged();

                    dialog.cancel();
                }
            });

            alertDialog.setNegativeButton("아니오", new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertDialog.show();
        }
    };
}
