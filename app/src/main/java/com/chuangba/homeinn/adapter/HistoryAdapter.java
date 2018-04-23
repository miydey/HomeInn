package com.chuangba.homeinn.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.chuangba.homeinn.R;
import com.chuangba.homeinn.bean.HistoryInfo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/4/20.
 */

public class HistoryAdapter extends RecyclerView.Adapter <HistoryAdapter.ViewHolder> {
    private static final  String TAG = "HistoryAdapter";
    private List<HistoryInfo> historyInfos;
    private Context context;
    private MyItemClickListener mItemClickListener;
    SparseBooleanArray mSelectedPositions = new SparseBooleanArray();

    private void setItemChecked(int position, boolean isChecked) {
        mSelectedPositions.put(position, isChecked);
    }
    private boolean isItemChecked(int position) {
        return mSelectedPositions.get(position);
    }

    public void setColor(int position){

    }

    public ArrayList<HistoryInfo> getSelectedItem() {
        ArrayList<HistoryInfo> selectList = new ArrayList<>();
        for (int i = 0; i < historyInfos.size(); i++) {
            if (isItemChecked(i)) {
                selectList.add(historyInfos.get(i));
            }
        }
        return selectList;
    }

    public void selectAll(){
        int all = historyInfos.size();
        for (int i = 0;i<all;i++){
            setItemChecked(i,true);
        }
        notifyDataSetChanged();
    }

    public void unSelectAll(){
        int all = historyInfos.size();
        for (int i = 0;i<all;i++){
            setItemChecked(i,false);
        }
        notifyDataSetChanged();
    }
        public HistoryAdapter(List<HistoryInfo> historyInfos, Context context) {
        this.historyInfos = historyInfos;
        this.context = context;
    }

    public void add(HistoryInfo historyInfo, int position) {
        historyInfos.add(position, historyInfo);
        notifyItemInserted(position);
    }

    public void refresh(List<HistoryInfo> list) {
        this.historyInfos = list;
        notifyDataSetChanged();
    }


    static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView textView_name;
        TextView textView_date;
        TextView textView_type;
        CheckBox checkBox;
        private MyItemClickListener mListener;
        public ViewHolder(View itemView , MyItemClickListener mListener) {
            super(itemView);
            this.mListener = mListener;
            textView_name = (TextView) itemView.findViewById(R.id.his_name);
            textView_date = (TextView) itemView.findViewById(R.id.his_date);
            textView_type = (TextView) itemView.findViewById(R.id.his_type);
            checkBox = (CheckBox) itemView.findViewById(R.id.check_item);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onItemClick(v,getPosition());
            }
        }
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_his,parent,false);
        final ViewHolder holder = new ViewHolder(view,mItemClickListener);

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        HistoryInfo historyInfo = historyInfos.get(position);
        holder.textView_name.setText(historyInfo.getIdCardInfo().getPeopleName());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date(historyInfo.getDate());
        holder.textView_date.setText(simpleDateFormat.format(date));
        holder.textView_type.setText("身份证");
        holder.checkBox.setChecked(isItemChecked(position));
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isItemChecked(position)) {
                    setItemChecked(position, false);
                } else {
                    setItemChecked(position, true);
                }
                notifyItemChanged(position);
            }
        });


    }

    @Override
    public int getItemCount() {
        return historyInfos.size();
    }



    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }

}
