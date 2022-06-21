package com.example.trip;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder> {

    // adapter에 들어갈 list 입니다.
    private ArrayList<AreaTheme> listData = new ArrayList<>();

//    private OnItemClick myCallbacklback;

//    RecyclerAdapter(OnItemClick listener){
//        this.myCallback=listener;
//    }

    private OnItemClick myCallback;

    RecyclerAdapter(OnItemClick listener,ArrayList<AreaTheme> listData){
        this.myCallback=listener;
        this.listData=listData;
    }


    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // LayoutInflater를 이용하여 전 단계에서 만들었던 item.xml을 inflate 시킵니다.
        // return 인자는 ViewHolder 입니다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        // Item을 하나, 하나 보여주는(bind 되는) 함수입니다.
        holder.onBind(listData.get(position));

        holder.themename.setText(listData.get(position).getThemeName());
    }

    @Override
    public int getItemCount() {
        // RecyclerView의 총 개수 입니다.
        return listData.size();
    }

    void addItem(AreaTheme areaTheme) {
        // 외부에서 item을 추가시킬 함수입니다.
        listData.add(areaTheme);
    }


    // RecyclerView의 핵심인 ViewHolder 입니다.
    // 여기서 subView를 setting 해줍니다.
    class ItemViewHolder extends RecyclerView.ViewHolder {

        private ImageView themePic;
        TextView themename;

        ItemViewHolder(View itemView) {
            super(itemView);

            themename=itemView.findViewById(R.id.themeName);
            themePic = itemView.findViewById(R.id.themePic);

//            final RecyclerAdapter.ItemViewHolder holder=(RecyclerView.ViewHolder)

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
//                    exerPartName[pos]=exerList.getExerPartArray().toString();
                    if(pos!=RecyclerView.NO_POSITION){
                        System.out.println(pos+"선택됨");
                        switch (pos){
                            case 0:
                                myCallback.onClick("전체");
                                break;
                            case 1:
                                myCallback.onClick("산");
                                break;
                            case 2:
                                myCallback.onClick("절");
                                break;
                            case 3:
                                myCallback.onClick("전망대");
                                break;
                            case 4:
                                myCallback.onClick("포토존");
                                break;
                            case 5:
                                myCallback.onClick("문화재");
                                break;
                            case 6:
                                myCallback.onClick("역사");
                                break;
                            case 7:
                                myCallback.onClick("관람");
                                break;
                            case 8:
                                myCallback.onClick("바다");
                                break;
                            case 9:
                                myCallback.onClick("힐링");
                                break;
                            case 10:
                                myCallback.onClick("기념물");
                                break;
                            case 11:
                                myCallback.onClick("체험");
                                break;
                            case 12:
                                myCallback.onClick("액티비티");
                                break;
                            case 13:
                                myCallback.onClick("맛집탐방");
                                break;
                            case 14:
                                myCallback.onClick("식물원");
                                break;
                            case 15:
                                myCallback.onClick("경치");
                                break;
                            case 16:
                                myCallback.onClick("온천");
                                break;
                            case 17:
                                myCallback.onClick("워터파크");
                                break;
                            case 18:
                                myCallback.onClick("테마파크");
                                break;
                            case 19:
                                myCallback.onClick("유네스코");
                                break;
                            case 20:
                                myCallback.onClick("동물원");
                                break;
                            case 21:
                                myCallback.onClick("미술관");
                                break;
                            case 22:
                                myCallback.onClick("자연");
                                break;
                            case 23:
                                myCallback.onClick("도보코스");
                                break;
                            case 24:
                                myCallback.onClick("박물관");
                                break;
                            default:
                                break;
                        }
                    }
                }
            });



        }

        void onBind(AreaTheme areaTheme) {
            themePic.setImageResource(areaTheme.getResId());
        }
    }

}