package com.example.rfidwriter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListItemAdapter extends BaseAdapter {
    public ArrayList<Model> productList;

    Activity activity;

    public ListItemAdapter(Activity activity, ArrayList<Model> productList) {
        super();
        this.activity = activity;
        this.productList = productList;
    }




    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView ShipNoView;
        TextView DockView;
        TextView ShipToView;
        TextView TrailerNoView;

        Spinner DockList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder holder;
            LayoutInflater inflater = activity.getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.listitem, null);
                holder = new ViewHolder();
                holder.ShipNoView = (TextView) convertView.findViewById(R.id.SHIPNO);
                holder.DockView = (TextView) convertView.findViewById(R.id.DockNo);
                holder.ShipToView = (TextView) convertView
                        .findViewById(R.id.ShipToNo);
                holder.TrailerNoView = (TextView) convertView.findViewById(R.id.TrailerNO);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            Model item = productList.get(position);
        holder.ShipNoView.setText(item.getsNo().toString());
        holder.DockView.setText(item.getProduct().toString());
        holder.ShipToView.setText(item.getCategory().toString());
        holder.TrailerNoView.setText(item.getPrice().toString());

        return convertView;
    }




}
