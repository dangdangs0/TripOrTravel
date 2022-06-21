package com.example.trip;

/* 투어 리스트뷰 어뎁터 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter implements AdapterView.OnItemClickListener {
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
    }

    public CustomAdapter(Context context, ArrayList list){
        super(context, 0, list);
        this.context=context;
        this.list=list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        ToggleButton likebtn;

        if(convertView==null){
            LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
            convertView=layoutInflater.inflate(R.layout.categories_layout,parent,false);
        }

        viewHolder=new ViewHolder();
        viewHolder.tv_area=(TextView) convertView.findViewById(R.id.textView_area);
        viewHolder.tv_sigungu=(TextView) convertView.findViewById(R.id.textView_sigungu);
        viewHolder.tv_tour_spot=(TextView) convertView.findViewById(R.id.textView_tour_spot);
        likebtn= convertView.findViewById(R.id.toggleButton);

        final Tourist tourist=(Tourist)list.get(position);
        viewHolder.tv_area.setText(tourist.getArea());
        viewHolder.tv_sigungu.setText(tourist.getSigungu());
        viewHolder.tv_tour_spot.setText(tourist.getTour_spot());
        viewHolder.tv_area.setTag(tourist.getArea());

        likebtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if (likebtn.isChecked()) {
                    //System.out.println("likebtn checked");
                    likebtn.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.likeon));
                } else {
                    likebtn.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.likeoff));
                    //System.out.println("likebtn unchecked");
                    //likebtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.likeoff));
                }
            } //버튼 넣을곳
        });

        //return super.getView(position, convertView, parent);
        return convertView;
    }


}
