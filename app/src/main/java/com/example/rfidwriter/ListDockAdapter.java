package com.example.rfidwriter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class ListDockAdapter extends BaseAdapter {
    private ArrayList<String> dockList;
    private Activity activity;

    public ListDockAdapter(Activity activity, ArrayList<String> dockList) {
        super();
        this.activity = activity;
        this.dockList = dockList;
    }

    @Override
    public int getCount() {
        return dockList.size();
    }

    @Override
    public Object getItem(int position) {
        return dockList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        Spinner dockHolder;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listitem, null);
            holder = new ViewHolder();
            holder.dockHolder = (Spinner) convertView.findViewById(R.id.DockSpinner);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String item = dockList.get(position);

        // 스피너에 어댑터 설정
        ArrayList<String> spinnerItems = new ArrayList<>();
        spinnerItems.add(item);  // 직접적으로 데이터를 추가
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.dockHolder.setAdapter(adapter);

        // 선택된 아이템 설정
        holder.dockHolder.setSelection(0);

        return convertView;
    }
}
