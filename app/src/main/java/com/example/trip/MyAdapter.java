package com.example.trip;

import static android.net.wifi.WifiConfiguration.Status.strings;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * 2022.05.16
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private ArrayList<Tourist> mData = null;
    Context context;
    String ID;

    public MyAdapter(ArrayList<Tourist> data, String ID) {
        mData = data;
        this.ID=ID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        View view=inflater.inflate(R.layout.tourist_cardview,parent,false);
        MyAdapter.ViewHolder vh=new MyAdapter.ViewHolder(view);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tourist item= mData.get(position);


        Glide.with(holder.itemView.getContext()).load(item.getArea_pic()).override(1000,400).into(holder.tourpicView);
        holder.areaNameText.setText(item.getArea());
        holder.sigunguNameText.setText(item.getSigungu());
        holder.tourSpotText.setText(item.getTour_spot());
        holder.countlikes.setText(String.valueOf(item.getLikes()));

        if (item.getLikeb()==0) {
            //System.out.println("likebtn checked");
            holder.likesButton.setChecked(false);
            holder.likesButton.setBackgroundResource(R.drawable.likeoff);
        } else {
            holder.likesButton.setChecked(true);
            holder.likesButton.setBackgroundResource(R.drawable.likeon);
            //System.out.println("likebtn unchecked");
            //likebtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.likeoff));
        } //2022.05.18 임시 주석


        holder.likesButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (holder.likesButton.isChecked()) {
//                    System.out.println("togglebutton clicked ischecked");
                    //System.out.println("likebtn checked");
                    holder.likesButton.setBackgroundResource(R.drawable.likeon);
                    updateInsert(ID, item.getTour_spot(), 1);
                    holder.countlikes.setText(String.valueOf(Integer.parseInt(String.valueOf(holder.countlikes.getText()))+1));
                    //holder.likesButton.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.likeon));
                }
                else{
//                    System.out.println("togglebutton clicked isUNchecked");
                    holder.likesButton.setBackgroundResource(R.drawable.likeoff);
                    updateInsert(ID, item.getTour_spot(), 0);

                    holder.countlikes.setText(String.valueOf(Integer.parseInt(String.valueOf(holder.countlikes.getText()))-1));
                }
            } //버튼 넣을곳
        });

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView tourpicView;
        TextView areaNameText;
        TextView sigunguNameText;
        TextView tourSpotText;
        ToggleButton likesButton;
        TextView countlikes;

        ViewHolder(View itemView){
            super(itemView);
            tourpicView=itemView.findViewById(R.id.tourist_pic);
            areaNameText=itemView.findViewById(R.id.area_name);
            sigunguNameText=itemView.findViewById(R.id.sigungu_name);
            tourSpotText=itemView.findViewById(R.id.tourspot_name);
            likesButton=itemView.findViewById(R.id.likebutton);
            countlikes=itemView.findViewById(R.id.countlike);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                    Tourist item= mData.get(pos);
                    Context mcontext=v.getRootView().getContext();

                    String selectedArea = item.getArea();
                    String selectedSigungu=item.getSigungu().toString();
                    String selectedTourspot=item.getTour_spot().toString();
//                    System.out.println(selectedArea+selectedSigungu+selectedTourspot);
                    AlertDialog.Builder builder=new AlertDialog.Builder(v.getRootView().getContext());
                    builder.setTitle("항목을 선택해주세요");
                    builder.setItems(R.array.SELECT, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int pos) {
                            String[] items=mcontext.getResources().getStringArray((R.array.SELECT));
                            if(items[pos].equals("일정 추가")){
                                Intent intent = new Intent(v.getRootView().getContext(), PickDateActivity.class);
                                intent.putExtra("selectedArea",selectedArea);
                                intent.putExtra("selectedSigungu",selectedSigungu);
                                intent.putExtra("selectedTourspot",selectedTourspot);
                                v.getContext().startActivity(intent);
                            }
                            else{
                                Intent intent = new Intent(v.getRootView().getContext(),MapsActivity.class);
                                intent.putExtra("selectedArea",selectedArea);
                                intent.putExtra("selectedSigungu",selectedSigungu);
                                intent.putExtra("selectedTourspot",selectedTourspot);
                                v.getContext().startActivity(intent);
                            }
                        }
                    });

                AlertDialog alertDialog= builder.create();
                alertDialog.show();
                }
            });
        }
    }

    private void updateInsert(String Id, String tourspot, Integer userlikeb) {
        class updateLike extends AsyncTask<String, Void, String> {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }
            @Override
            protected String doInBackground(String... params) {
                try {
                    String Id = (String) params[0];
                    String tourspot = (String) params[1];
                    Integer userlikeb= Integer.parseInt(params[2]);

                    String link = "http://tot.dothome.co.kr/UpdateLike.php";
                    String data = URLEncoder.encode("ID", "UTF-8") + "=" + URLEncoder.encode(ID, "UTF-8");
                    data += "&" + URLEncoder.encode("tourSpot", "UTF-8") + "=" + URLEncoder.encode(tourspot, "UTF-8");
                    data += "&" + URLEncoder.encode("userlikeb", "UTF-8") + "=" + userlikeb;

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
        updateLike task = new updateLike();
        task.execute(Id, tourspot, String.valueOf(userlikeb));
    }
}