package com.example.trip;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import net.daum.mf.map.api.MapView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Second extends Fragment implements OnItemClick{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public Second() {
        // Required empty public constructor
    }



    Spinner area_theme;
    private String mJsonString;
    ArrayList<Tourist> tourist;
    ListView customListView;
    private static CustomAdapter customAdapter;
    String ID;
    String AREA;
    TextView interest_Area;


    public static String selectPart="전체";

    //2022.05.15 아래 변수명 정의함
    private RecyclerAdapter adapter;
//    ListView themeListView;
    ArrayList<AreaTheme> themeArray;
//    private ListAdapter listViewAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    //ToggleButton likebtn;

    //2022.05.18
    RecyclerView mRecyclerView=null;
    MyAdapter myAdapter=null;

    //테마
    RecyclerView recyclerView=null;

    public static Second newInstance(String param1, String param2) {
        Second fragment = new Second();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=inflater.inflate(R.layout.fragment_second,container,false);//이거 없으면 프래그먼트에는 findViewById못씀


//        FragmentTransaction ft= getFragmentManager().beginTransaction();
//
//        ft.detach(this).attach(this).commit();

//        ID=getArguments().getString("ID");
//        AREA=getArguments().getString("AREA");


        SharedPreferences info= getActivity().getSharedPreferences("Info", Context.MODE_PRIVATE);
        ID=info.getString("userID",null);
        AREA=info.getString("userArea",null);

//        System.out.println("ID="+ID+" AREA= "+AREA);
//        area_theme=(Spinner)v.findViewById(R.id.area_theme);//2022.05.15
//        likebtn= v.findViewById(R.id.toggleButton);

        GetData task=new GetData();
        task.execute();

        interest_Area =(TextView) v.findViewById(R.id.interest_area);
        interest_Area.setText(AREA);

        recyclerView = (RecyclerView) v.findViewById(R.id.area_theme);
//        adapter = new RecyclerAdapter(this::onClick);//2022.05.15
        themeArray=new ArrayList<>();
//
        adapter=new RecyclerAdapter(this::onClick,themeArray);
        recyclerView.setAdapter(adapter);

        mLayoutManager = new LinearLayoutManager(getContext().getApplicationContext(),RecyclerView.HORIZONTAL,false);
        recyclerView.setLayoutManager(mLayoutManager);

        getThemeList();
        adapter.notifyDataSetChanged();

        tourist=new ArrayList<>();

        customListView=(ListView) v.findViewById(R.id.listview_custom);

        //2022.05.18
//        customAdapter=new CustomAdapter(getContext(),tourist);
//        customListView.setAdapter(customAdapter);

        //2022.05.18추가
        mRecyclerView=v.findViewById(R.id.tourist_recyclerview);

        myAdapter=new MyAdapter(tourist,ID);
        mRecyclerView.setAdapter(myAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext().getApplicationContext(),RecyclerView.VERTICAL,false));


//        onClick(selectPart);




//       area_theme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//           String selected_theme="테마를 선택하세요.";
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(mJsonString!=null) {
//                    selected_theme = area_theme.getSelectedItem().toString();
//                    showResult(selected_theme);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//                if(mJsonString!=null) {
//                    showResult(selected_theme);
//                }
//            }
//        }); //2022.05.15 이게 테마 선택했던거!!

        return v;

    }

    public void getThemeList() { //테마별,, 2022.05.15
        List<String> themeNameArray = Arrays.asList("전체","산","절","전망대","포토존","문화재","역사","관람","바다","힐링","기념물","체험","액티비티","맛집탐방","식물원","경치","온천","워터파크","테마파크","유네스코","동물원","미술관","자연","도보코스","박물관");


        List<Integer> listResId = Arrays.asList(
                R.drawable.choosetheme,
                R.drawable.mountain,
                R.drawable.zeol,
                R.drawable.zeonmang,
                R.drawable.photozone,
                R.drawable.culture,
                R.drawable.history,
                R.drawable.seeing,
                R.drawable.beach,
                R.drawable.healing,
                R.drawable.ginum,
                R.drawable.chehum,
                R.drawable.activity,
                R.drawable.food,
                R.drawable.plant,
                R.drawable.geongchi,
                R.drawable.onchoen,
                R.drawable.waterpark,
                R.drawable.themepark,
                R.drawable.unesco,
                R.drawable.zoo,
                R.drawable.artmuseum,
                R.drawable.natural,
                R.drawable.walking,
                R.drawable.museum
        );
        for (int i = 0; i < themeNameArray.size(); i++) {
            AreaTheme areaTheme = new AreaTheme(themeNameArray.get(i),listResId.get(i));
            themeArray.add(areaTheme);
        }
    }


    private class GetData extends AsyncTask<String, Void, String>{

        //ProgressDialog progressDialog;
        String errorString=null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //progressDialog=ProgressDialog.show(getActivity().getApplicationContext(),"Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //progressDialog.dismiss();
            System.out.println("response - "+result);

            if(result!=null){
                mJsonString=result;
                showResult("전체");
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                URL url = new URL("http://tot.dothome.co.kr/tourist.php");//url 지정

                //2020.05.19

                String data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(ID, "UTF-8");

                //

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");//보내는 방식
                conn.setRequestProperty("Accept", "application/json");//받는방식 json
                conn.setRequestMethod("POST");//요청방식 : POST

                conn.setDoInput(true);//InputStream으로 서버로부터 응답받겠다는 옵션
                conn.setDoOutput(true);//OutputStream으로 POST 데이터 넘겨주겠다는 옵션

                OutputStream outs = conn.getOutputStream();
                OutputStreamWriter outs_w = new OutputStreamWriter(outs, StandardCharsets.UTF_8);//받아오는거 utf-8 인코딩

                outs_w.write(data);//2020.05.19
                outs_w.flush();//버퍼에 데이터 모두 출력시키고 비움
                outs_w.close();//자원풀기
                outs.close();//풀기

                int responseStatusCode=conn.getResponseCode();
                System.out.println("response - "+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode==conn.HTTP_OK){
                    inputStream=conn.getInputStream();
                }
                else{
                    inputStream=conn.getErrorStream();
                }

                InputStreamReader inputStreamReader=new InputStreamReader(inputStream,"UTF-8");
                BufferedReader bufferedReader=new BufferedReader(inputStreamReader);

                StringBuilder sb=new StringBuilder();
                String line;

                while((line=bufferedReader.readLine())!=null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();

            }
            catch(Exception e){
                System.out.println("error - "+e);
                errorString=e.toString();

                return null;
            }

        }
    }

    private void showResult(String selected_theme){
        String TAG_JSON="webnautes";
        String TAG_AREA="area";
        String TAG_SIGUNGU="sigungu";
        String TAG_TOUR_SPOT="tour_spot";
        String TAG_THEME1="theme1";
        String TAG_THEME2="theme2";
        String TAG_LIKES="likes";
        String TAG_TOURPIC="tourpic";
        String TAG_LIKEB="likeb";

        try{
            JSONObject jsonObject=new JSONObject(mJsonString);
            JSONArray jsonArray=jsonObject.getJSONArray(TAG_JSON);
            tourist.clear();
            for(int i=0;i<jsonArray.length();i++){
                JSONObject item=jsonArray.getJSONObject(i);

                String area=item.getString(TAG_AREA).toString();
                String sigungu=item.getString(TAG_SIGUNGU).toString();
                String tour_spot=item.getString(TAG_TOUR_SPOT).toString();
                String theme1=item.getString(TAG_THEME1).toString();
                String theme2=item.getString(TAG_THEME2).toString();
                int likes=Integer.parseInt(item.getString(TAG_LIKES));//인트로 바꾸고싶어요..
                String tourpic=item.getString(TAG_TOURPIC).toString();
                int likeb=Integer.parseInt(item.getString(TAG_LIKEB));


                //System.out.println("jsonobject"+i+"= "+area+" "+sigungu+"\n");

                if(area.equals(AREA)){
                    if(theme1.equals(selected_theme)||theme2.equals(selected_theme)){
                        tourist.add(new Tourist(area, sigungu, tour_spot,likes,tourpic,likeb));
                    }
                    if(selected_theme.equals("전체")){
                        tourist.add(new Tourist(area, sigungu, tour_spot,likes,tourpic,likeb));
                    }
                }


            }
            System.out.println("selected_theme="+selected_theme);
//            customAdapter.notifyDataSetChanged(); 2022.05.18
            myAdapter.notifyDataSetChanged();
        }
        catch (JSONException e){
            System.out.println("error - "+e);

        }
    }

    public void onClick(String selectPart) {
        this.selectPart=selectPart;
        System.out.println("selectPart="+selectPart);
        showResult(selectPart);
//        ArrayList<AreaTheme> areaTheme=new ArrayList<>();
//        for(int i=0;i<tourist.size();i++){
//            if(selectPart.equals(tourist.get(i).getThemeName())){
//                tourist.add(themeArray.get(i));
//            }
//        }

//        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }

//    public void refresh(){
//        FragmentTransaction ft= getActivity().getSupportFragmentManager().beginTransaction();
//        ft.detach(this).attach(this).commit();
//    }

}