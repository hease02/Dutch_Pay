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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class CompleteActivity extends AppCompatActivity {
    String link = "http://35.194.232.116/dutch/complete.php";

    String money;
    String date;
    String bank;
    String account;

    ArrayList<Member> member;
    ArrayList<Member> members = new ArrayList<>();

    TextView tv_money;
    TextView tv_date;
    TextView tv_bank;
    TextView tv_account;
    ListView lv_rent;
    String loan;

    //final TextView revise_money;
    RentMemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Dutch Activity로부터 받아온 인자들
        Intent intent = getIntent();
        member = (ArrayList<Member>) intent.getSerializableExtra("member");
        money = intent.getExtras().getString("money");
        date = intent.getExtras().getString("date");
        bank = intent.getExtras().getString("bank");
        account = intent.getExtras().getString("account");
        loan = intent.getExtras().getString("loan");

        tv_money = (TextView) findViewById(R.id.tv_money);
        tv_date = (TextView) findViewById(R.id.tv_date);
        tv_bank = (TextView) findViewById(R.id.tv_Bank);
        tv_account = (TextView) findViewById(R.id.tv_account);
        lv_rent = (ListView) findViewById(R.id.lv_rent);

        tv_money.setText(money);
        tv_date.setText(date);
        tv_bank.setText(bank);
        tv_account.setText(account);

        // 인원수에 맞게 돈을 나눔
        int len = member.size();
        int individual = Integer.parseInt(money) / len;
        for (int i = 0; i < len; i++) {
            Member rm = new Member();

            rm.id = member.get(i).id;
            rm.rate = member.get(i).rate;
            rm.money = Integer.toString(individual);

            members.add(rm);
        }
        adapter = new RentMemberAdapter(getApplicationContext(), 0, members);
        lv_rent.setAdapter(adapter);
        lv_rent.setOnItemClickListener(itemClickListener);
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


    // 리스트를 클릭하면 돈을 수정할 수 있는 alertDialog를 띄움
    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(final AdapterView<?> adapterView, View view, final int position, long l) {
            // 돈 수정을 위한 새로운 EditText();
            final EditText revise_money = new EditText(CompleteActivity.this);
            revise_money.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            final AlertDialog.Builder ab = new AlertDialog.Builder(CompleteActivity.this);
            ab.setTitle("수정할 금액 입력");
            ab.setMessage("수정할 금액을 입력해주세요");
            ab.setView(revise_money);
            ab.setPositiveButton("수정", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String m = revise_money.getText().toString();
                    if (m.getBytes().length > 0) {
                        Member rm = (Member) adapterView.getAdapter().getItem(position);
                        rm.money = m;
                        adapter.notifyDataSetChanged();
                        dialog.cancel();
                    } else {
                        Toast.makeText(CompleteActivity.this, "수정할 금액을 입력하세요", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            ab.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            ab.show();
        }
    };

    public void Complete(View view) {
        // retmembers arrayList to jsonArray
        JSONObject obj = new JSONObject();
        try {
            JSONArray jarr = new JSONArray();
            for (int i = 0; i < members.size(); i++) {
                JSONObject jo = new JSONObject();
                jo.put("rent", members.get(i).id);
                jo.put("money", members.get(i).money);
                jarr.put(jo);
            }
            obj.put("loan", loan);
            obj.put("date", date);
            obj.put("bank", bank);
            obj.put("account", account);
            obj.put("item", jarr);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        inputDB io = new inputDB();
        io.execute(obj);
    }

    class inputDB extends AsyncTask<JSONObject, Void, String> {
        @Override
        protected String doInBackground(JSONObject... jsonObjects) {
            if (jsonObjects.length > 0) {
                final JSONObject jo = jsonObjects[0];
                String json = jo.toString();
                try {
                    String data = URLEncoder.encode("json", "UTF-8") + "=" + URLEncoder.encode(json, "UTF-8");
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
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                if (s.equalsIgnoreCase("success")) {
                    Toast.makeText(CompleteActivity.this, "더치페이가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent( getApplicationContext(), StateActivity.class );
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("id",loan );
                    startActivity( intent );
                } else {
                    Toast.makeText(CompleteActivity.this, "다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e("Error", "Exception: " + e.getMessage());
            }
        }
    }
}
