package com.example.jh.dutch;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

class LoanParser {
    private static final String TAG_RESULTS = "result";
    private static final String TAG_ID = "id";
    private static final String TAG_RATE = "rate";
    private static final String TAG_MONEY = "money";
    private static final String TAG_DATE= "date";

    ArrayList<DutchMember> getList(String geturl, String id) {
        BufferedReader bufferedReader;
        try {
            String data = URLEncoder.encode("id","UTF-8")+"=" + URLEncoder.encode(id,"UTF-8");

            URL url = new URL(geturl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

            wr.write(data);
            wr.flush();

            bufferedReader  = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder sb = new StringBuilder();

            String json;
            while((json = bufferedReader.readLine())!= null){
                sb.append(json+"\n");
            }
            return returnList(sb.toString().trim());
        }catch(Exception e){
            return null;
        }
    }
    private ArrayList<DutchMember> returnList(String trim){
        ArrayList<DutchMember> tmp = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(trim);
            JSONArray jsonarray = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0 ; i < jsonarray.length() ; i++ ){
                JSONObject c = jsonarray.getJSONObject(i);

                String id = c.getString(TAG_ID);
                String rate = c.getString(TAG_RATE);
                String money = c.getString(TAG_MONEY);
                String date = c.getString(TAG_DATE);

                DutchMember lm = new DutchMember(id);
                lm.info.add(rate);
                lm.info.add(money);
                lm.info.add(date);

                tmp.add(lm);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tmp;
    }
}
