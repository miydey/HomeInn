package com.chuangba.homeinn.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.chuangba.homeinn.R;

import java.util.ArrayList;

/**
 * Created by jinyh on 2018/3/14.
 */

public class FloorAdapter extends BaseAdapter {
    ArrayList<String> floors;
    Context context;
    public FloorAdapter(Context context, ArrayList<String> floors){
        this.floors = floors;
        this.context = context;
    }
    @Override
    public int getCount() {
        return floors.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        FloorAdapter.ViewHolder viewHolder;
        if (convertView==null){
            viewHolder = new FloorAdapter.ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_floor, null);
            viewHolder.textViewFloorNumber = (TextView) convertView.findViewById(R.id.tv_floor_number);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textViewFloorNumber.setText("楼层："+floors.get(position));
        return convertView;

    }

    class  ViewHolder{
        TextView textViewFloorNumber;
    }
}
