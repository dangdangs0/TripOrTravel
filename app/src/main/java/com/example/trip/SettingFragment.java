package com.example.trip;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SettingFragment extends PreferenceFragmentCompat {
    private static final String SETTING_PW="change_password";
    private static final String SETTING_AREA="interest_area_list";
    private static final String LOGOUT="logout";
    private static final String DELETE="delete_user";
    SharedPreferences prefs;
    SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    String ID;
    String AREA;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings_preference);
        prefs= PreferenceManager.getDefaultSharedPreferences(getActivity());


        SharedPreferences info= getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE);
        ID=info.getString("userID",null);
        AREA=info.getString("userArea",null);

        System.out.println("ID="+ID+" AREA= "+AREA);
        prefs.edit().putString("change_password","").commit();
        prefs.edit().putString("interest_area_list","지역을 선택하세요.").commit();

        prefListener= new SharedPreferences.OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if(key.equals(SETTING_PW)){
//                    System.out.println(key+"selected");
                    if(prefs.getString("change_password","").equals("")){
                        System.out.println("nothing");
                        Toast.makeText(getActivity(), "입력된 값이 없습니다", Toast.LENGTH_SHORT).show();
                    }
                    else{
//                        System.out.println(prefs.getString("change_password","").toString());
                        //AsyncTask 실행
                        settingPW sp=new settingPW();
                        sp.execute();
                    }
                }
                else if(key.equals(SETTING_AREA)){
                    if(prefs.getString("interest_area_list","").equals("지역을 선택하세요.")){
                        System.out.println("nothing");
                        Toast.makeText(getActivity(), "지역을 선택해주세요", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        info.edit().putString("userArea", prefs.getString("interest_area_list","")).commit();
                        System.out.println(key + "selected");
                        settingArea sa = new settingArea();
                        sa.execute();
                    }
                }
            }
        };
        prefs.registerOnSharedPreferenceChangeListener(prefListener);

        Preference logoutBtn=findPreference(LOGOUT);
        logoutBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent=new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                info.edit().clear().commit();
                return false;
            }
        });

        Preference delBtn=findPreference(DELETE);
        delBtn.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                settingDel sd = new settingDel();
                sd.execute();
                Intent intent=new Intent(getActivity(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);


                return false;

                //php 해서 회원탈퇴 시켜야됨됨
            }
       });
    }

    public class settingPW extends AsyncTask<Void, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String value;
                String params = "Id=" + ID + "&Pw=" + prefs.getString("change_password","");//파라미터
                URL url = new URL("http://tot.dothome.co.kr/ChangePW.php");//url 지정

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//보내는 방식
                conn.setRequestProperty("Accept", "application/json");//받는방식 json
                conn.setRequestMethod("POST");//요청방식 : POST

                conn.setDoInput(true);//InputStream으로 서버로부터 응답받겠다는 옵션
                conn.setDoOutput(true);//OutputStream으로 POST 데이터 넘겨주겠다는 옵션

                OutputStream outs = conn.getOutputStream();
                OutputStreamWriter outs_w = new OutputStreamWriter(outs, StandardCharsets.UTF_8);//받아오는거 utf-8 인코딩
                outs_w.write(params);//params를 POST 형식으로 데이터 넘겨줌
                outs_w.flush();//버퍼에 데이터 모두 출력시키고 비움
                outs_w.close();//자원풀기
                outs.close();//풀기

                InputStream is = null;
                BufferedReader in = null;
                String data = "";

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is));
                String line = null;
                StringBuffer buff = new StringBuffer();

                while ((line = in.readLine()) != null) {//버퍼로 데이터 읽기
                    buff.append(line);
                }

                if(buff.toString().equals("good")){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "비밀번호 변경완료", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "비밀번호 변경실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (MalformedURLException | ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class settingArea extends AsyncTask<Void, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String value;
                String params = "Id=" + ID + "&Area=" + prefs.getString("interest_area_list","");//파라미터
                URL url = new URL("http://tot.dothome.co.kr/ChangeArea.php");//url 지정

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//보내는 방식
                conn.setRequestProperty("Accept", "application/json");//받는방식 json
                conn.setRequestMethod("POST");//요청방식 : POST

                conn.setDoInput(true);//InputStream으로 서버로부터 응답받겠다는 옵션
                conn.setDoOutput(true);//OutputStream으로 POST 데이터 넘겨주겠다는 옵션

                OutputStream outs = conn.getOutputStream();
                OutputStreamWriter outs_w = new OutputStreamWriter(outs, StandardCharsets.UTF_8);//받아오는거 utf-8 인코딩
                outs_w.write(params);//params를 POST 형식으로 데이터 넘겨줌
                outs_w.flush();//버퍼에 데이터 모두 출력시키고 비움
                outs_w.close();//자원풀기
                outs.close();//풀기

                InputStream is = null;
                BufferedReader in = null;
                String data = "";

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is));
                String line = null;
                StringBuffer buff = new StringBuffer();

                while ((line = in.readLine()) != null) {//버퍼로 데이터 읽기
                    buff.append(line);
                }

                if(buff.toString().equals("failed")){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "관심지역 변경실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "관심지역 변경완료", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (MalformedURLException | ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public class settingDel extends AsyncTask<Void, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                String value;
                String params = "ID=" + ID;//파라미터
                URL url = new URL("http://tot.dothome.co.kr/DelUser.php");//url 지정

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//보내는 방식
                conn.setRequestProperty("Accept", "application/json");//받는방식 json
                conn.setRequestMethod("POST");//요청방식 : POST

                conn.setDoInput(true);//InputStream으로 서버로부터 응답받겠다는 옵션
                conn.setDoOutput(true);//OutputStream으로 POST 데이터 넘겨주겠다는 옵션

                OutputStream outs = conn.getOutputStream();
                OutputStreamWriter outs_w = new OutputStreamWriter(outs, StandardCharsets.UTF_8);//받아오는거 utf-8 인코딩
                outs_w.write(params);//params를 POST 형식으로 데이터 넘겨줌
                outs_w.flush();//버퍼에 데이터 모두 출력시키고 비움
                outs_w.close();//자원풀기
                outs.close();//풀기

                InputStream is = null;
                BufferedReader in = null;
                String data = "";

                is = conn.getInputStream();
                in = new BufferedReader(new InputStreamReader(is));
                String line = null;
                StringBuffer buff = new StringBuffer();

                while ((line = in.readLine()) != null) {//버퍼로 데이터 읽기
                    buff.append(line);
                }

                if(buff.toString().equals("success")){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "회원탈퇴 성공", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), "회원탈퇴 실패", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (MalformedURLException | ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
