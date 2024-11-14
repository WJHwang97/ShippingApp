package com.example.rfidwriter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter<GridViewModel> {
    private String EMPNO,Name;
    public GridViewAdapter(@NonNull Context context, ArrayList<GridViewModel> courseModelArrayList, String EMPNO, String Name) {
        super(context, 0, courseModelArrayList);

        this.EMPNO = EMPNO;
        this.Name = Name;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listitemView = convertView;

        if (listitemView == null) {
            // Layout Inflater inflates each item to be displayed in GridView.
            listitemView = LayoutInflater.from(getContext()).inflate(R.layout.card_item, parent, false);
        }

        GridViewModel gridviewmodel = getItem(position);
        TextView courseTV = listitemView.findViewById(R.id.idTVCourse);
        ImageView courseIV = listitemView.findViewById(R.id.idIVcourse);

        courseTV.setText(gridviewmodel.getCourse_name());
        courseIV.setImageResource(gridviewmodel.getImgid());


        listitemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;

                switch (courseTV.getText().toString()) {
                    case "Write RFID":
                        i = new Intent(getContext(), ListViewActivity.class);
                        getContext().startActivity(i);
                        break;
                    case "Read RFID":
                        i = new Intent(getContext(), ReadrfidActivity.class);
                        getContext().startActivity(i);
                        break;
                    case "Sales Loading Scan":
                        i = new Intent(getContext(), SalesLoadingScan.class);
                        i.putExtra("EMPNO", EMPNO);
                        i.putExtra("Name", Name);
                        getContext().startActivity(i);
                        break;
                    case "Change QTY":
                        i = new Intent(getContext(), ChangeQTY.class);
                        getContext().startActivity(i);
                        break;
                    case "Mold Read":
                        i = new Intent(getContext(), PressMold.class);
                        getContext().startActivity(i);
                        break;
                }

                //Toast.makeText(getContext(), courseTV.getText(), Toast.LENGTH_SHORT).show();
            }
        });

        return listitemView;
    }
}
