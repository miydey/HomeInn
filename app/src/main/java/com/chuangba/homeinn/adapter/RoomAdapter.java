package com.chuangba.homeinn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chuangba.homeinn.R;
import com.chuangba.homeinn.bean.RoomInfo;

import java.util.ArrayList;

/**
 * Created by jinyh on 2018/3/2.
 */

public class RoomAdapter extends BaseAdapter {

    Context context;
    ArrayList<RoomInfo> roomInfos;

    public RoomAdapter(Context context,ArrayList<RoomInfo>roomInfos){
        this.context =context;
        this.roomInfos = roomInfos;
    }

    @Override
    public int getCount() {
        return roomInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return roomInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_room, null);
            viewHolder.textViewRoomNumber = (TextView) convertView.findViewById(R.id.tv_room_number);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textViewRoomNumber.setText("房间号："+roomInfos.get(position).getRoomNumber());
        return convertView;
    }
    class  ViewHolder{
        TextView textViewRoomNumber;
    }
}
