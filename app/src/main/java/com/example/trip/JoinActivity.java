package com.example.trip;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

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
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class JoinActivity extends AppCompatActivity {

    private EditText editTextId;
    private EditText editTextPw;
    private EditText editTextPw2;   //비밀번호확인 editText
    Spinner area;   //관심지역 드롭메뉴
    int idcheck_flag=0; //중복확인 클릭 여부 체크

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        editTextId = (EditText) findViewById(R.id.new_id);
        editTextPw = (EditText) findViewById(R.id.new_pw);
        editTextPw2 = (EditText) findViewById(R.id.new_pw2);
        area=(Spinner)findViewById(R.id.area);

        //spinner에 드롭다운 메뉴 적용(관심지역)
        String[] str=getResources().getStringArray(R.array.areas);  //res/values/arrays.xml
        final ArrayAdapter<String> adapter=new ArrayAdapter<String>(getApplicationContext(),R.layout.arrays_layout,str);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        area.setAdapter(adapter);
        //관심지역 클릭 시 이벤트
        area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (area.getSelectedItemPosition() > 0) {
                    Log.v("알람",area.getSelectedItem().toString()+" is selected");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //다른 아이디로 중복확인 후 아이디 변경->확인되는 것 방지
        editTextId.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    idcheck_flag=0;
                }
            }
        });
    }

    //확인버튼 클릭 시 이벤트
    public void insert(View view) {
        String Id = editTextId.getText().toString();
        String Pw = editTextPw.getText().toString();
        String Pw2 = editTextPw2.getText().toString();
        String Area = area.getSelectedItem().toString();

        if(Id.equals("")){
            Toast.makeText(getApplicationContext(),"id를 입력하세요", Toast.LENGTH_LONG).show();
        }
        else if(Pw.equals("")){
            Toast.makeText(getApplicationContext(),"pw를 입력하세요", Toast.LENGTH_LONG).show();
        }
        else if(!Pw.equals(Pw2)){
            Toast.makeText(getApplicationContext(),"입력하신 비밀번호가 다릅니다", Toast.LENGTH_LONG).show();
        }
        else if(area.getSelectedItemPosition()==0){
            Toast.makeText(getApplicationContext(),"관심지역을 선택해주세요", Toast.LENGTH_LONG).show();
        }
        else {
            if (idcheck_flag == 0) {
                Toast.makeText(getApplicationContext(), "중복확인 해주세요.", Toast.LENGTH_LONG).show();
            } else if (idcheck_flag == 1) {
                Toast.makeText(getApplicationContext(), "중복된 아이디입니다.", Toast.LENGTH_LONG).show();
            } else {
                insertoToDatabase(Id, Pw, Area); //중복확인이랑 비밀번호 확인 한 후에 이거실행

            }
        }
    }

    //사용자가 입력한 정보를 받아 회원으로 등록
    private void insertoToDatabase(String Id, String Pw, String Area) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(JoinActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();  //progressDialog 종료
                Toast.makeText(getApplicationContext(), "회원가입이 완료되었습니다.", Toast.LENGTH_LONG).show();
                finish();   //LoginActivity로 돌아감
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    String Id = (String) params[0];
                    String Pw = (String) params[1];
                    String Area=(String) params[2];

                    String link = "http://tot.dothome.co.kr/Join.php";
                    String data = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8");
                    data += "&" + URLEncoder.encode("Pw", "UTF-8") + "=" + URLEncoder.encode(Pw, "UTF-8");
                    data += "&" + URLEncoder.encode("Area", "UTF-8") + "=" + URLEncoder.encode(Area, "UTF-8");

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
                        break;
                    }
                    System.out.println(sb.toString());
                    return sb.toString();   //true
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
        InsertData task = new InsertData();
        task.execute(Id, Pw, Area);
    }

    //사용자가 입력한 id가 기존에 입력된 아이디와 중복되는지 여부 확인 1=이미 존재, 2=사용가능
    public class idcheck_request extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        public String doInBackground(Void... strings) {
            try {
                String value;
                String params = "ID=" + editTextId.getText().toString();//파라미터

                URL url = new URL("http://tot.dothome.co.kr/Idcheck.php");//url 지정

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

                if(buff.toString().equals("exist")){
                    idcheck_flag=1;
                    runOnUiThread(new Runnable() {  //asynctask toast message
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "중복된 id 입니다", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else{
                    idcheck_flag=2;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "사용할 수 있는 아이디", Toast.LENGTH_LONG).show();
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

    public void onClick_idcheck(View view){
        if(editTextId.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"id를 입력하세요", Toast.LENGTH_LONG).show();
        }
        else{
            idcheck_request Ir = new idcheck_request();
            Ir.execute();
        }
    }

    public void onClick_cancel(View view){
        finish();   //LoginActivity로 돌아감
    }



}