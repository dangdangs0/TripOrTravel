package com.example.trip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomnavigation.LabelVisibilityMode;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    First firstFragment;    //일정
    Second secondFragment;  //투어
    SettingFragment settingFragment;    //설정
    BottomNavigationView bottomNavigation;
    String ID;
    String AREA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firstFragment=new First();
        secondFragment=new Second();
        settingFragment=new SettingFragment();

        //LoginActivity에서 받아옴
        Intent intent=getIntent();
        ID=intent.getStringExtra("ID");
        AREA=intent.getStringExtra("AREA");
        //System.out.println("ID: "+ID+" AREA: "+AREA);

//        Bundle bundle=new Bundle();
//        bundle.putString("ID",ID);
//        bundle.putString("AREA",AREA);
//        secondFragment.setArguments(bundle);

        //현재 로그인된 사용자의 id와 관심지역 저장
        SharedPreferences info=getSharedPreferences("Info", Activity.MODE_PRIVATE);
        SharedPreferences.Editor infoEdit=info.edit();
        infoEdit.putString("userID",ID);
        infoEdit.putString("userArea",AREA);
        infoEdit.commit();

        //처음에 투어화면이 나오도록 설정
        getSupportFragmentManager().beginTransaction().replace(R.id.container,secondFragment).commit(); 
        bottomNavigation=(BottomNavigationView) findViewById(R.id.bottom_menu);
        bottomNavigation.setItemIconTintList(null);
        bottomNavigation.setSelectedItemId(R.id.second_tab);    //처음에 투어가 선택되도록 설정
        //bottomNavigation.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_SELECTED);//선택된것만 이름 띄우는거

        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.first_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, firstFragment).commit();
                        return true;

                    case R.id.second_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, secondFragment).commit();
                        return true;

                    case R.id.third_tab:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, settingFragment).commit();
                        return true;
                }
                return false;
            }
        });


    }
}