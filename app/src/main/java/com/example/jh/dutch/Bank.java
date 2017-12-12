package com.example.jh.dutch;

import android.app.Dialog;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class Bank extends AppCompatActivity {
    Button btn_bankChose;
    View bank_list_dialog;
    Dialog myDialog;

    //은행 선택 버튼 클릭
    public void onBank(View view) {
        showDialog();
    }

    //은행 선택 dialog 띄우기
    private void showDialog() {
        myDialog.setTitle("은행 선택");
        myDialog.setContentView(bank_list_dialog);
        myDialog.show();
    }

    //은행 선택 dialog에서 은행 선택하여 버튼 text 변경
    public void myBank(View view) {
        switch(view.getId()) {
            case R.id.bank1:
                btn_bankChose.setText("NH농협");
                break;
            case R.id.bank2:
                btn_bankChose.setText("KB국민");
                break;
            case R.id.bank3:
                btn_bankChose.setText("신한");
                break;
            case R.id.bank4:
                btn_bankChose.setText("전북");
                break;
            case R.id.bank5:
                btn_bankChose.setText("IBK기업");
                break;
            case R.id.bank6:
                btn_bankChose.setText("우체국");
                break;
            case R.id.bank7:
                btn_bankChose.setText("대구");
                break;
            case R.id.bank8:
                btn_bankChose.setText("우리");
                break;
            case R.id.bank9:
                btn_bankChose.setText("하나");
                break;
        }
        btn_bankChose.setTextColor(Color.parseColor("#737373"));
        myDialog.cancel();
    }
}
