package com.chuangba.homeinn.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.chuangba.homeinn.ui.CheckActivity;
import com.chuangba.homeinn.R;
import com.chuangba.homeinn.adapter.RoomAdapter;
import com.chuangba.homeinn.bean.RoomInfo;
import com.chuangba.homeinn.util.ToastUtil;

import java.util.ArrayList;

/**
 * Created by jinyh on 2018/3/2.
 */

public class RoomFragment extends Fragment {
    private GridView gridViewRoom;
    private ArrayList<RoomInfo> roomInfos;
    //ListView listViewFloor;
    Context context;
    TextView textViewSelect;
    TextView textViewSubmit;
    boolean selected;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_room,container,false);
        gridViewRoom = (GridView) view.findViewById(R.id.gv_room);
        textViewSelect = (TextView) view.findViewById(R.id.tv_selected);
        textViewSubmit = (TextView) view.findViewById(R.id.tv_room_submit);
        //listViewFloor = (ListView) view.findViewById(R.id.lv_floor);
        context = getActivity();
        roomInfos = new ArrayList<>();
       for (int i = 0;i<9;i++){
        RoomInfo roomInfo = new RoomInfo();
           roomInfo.setRoomNumber(i);
           roomInfos.add(roomInfo);
       }

        ArrayList<String> floors = new ArrayList<>();
        for (int i =10;i<19;i++){
            String floor = i+"楼";
            floors.add(floor);
        }
//        FloorAdapter floorAdapter = new FloorAdapter(context,floors);
//        listViewFloor.setAdapter(floorAdapter);
//        listViewFloor.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//
//            }
//        });


        RoomAdapter roomAdapter = new RoomAdapter(context,roomInfos);
        gridViewRoom.setAdapter(roomAdapter);

        gridViewRoom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int number = roomInfos.get(position).getRoomNumber();
                textViewSelect.setText("已选房间:"+number);
                selected = true;
            }
        });
    textViewSubmit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (selected){
                CheckActivity activity = (CheckActivity) context;
                activity.setTab(3);
            } else {
                ToastUtil.showToast(getActivity(),"请先选择房间");
            }

        }
    });

        return view;
    }
}
