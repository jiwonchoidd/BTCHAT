package com.nsu.btchat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class MenuActivity extends AppCompatActivity {
    private BackPressedForFinish backPressedForFinish;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);
        //뒤로가기 눌렀을때 종료되게
        backPressedForFinish = new BackPressedForFinish(this);
        Button guest_bnt=(Button)findViewById(R.id.guestbnt);
        Button host_bnt=(Button)findViewById(R.id.hostbnt);
        TextView userTv=(TextView)findViewById(R.id.menu_tv);
        Intent intent= getIntent();
        username = intent.getStringExtra("username");
        //~누구누구님 대화를 시작해볼까요
        userTv.setText(username);
        // lcw126.tistory.com/92 마시멜로우버젼부터 동적 퍼미션을 필요로함
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED){
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},10);
            }
        }
        host_bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //호스트
                Intent intent = new Intent(getApplicationContext(), HostActivity.class);
                startActivity(intent);
            }
        });
        guest_bnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //게스트
                Intent intent = new Intent(getApplicationContext(), GuestActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions,grantResults);
        switch (requestCode){
            case 10:
                if(grantResults[0]==PackageManager.PERMISSION_DENIED){
                    Toast.makeText(this,"새로운 장치를 탐색하는 기능을 제한합니다 \n 기존에 헤어링된 장치가 있다면 접속 가능함", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater =getMenuInflater();
        inflater.inflate(R.menu.dotmenu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_tutorial:
                SharedPreferences main = getSharedPreferences("main", MODE_PRIVATE);
                SharedPreferences.Editor editor = main.edit();
                editor.putInt("main", 0); //튜토리얼을 봤으니 1을 저장함
                editor.commit(); //다시 초기화
                Intent intent1 = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent1);
                return true;
            case R.id.menu_changename:
                Intent intent2 = new Intent(getApplicationContext(), UserNameActivity.class);
                startActivity(intent2);
                return true;
            case R.id.menu_developer:
                AlertDialog.Builder dlg = new AlertDialog.Builder(MenuActivity.this);
                dlg.setTitle("개발자 소개"); //제목
                dlg.setMessage(R.string.developer); // 메시지
                dlg.setIcon(R.drawable.ic_launcher_web); // 아이콘 설정
                //버튼 클릭시 동작
                dlg.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                    // 취소
                });
                dlg.setPositiveButton("이메일", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //토스트 메시지
                    Toast.makeText(MenuActivity.this, "이메일 전송", Toast.LENGTH_SHORT).show();
                    //이메일 인텐트
                    Intent email = new Intent(Intent.ACTION_SEND);
                    email.setType("plain/text");
                    // email setting 배열로 해놔서 복수 발송 가능
                    String[] address = {"jae2021@gmail.com"};
                    email.putExtra(Intent.EXTRA_EMAIL, address);
                    email.putExtra(Intent.EXTRA_SUBJECT,"BTCHAT");
                    email.putExtra(Intent.EXTRA_TEXT,"BTCHAT앱을 사용중인 "+username+"입니다.");
                    startActivity(email);
                }
            });

                dlg.show();
        return true;
        default:
        return super.onOptionsItemSelected(item);
    }
}
    @Override public void onBackPressed() {
        backPressedForFinish.onBackPressed();
    }
}


