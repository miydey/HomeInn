package com.chuangba.homeinn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuangba.homeinn.ui.CheckActivity;
import com.chuangba.homeinn.R;
import com.chuangba.homeinn.bean.OrderInfo;
import com.chuangba.homeinn.bean.RoomTypeInfo;

import java.util.ArrayList;

/**
 * Created by jinyh on 2018/3/1.
 */

public class OrderAdapter extends BaseAdapter {
    ArrayList<RoomTypeInfo> roomTypeInfos;
    Context context;
    public OrderAdapter(Context context,ArrayList<RoomTypeInfo> roomTypeInfos){
        this.context = context;
        this.roomTypeInfos = roomTypeInfos;
    }
    @Override
    public int getCount() {
        return roomTypeInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return roomTypeInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
       ViewHolder viewHolder=null;
        RoomTypeInfo faceItem=(RoomTypeInfo) getItem(position);
        if(convertView==null)
        {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_order, null);
            viewHolder.textViewType = (TextView)convertView.findViewById(R.id.tv_item_type);
            viewHolder.textViewNormal = (TextView)convertView.findViewById(R.id.tv_item_price_normal);
            viewHolder.textViewVip = (TextView)convertView.findViewById(R.id.tv_item_price_vip);
            viewHolder.textViewDay = (TextView)convertView.findViewById(R.id.tv_item_day);
            viewHolder.imageViewMinus = (ImageView) convertView.findViewById(R.id.iv_day_minus);
            viewHolder.imageAdd = (ImageView) convertView.findViewById(R.id.iv_day_add);
            viewHolder.buttonSubmit = (Button) convertView.findViewById(R.id.bt_order_submit);
            convertView.setTag(viewHolder);
        }
        else
        {
            viewHolder=(ViewHolder) convertView.getTag();
        }
        final TextView tvDay = viewHolder.textViewDay;
        final ImageView ivMinus = viewHolder.imageViewMinus;
        viewHolder.textViewNormal.setText(String.valueOf(roomTypeInfos.get(position).getPriceNormal()));
        viewHolder.textViewType.setText(String.valueOf(roomTypeInfos.get(position).getRoomType()));
        viewHolder.textViewDay.setText(String.valueOf(roomTypeInfos.get(position).getDay()));
        viewHolder.textViewVip.setText(String.valueOf(roomTypeInfos.get(position).getPriceVIP()));

        viewHolder.imageAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int day =  ((RoomTypeInfo) getItem(position)).getDay() ;
                if (day < 99){
                    day = day + 1;
                    roomTypeInfos.get(position).setDay(day);
                    tvDay.setText(String.valueOf(day));
                    ivMinus.setImageResource(R.mipmap.day_minus);
                }

            }
        });

        viewHolder.imageViewMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int day =  ((RoomTypeInfo) getItem(position)).getDay();
                if (day > 1){
                    day = day - 1;
                    roomTypeInfos.get(position).setDay(day);
                    tvDay.setText(String.valueOf(day));
                    if (day==1){
                        ivMinus.setImageResource(R.mipmap.day_minus_grey);
                    }
                }

            }
        });


        viewHolder.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckActivity activity = (CheckActivity) context;
                RoomTypeInfo roomTypeInfo = roomTypeInfos.get(position);
                int price = roomTypeInfo.getDay()*roomTypeInfo.getPriceNormal();
                OrderInfo  orderInfo = new OrderInfo();
                orderInfo.setPriceTotal(price);
                orderInfo.setRoomTypeInfo(roomTypeInfo);

                activity.setOrderInfo(orderInfo);
                activity.setTab(2);
            }
        });


        return convertView;
    }
    class ViewHolder
    {
        public TextView textViewType;
        public TextView  textViewVip;
        public TextView textViewNormal;
        public TextView  textViewDay;
        public Button buttonSubmit;
        public ImageView imageViewMinus;
        public ImageView imageAdd;
    }
}
