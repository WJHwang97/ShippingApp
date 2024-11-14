package com.example.rfidwriter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class ScanDetailsAdapter extends BaseAdapter {
    public ArrayList<ScanDetailsModel> results;

    Activity activity;

    public ScanDetailsAdapter(Activity activity, ArrayList<ScanDetailsModel> results) {
        super();
        this.activity = activity;
        this.results = results;
    }




    @Override
    public int getCount() {return results.size();}

    @Override
    public Object getItem(int position) {
        return results.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    private class ViewHolder {

        TextView LOTNO;
        TextView EDIPO;
        TextView QTY;



    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            LayoutInflater inflater = activity.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listlotnoedi, null);
                holder = new ViewHolder();
                holder.LOTNO = (TextView) convertView.findViewById(R.id.LOTNO);
                holder.EDIPO = (TextView) convertView.findViewById(R.id.EDIPO);
                holder.QTY = (TextView) convertView.findViewById(R.id.QTY);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

        ScanDetailsModel item = results.get(position);
        holder.LOTNO.setText(item.getLOTNO().toString());
        holder.EDIPO.setText(item.getEDIPO().toString());
        holder.QTY.setText(item.getQTY().toString());

        return convertView;
    }




}
