package com.example.trip;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class PickDateActivity extends AppCompatActivity {
    private int startYear, startMonth,startDay;
    private int endYear, endMonth,endDay;
    String Id,Area,Sigungu,Tour_spot,Startdate,Enddate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_date);

        SharedPreferences info= getSharedPreferences("Info", Context.MODE_PRIVATE);
        Id=info.getString("userID",null);
        Area=getIntent().getStringExtra("selectedArea");
        Sigungu=getIntent().getStringExtra("selectedSigungu");
        Tour_spot=getIntent().getStringExtra("selectedTourspot");

        Calendar calendar=new GregorianCalendar();
        startYear=calendar.get(Calendar.YEAR);
        startMonth=calendar.get(Calendar.MONTH);
        startDay=calendar.get(Calendar.DAY_OF_MONTH);

        endYear=calendar.get(Calendar.YEAR);
        endMonth=calendar.get(Calendar.MONTH);
        endDay=calendar.get(Calendar.DAY_OF_MONTH);

        DatePicker startdatePicker=findViewById(R.id.startDatePicker);
        DatePicker enddatePicker=findViewById(R.id.endDatePicker);

        startdatePicker.init(startYear,startMonth,startDay,startOnDateChangedListener);
        enddatePicker.init(endYear,endMonth,endDay, endOnDateChangedListener);

    }

    DatePicker.OnDateChangedListener startOnDateChangedListener=new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            startYear=year;
            startMonth=monthOfYear;
            startDay=dayOfMonth;
        }

    };

    DatePicker.OnDateChangedListener endOnDateChangedListener =new DatePicker.OnDateChangedListener() {
        @Override
        public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            endYear=year;
            endMonth=monthOfYear;
            endDay=dayOfMonth;
        }
    };

    public void OnClickOkButton(View v){
        saveDate task = new saveDate();
        Startdate=startYear+"-"+(startMonth+1)+"-"+startDay;
        Enddate=endYear+"-"+(endMonth+1)+"-"+endDay;
        //System.out.println("시작: "+Startdate+" 끝: "+Enddate);
        task.execute(Id,Area,Sigungu,Tour_spot,Startdate,Enddate);
    }

    public void OnClickCancelButton(View v){
        finish();
    }

    public class saveDate extends AsyncTask<String, Void, String>  {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(PickDateActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(getApplicationContext(), "일정 추가완료", Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String Id = (String) params[0];
                    String Area = (String) params[1];
                    String Sigungu=(String) params[2];
                    String Tour_spot = (String) params[3];
                    String Startdate = (String) params[4];
                    String Enddate=(String) params[5];

                    String link = "http://tot.dothome.co.kr/saveDate.php";
                    String data = URLEncoder.encode("Id", "UTF-8") + "=" + URLEncoder.encode(Id, "UTF-8");
                    data += "&" + URLEncoder.encode("Area", "UTF-8") + "=" + URLEncoder.encode(Area, "UTF-8");
                    data += "&" + URLEncoder.encode("Sigungu", "UTF-8") + "=" + URLEncoder.encode(Sigungu, "UTF-8");
                    data += "&" + URLEncoder.encode("Tour_spot", "UTF-8") + "=" + URLEncoder.encode(Tour_spot, "UTF-8");
                    data += "&" + URLEncoder.encode("Startdate", "UTF-8") + "=" + URLEncoder.encode(Startdate, "UTF-8");
                    data += "&" + URLEncoder.encode("Enddate", "UTF-8") + "=" + URLEncoder.encode(Enddate, "UTF-8");

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
                    //System.out.println("이게뭐지: "+sb.toString());
                    if(sb.toString().equals("success")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "일정 추가 성공", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "일정 추가 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    return null;
                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());
                }
            }
        }
    }