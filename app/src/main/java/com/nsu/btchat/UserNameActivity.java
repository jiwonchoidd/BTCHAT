package com.nsu.btchat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class UserNameActivity extends AppCompatActivity {
    private BackPressedForFinish backPressedForFinish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //커스텀위젯을 넣은 유저네임액티비티 레이아웃
        setContentView(R.layout.activity_username);
        final CheckBox customCheckbox = (CheckBox)findViewById(R.id.user_checkbox);
        final EditText customEdit = (EditText) findViewById(R.id.user_edittext);
        final Button customBtn = (Button)findViewById(R.id.user_button);
        backPressedForFinish = new BackPressedForFinish(this);
        // 만약에 이전에 체크박스이벤트로 쒜어드에 저장이 되어있다면 불러옴
        final SharedPreferences saveName = getSharedPreferences("saveName", MODE_PRIVATE);
        String nameData = saveName.getString("saveName", "");
        if(nameData!=""){
           customEdit.setText(nameData);
        }

        customCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(customCheckbox.isChecked()==false){
                    Toast.makeText(getApplicationContext(), "이름을 기억하지 않습니다.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        customBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customCheckbox.isChecked()&&customEdit.getText().toString().length() != 0){
                    //만약 체크박스가 true이라면 값을 저장해줘해야지 길이가 0이면 안됨
                    String yourname=customEdit.getText().toString();
                    nameSharedPref(yourname);
                    Toast.makeText(getApplicationContext(), yourname+" 이름으로 입장합니다.",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    intent.putExtra("username", yourname);
                    startActivity(intent);
                }
                else if(customEdit.getText().toString().length() != 0){
                    String yourname=customEdit.getText().toString();
                    //체크를 안했다면 저장되어있는 내용 지워드립니다.
                    nameSharedPref("");
                    Toast.makeText(getApplicationContext(), yourname+" 이름으로 입장합니다.",
                            Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                    intent.putExtra("username", yourname);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "아무것도 입력하지 않았습니다.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
    @Override public void onBackPressed() {
        backPressedForFinish.onBackPressed();
    }
    // getSharedPreferences 함수 스트링 받아와서 넣어버리자
    void nameSharedPref(String one){
        SharedPreferences saveName = getSharedPreferences("saveName", MODE_PRIVATE);
        SharedPreferences.Editor editor = saveName.edit();
        //에딧텍스트 내용 저장
        editor.putString("saveName",one);
        editor.commit(); //저장 완료
    }

}
