package com.example.trip;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.LocalDate;

public class First extends Fragment{

    private static final String TAG="First";

    String ID;
    String AREA;

    ArrayList<Schedule> scheduleAll;    //모든 일정 저장
    ArrayList<Schedule> schedule;   //화면에 보여주는 일정
    ListView customListView;    //일정 리스트
    private static CustomAdapterTwo customAdapter;

    private String mJsonString; //getDate return
    MaterialCalendarView materialCalendarView;
    ArrayList<CalendarDay> calendarDayList; //일정 시작, 끝 이벤트

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_first,container,false);

        materialCalendarView=(MaterialCalendarView) v.findViewById(R.id.calendarView);
        materialCalendarView.setSelectedDate(CalendarDay.today());  //현재 날짜로 기본 선택

        calendarDayList=new ArrayList<>();
//        calendarDayList.add(CalendarDay.from(2021,11,1));
//        calendarDayList.add(CalendarDay.from(2021,11,15));
//        calendarDayList.add(CalendarDay.from(2021,11,17));
//        materialCalendarView.addDecorator(new EventDecorator(0xFF647AC3, calendarDayList));

        //MainActivity에서 저장한 정보 불러오기
        SharedPreferences info= getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE);
        ID=info.getString("userID",null);
//        AREA=info.getString("userArea",null);

        scheduleAll=new ArrayList<>();
        schedule=new ArrayList<Schedule>();
        
        getDate task=new getDate();
        task.execute();

        //ListView와 Adapter 연결
        customListView=(ListView) v.findViewById(R.id.listview_custom);
        customAdapter=new CustomAdapterTwo(getContext(),schedule);
        customListView.setAdapter(customAdapter);
        
        //날짜 선택 시 이벤트
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                changePlanList(date);
            }
        });

        //리스트뷰 목록을 선택했을 때 발생하는 이벤트
        customListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedArea = ((Schedule)customAdapter.getItem(position)).getArea();
                String selectedSigungu=((Schedule)customAdapter.getItem(position)).getSigungu();
                String selectedTourspot=((Schedule)customAdapter.getItem(position)).getTour_spot();
                String selectedStart=((Schedule) customAdapter.getItem(position)).getStartdate();
                String selectedEnd=((Schedule)customAdapter.getItem(position)).getEnddate();

                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setTitle("일정을 삭제하시겠습니까?");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        delDate delDate=new delDate();
                        try {
                            String buff = delDate.execute(selectedArea, selectedSigungu, selectedTourspot, selectedStart, selectedEnd).get();
                            if(buff.equals("success")){
                                Toast.makeText(getActivity(), "일정이 삭제되었습니다", Toast.LENGTH_SHORT).show();

                                for(int i=0;i<scheduleAll.size();i++){
                                    if(scheduleAll.get(i).getArea()==selectedArea && scheduleAll.get(i).getSigungu()==selectedSigungu && scheduleAll.get(i).getTour_spot()==selectedTourspot && scheduleAll.get(i).getStartdate()==selectedStart && scheduleAll.get(i).getEnddate()==selectedEnd){
                                        scheduleAll.remove(i);
                                    }
                                }
                                schedule.remove(position);
                                customAdapter.notifyDataSetChanged();

                                addDateDeco();
                                //materialCalendarView.removeDecorators(); //점 전부 삭제하는거..
                                //refreshFg();
                            }else{
                                Toast.makeText(getActivity(), "일정 삭제 실패", Toast.LENGTH_SHORT).show();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int id)
                    {
                        Toast.makeText(getActivity(), "삭제 취소", Toast.LENGTH_SHORT).show();
                    }
                });
                AlertDialog alertDialog= builder.create();
                alertDialog.show();
            }
        });
        return v;
    }

    //schedule table에서 현재 로그인된 사용자의 일정 불러오기
    public class getDate extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            scheduleAll.clear();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            System.out.println("response - "+s);
            if(s!=null){
                mJsonString=s;
                showResult();
            }
        }

        @Override
        public String doInBackground(Void... strings) {
            try {
                String value;
                String params = "Id=" +ID;//파라미터

                URL url = new URL("http://tot.dothome.co.kr/getDate.php");//url 지정

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

                return buff.toString().trim();  //area, sigungu, tour_spot, startdate, enddate

            } catch (MalformedURLException | ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    //getDate에서 받아온 데이터를 리스트에 표시
    private void showResult(){
        String TAG_JSON="result";
        String TAG_AREA="area";
        String TAG_SIGUNGU="sigungu";
        String TAG_TOUR_SPOT="tour_spot";
        String TAG_STARTDATE="startdate";
        String TAG_ENDDATE="enddate";
        try{
            JSONObject jsonObject=new JSONObject(mJsonString);  //JSONObject로 변환
            JSONArray jsonArray=jsonObject.getJSONArray(TAG_JSON);

            schedule.clear();
            for(int i=0;i<jsonArray.length();i++){
                JSONObject item=jsonArray.getJSONObject(i);
                String area=item.getString(TAG_AREA).toString();
                String sigungu=item.getString(TAG_SIGUNGU).toString();
                String tour_spot=item.getString(TAG_TOUR_SPOT).toString();
                String startdate=item.getString(TAG_STARTDATE).toString();
                String enddate=item.getString(TAG_ENDDATE).toString();
                Schedule s=new Schedule(area, sigungu, tour_spot, startdate, enddate);
                scheduleAll.add(s);
                /*
                SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
                try {
                    nowdate=dateFormat.parse(CalendarDay.today().getDate().toString());
                    startDate = dateFormat.parse(startdate);
                    endDate = dateFormat.parse(enddate);
                    if(startDate.compareTo(nowdate) > 0){   //startDate > nowdate인 경우
//                            Toast.makeText(getActivity(), "startdate > nowdate", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        if(endDate.compareTo(nowdate) >= 0){    //startDate <= nowdate <= endDate인 경우
                            //System.out.println(schedule.get(i).getArea()+schedule.get(i).getSigungu()+schedule.get(i).getTour_spot());
                            schedule.add(new Schedule(area,sigungu,tour_spot,startdate,enddate));
                        }
                        else{
//                          Toast.makeText(getActivity(), "enddate < nowdate", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                 */
                //System.out.println("i= "+i+",area= "+area+",sigungu= "+sigungu+",tour_spot= "+tour_spot+",startdate= "+startdate+",enddate= "+enddate);
            }
            changePlanList(CalendarDay.today());
            addDateDeco();
            //materialCalendarView.addDecorator(new EventDecorator(0xFF334CB1, calendarDayList));
        }
        catch (JSONException e){
            System.out.println("error - "+e);
        }
    }
    
    //현재 보여주는 일정을 갱신해주는 함수
    private void changePlanList(CalendarDay standard){
        schedule.clear();
        for(int i=0;i<scheduleAll.size();i++){
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date nowdate=dateFormat.parse(standard.getDate().toString());
                Date startDate = dateFormat.parse(scheduleAll.get(i).getStartdate());
                Date endDate = dateFormat.parse(scheduleAll.get(i).getEnddate());

                //범위 내의 일정만 표시하도록 설정
                if(startDate.compareTo(nowdate) > 0){   //startDate > nowdate
                    //Toast.makeText(getActivity(), "startdate > nowdate", Toast.LENGTH_SHORT).show();
                }
                else{
                    if(endDate.compareTo(nowdate) >= 0){    //startDate <= nowdate <= endDate
                        schedule.add(scheduleAll.get(i));
                    }
                    else{
                        //Toast.makeText(getActivity(), "enddate < nowdate", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        customAdapter.notifyDataSetChanged();
    }

    //불러온 일정의 시작일, 종료일에 Deco 추가
    private void addDateDeco(){
        materialCalendarView.removeDecorators(); //점 전부 삭제하는거..
        calendarDayList.clear();
        for(int i=0;i<scheduleAll.size();i++){
            String[] splitstart=scheduleAll.get(i).getStartdate().split("-");
            String[] splitend=scheduleAll.get(i).getEnddate().split("-");

            int styear=Integer.parseInt(splitstart[0]);
            int stmonth=Integer.parseInt(splitstart[1]);
            int stday=Integer.parseInt(splitstart[2]);
            calendarDayList.add(CalendarDay.from(styear,stmonth,stday));

            int eyear=Integer.parseInt(splitend[0]);
            int emonth=Integer.parseInt(splitend[1]);
            int eday=Integer.parseInt(splitend[2]);
            calendarDayList.add(CalendarDay.from(eyear,emonth,eday));
        }
        materialCalendarView.addDecorator(new EventDecorator(0xFF11ADD6, calendarDayList));
    }


    //선택된 일정을 schedule table에서 삭제
    public class delDate extends AsyncTask<String, Void, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                String params="userID="+ID+"&area="+strings[0]+"&sigungu="+strings[1]+"&tour_spot="+strings[2]+"&startdate="+strings[3]+"&enddate="+strings[4];
                URL url = new URL("http://tot.dothome.co.kr/delDate.php");//url 지정

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
                return buff.toString();
//                if(buff.toString().equals("success")){
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getActivity(), "일정이 삭제되었습니다", Toast.LENGTH_SHORT).show();
//                            customAdapter.notifyDataSetChanged();
//                            System.out.println("schedule size1 = "+schedule.size());
//                            refreshFg();
//                        }
//                    });
//                }
//                else{
//                    getActivity().runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(getActivity(), "일정 삭제 실패", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
            } catch (MalformedURLException | ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
    /*
    public void refreshFg(){
        System.out.println("refreshFg execute");
        FragmentTransaction ft=getActivity().getSupportFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
     */