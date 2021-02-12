package com.nsu.btchat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import me.relex.circleindicator.CircleIndicator;

public class MainActivity extends AppCompatActivity {
    private int mainData;
    //다른 액티비티에서 여기있는 함수 쓰기위해서 static context 씁니다.
    public static Context mContext;

    //액션바에 버튼 추가하자
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return  true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.action_bar_skip:
                skip();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }
    }
    //프래그먼트 어뎁터
    FragmentPagerAdapter adapterViewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Skip 함수 3번째 프래그먼트에서 쓰기위함임
        mContext=this;
        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        // 인디케이터를 뷰페이저에 등록
        indicator.setViewPager(vpPager);
        SharedPreferences main = getSharedPreferences("main", MODE_PRIVATE);
        //만약에 이미 튜토리얼을 봤다면 이 액티비티를 지나가게해줍니다.
        mainData = main.getInt("main", 0);
        if( mainData==1 ){
            Intent intent = new Intent(getApplicationContext(), UserNameActivity.class);
            startActivity(intent);
        }
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 3;
// 참조 https://webnautes.tistory.com/1013
        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }
        // 전체 페이지 수를 리턴
        @Override
        public int getCount()
        {

            return NUM_ITEMS;
        }
        // 페이지에 보여질 프래그먼트 리턴
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FirstFragment.newInstance(0, "Page # 1");
                case 1:
                    return SecondFragment.newInstance(1, "Page # 2");
                case 2:
                    return ThirdFragment.newInstance(2, "Page # 3");
                default:
                    return null;
            }
        }
    }

// 스킵해주는 메소드 생성
    public void skip() {
        SharedPreferences main = getSharedPreferences("main", MODE_PRIVATE);
        SharedPreferences.Editor editor = main.edit();
        editor.putInt("main", 1); //튜토리얼을 봤으니 1을 저장함
        editor.commit(); //저장 완료
        Toast.makeText(getApplicationContext(), "메뉴에서 앱설명을 다시 볼 수 있습니다.",
                Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getApplicationContext(), UserNameActivity.class);
        startActivity(intent);
    }
}

