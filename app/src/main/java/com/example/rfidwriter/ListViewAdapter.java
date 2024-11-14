package com.example.rfidwriter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends BaseAdapter {
    public ArrayList<Model> productList;
    Activity activity;

    public ListViewAdapter(Activity activity, ArrayList<Model> productList) {
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
        TextView mSNo;
        TextView mProduct;
        TextView mCategory;
        TextView mPrice;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow, null);
            holder = new ViewHolder();
            holder.mSNo = (TextView) convertView.findViewById(R.id.Cat);
            holder.mProduct = (TextView) convertView.findViewById(R.id.No);
            holder.mCategory = (TextView) convertView
                    .findViewById(R.id.Part);
            holder.mPrice = (TextView) convertView.findViewById(R.id.PLT);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Model item = productList.get(position);
        holder.mSNo.setText(item.getsNo().toString());
        holder.mProduct.setText(item.getProduct().toString());
        holder.mCategory.setText(item.getCategory().toString());
        holder.mPrice.setText(item.getPrice().toString());

        return convertView;
    }


}
