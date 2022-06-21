package com.example.trip;

/* 일정 리스트뷰 어뎁터 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapterTwo extends ArrayAdapter implements AdapterView.OnItemClickListener {
    private Context context;
    private List list;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(context,"clicked",Toast.LENGTH_SHORT).show();
    }

    class ViewHolder{
        public TextView tv_area;
        public TextView tv_sigungu;
        public TextView tv_tour_spot;
        public TextView tv_startdate;
        public TextView tv_enddate;
    }

    public CustomAdapterTwo(Context context, ArrayList list){
        super(context, 0, list);
        this.context=context;
        this.list=list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView==null){
            LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
            convertView=layoutInflater.inflate(R.layout.schedule_layout,parent,false);
        }

        viewHolder=new ViewHolder();
        viewHolder.tv_startdate=(TextView) convertView.findViewById(R.id.textView_startdate);
        viewHolder.tv_enddate=(TextView) convertView.findViewById(R.id.textView_enddate);
        viewHolder.tv_area=(TextView) convertView.findViewById(R.id.textView_area);
        viewHolder.tv_sigungu=(TextView) convertView.findViewById(R.id.textView_sigungu);
        viewHolder.tv_tour_spot=(TextView) convertView.findViewById(R.id.textView_tour_spot);


        Schedule schedule=(Schedule) list.get(position);
        viewHolder.tv_area.setText(schedule.getArea());
        viewHolder.tv_sigungu.setText(schedule.getSigungu());
        viewHolder.tv_tour_spot.setText(schedule.getTour_spot());
        viewHolder.tv_startdate.setText(schedule.getStartdate());
        viewHolder.tv_enddate.setText(schedule.getEnddate());

        //return super.getView(position, convertView, parent);
        return convertView;
    }


}
