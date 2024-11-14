package com.example.rfidwriter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class LoadScanAdapter extends BaseAdapter {
    public ArrayList<LoadScanModel> LoadScanList;

    Activity activity;

    public LoadScanAdapter(Activity activity, ArrayList<LoadScanModel> LoadScanList) {
        super();
        this.activity = activity;
        this.LoadScanList = LoadScanList;
    }




    @Override
    public int getCount() {
        return LoadScanList.size();
    }

    @Override
    public Object getItem(int position) {
        return LoadScanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView PartNo;
        TextView SEQNO;
        TextView InputNo;
        TextView AutoScan;
        TextView ManualScan;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            LayoutInflater inflater = activity.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.loadscanlist, null);
                holder = new ViewHolder();
                holder.PartNo = (TextView) convertView.findViewById(R.id.PartNo);
                holder.SEQNO = (TextView) convertView.findViewById(R.id.SEQNo);
                holder.InputNo = (TextView) convertView
                        .findViewById(R.id.InputNo);
                holder.AutoScan = (TextView) convertView.findViewById(R.id.AutoScan);
                convertView.setTag(holder);
                holder.ManualScan = (TextView) convertView.findViewById(R.id.ManualScan);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            LoadScanModel item = LoadScanList.get(position);
        holder.PartNo.setText(item.getPartNo().toString());
        holder.SEQNO.setText(item.getSEQNo().toString());
        holder.InputNo.setText(item.getInputNo().toString());
        holder.AutoScan.setText(item.AutoScan().toString());
        holder.ManualScan.setText(item.getManualScan().toString());

        return convertView;
    }




}
