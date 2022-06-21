package com.example.trip;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

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

public class LoginActivity extends AppCompatActivity {

    Button button;  //login button
    EditText editTextId;
    EditText editTextPw;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        button=(Button)findViewById(R.id.button);
        editTextId = (EditText) findViewById(R.id.userid);
        editTextPw = (EditText) findViewById(R.id.userpw);
    }

    public void onClick_join(View view){//회원가입 버튼 클릭 시 회원가입 페이지로 이동
        Intent intent = new Intent(getApplicationContext(), JoinActivity.class);
        startActivity(intent);
    }

    public void onClick_login(View view){   //로그인 버튼 클릭 시 
        if(editTextId.getText().toString().equals("")){ //editTextId가 공백인 경우
            Toast.makeText(getApplicationContext(),"id를 입력하세요", Toast.LENGTH_SHORT).show();
        }
        else if(editTextPw.getText().toString().equals("")){    //editTextPw가 공백인 경우
            Toast.makeText(getApplicationContext(),"비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
        }
        else{
            login_request lr = new login_request();
            lr.execute();
        }
    }

    //사용자가 입력한 id, pw를 조회하여 가입되어 있는 회원여부 판단
    public class login_request extends AsyncTask<Void, Void, String> { //<Params, Progress, Result>
        //onPreExecute -> doInBackground -> onPostExecute

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {    //s는 doInBackground의 리턴값
            super.onPostExecute(s);
            //입력한 정보 초기화
            editTextId.setText("");
            editTextPw.setText("");
        }

        @Override
        public String doInBackground(Void... strings) {
            try {
                String value;
                String params = "ID=" + editTextId.getText().toString() + "&PW=" + editTextPw.getText().toString(); //파라미터

                URL url = new URL("http://tot.dothome.co.kr/Login.php");//url 지정

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

                if(!buff.toString().equals("failed")){
                    runOnUiThread(new Runnable() {  //asynctask toast message
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "login success", Toast.LENGTH_SHORT).show();
                        }
                    });
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class); //메인 액티비티!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                    intent.putExtra("ID",editTextId.getText().toString());
                    intent.putExtra("AREA",buff.toString());
                    startActivity(intent);
                }
                else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "login failed", Toast.LENGTH_SHORT).show();
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