package com.example.rfidwriter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Objects;

public class ListViewActivity extends AppCompatActivity {

    //public TextView description1;
    //public TextView description2;
    private ArrayList<Model> productList;
    //private String columnOne;
    //private String columnTwo;

    Button button_nea1;
    Button button_mea1;

    String CarType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listview);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        button_nea1 = findViewById(R.id.button_nea1);
        button_mea1 = findViewById(R.id.button_mea1);

        productList = new ArrayList<Model>();

        ListView lview = (ListView) findViewById(R.id.listview);
        ListViewAdapter adapter = new ListViewAdapter(this, productList);
        lview.setAdapter(adapter);

        button_nea1.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v)
           {
               productList.clear();
               CarType = "NE1a";
               populateList(CarType);
               adapter.notifyDataSetChanged();
           }
        });

        button_mea1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                productList.clear();
                CarType = "ME1a";
                populateList(CarType);
                adapter.notifyDataSetChanged();
            }
        });





        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String cat = ((TextView)view.findViewById(R.id.Cat)).getText().toString();
                String no = ((TextView)view.findViewById(R.id.No)).getText().toString();
                String part = ((TextView)view.findViewById(R.id.Part)).getText().toString();
                String plt = ((TextView)view.findViewById(R.id.PLT)).getText().toString();

//                Toast.makeText(getApplicationContext(),
//                        "Cat : " + sno +"\n"
//                                +"No : " + product +"\n"
//                                +"Part # : " +category +"\n"
//                                +"PLT # : " +price, Toast.LENGTH_SHORT).show();

                Intent i = new Intent(getApplicationContext(), WriterfidActivity.class);

//              WriterfidActivity 로 값 넘김.

                i.putExtra("cat",cat);
                i.putExtra("no",no);
                i.putExtra("part",part);
                i.putExtra("plt",plt);
                i.putExtra("car",CarType);

                startActivity(i);

            }
        });

    }

    private void populateList(String CarType) {

        Model item1, item2, item3, item4, item5,item6,item7,item8,item9,item10,
                item11, item12, item13, item14, item15, item16, item17, item18, item19;

        if (Objects.equals(CarType, "NE1a"))
        {
            item1 = new Model("RF", "A", "65500-PI810", "2111");
            productList.add(item1);

            item2 = new Model("RF", "A", "65500-PI710", "2112");
            productList.add(item2);

            item3 = new Model("RF", "A", "65500-PI800", "2113");
            productList.add(item3);

            item4 = new Model("RF", "A", "65500-PI700", "2114");
            productList.add(item4);

            item5 = new Model("CF", "B", "65100-PI200", "2101");
            productList.add(item5);

            item6 = new Model("CF", "B", "65100-PI700", "2102");
            productList.add(item6);

            item7 = new Model("BACK", "C", "69100-PI000", "2131");
            productList.add(item7);

            item8 = new Model("QTR LH", "D", "71601-PI000", "3131");
            productList.add(item8);

            item9 = new Model("QTR RH", "E", "71602-PI000", "4131");
            productList.add(item9);

            item10 = new Model("EXTN LH", "F", "71550-PI000", "3141");
            productList.add(item10);

            item11 = new Model("EXTN LH", "F", "71550-PI020", "3142");
            productList.add(item11);

            item12 = new Model("EXTN RH", "H", "71560-PI000", "4141");
            productList.add(item12);

            item13 = new Model("EXTN RH", "H", "71560-PI020", "4142");
            productList.add(item13);

            item14 = new Model("HOOD", "I", "66400-PI000", "7151");
            productList.add(item14);

            item15 = new Model("T/GATE", "J", "72801-PI000", "7101");
            productList.add(item15);

            item16 = new Model("T/GATE", "J", "72801-PI010", "7102");
            productList.add(item16);

            // 아래부터 아진 품목
//            item1 = new Model("DASH", "A", "64200-PI000", "1131");
//            productList.add(item1);
//
//            item2 = new Model("F/AP LH", "B", "64500-PI000", "1111");
//            productList.add(item2);
//
//            item3 = new Model("F/AP RH", "C", "64600-PI000", "1121");
//            productList.add(item3);
//
//            item4 = new Model("F/AP RH", "C", "64600-PI100", "1122");
//            productList.add(item4);
//
//            item5 = new Model("S/OTR LH", "D", "71110-PI000", "3121");
//            productList.add(item5);
//
//            item6 = new Model("S/OTR LH", "D", "71110-PI050", "3122");
//            productList.add(item6);
//
//            item7 = new Model("S/OTR RH", "E", "71120-PI000", "4121");
//            productList.add(item7);
//
//            item8 = new Model("S/OTR RH", "E", "71120-PI050", "4122");
//            productList.add(item8);
//
//            item9 = new Model("FR DR LH", "F", "76003-PI000", "7131");
//            productList.add(item9);
//
//            item10 = new Model("FR DR RH", "G", "76004-PI000", "7141");
//            productList.add(item10);
//
//            item11 = new Model("RR DR LH", "H", "77003-PI000", "7111");
//            productList.add(item11);
//
//            item12 = new Model("RR DR LH", "H", "77003-PI010", "7112");
//            productList.add(item12);
//
//            item13 = new Model("RR DR RH", "I", "77004-PI000", "7121");
//            productList.add(item13);
//
//            item14 = new Model("RR DR RH", "I", "77004-PI010", "7122");
//            productList.add(item14);
//
//            item15 = new Model("PNL ROOF UPR", "J", "67136-PI050", "5103");
//            productList.add(item15);
//
//            item16 = new Model("ROOF FRT", "K", "67123-GI000", "6101");
//            productList.add(item16);
//
//            item17 = new Model("ROOF FRT", "K", "67121-PI050", "6102");
//            productList.add(item17);
//
//            item18 = new Model("ROOF RR", "L", "67131-GI000", "6111");
//            productList.add(item18);
//
//            item19 = new Model("ROOF RR", "L", "67131-PI050", "6112");
//            productList.add(item19);
        }
        else if (Objects.equals(CarType, "ME1a"))
        {
            item1 = new Model("CF", "K", "65100-TD000", "2201");
            productList.add(item1);

            item2 = new Model("RF", "L", "65500-TD000", "2211");
            productList.add(item2);

            item3 = new Model("RF", "L", "65500-TD020", "2212");
            productList.add(item3);

            item4 = new Model("QTR LH", "M", "71601-TD000", "3231");
            productList.add(item4);

            item5 = new Model("QTR LH", "M", "71601-TD060", "3232");
            productList.add(item5);

            item6 = new Model("QTR RH", "N", "71602-TD000", "4231");
            productList.add(item6);

            item7 = new Model("QTR RH", "N", "71602-TD060", "4232");
            productList.add(item7);

            item8 = new Model("QTR RH", "N", "71602-TD050", "4233");
            productList.add(item8);

            item9 = new Model("QTR RH", "N", "71602-TD070", "4234");
            productList.add(item9);

            item10 = new Model("EXTN LH", "O", "71550-TD000", "3241");
            productList.add(item10);

            item11 = new Model("EXTN RH", "P", "71560-TD000", "4241");
            productList.add(item11);

            item12 = new Model("ROOF", "Q", "67110-TDO060", "5202");
            productList.add(item12);

            item13 = new Model("T/GATE", "R", "72801-TD000", "7202");
            productList.add(item13);

            item14 = new Model("HOOD", "S", "66401-TD000", "7251");
            productList.add(item14);

            item15 = new Model("FENDER LH", "T", "66310-TD000", "7261");
            productList.add(item15);

            item16 = new Model("FENDER RH", "U", "66320-TD000", "7271");
            productList.add(item16);

            // 아래부터 아진 품목
//            item1 = new Model("DASH", "A", "64200-TD000", "1231");
//            productList.add(item1);
//
//            item2 = new Model("F/AP LH", "B", "64500-TD000", "1212");
//            productList.add(item2);
//
//            item3 = new Model("F/AP RH", "C", "64600-TD000", "1222");
//            productList.add(item3);
//
//            item4 = new Model("F/AP RH", "C", "64600-TD020", "1223");
//            productList.add(item4);
//
//            item5 = new Model("S/OTR LH", "D", "71110-TD000", "3221");
//            productList.add(item5);
//
//            item6 = new Model("S/OTR LH", "D", "71110-TD060", "3222");
//            productList.add(item6);
//
//            item7 = new Model("S/OTR RH", "E", "71120-TD000", "4221");
//            productList.add(item7);
//
//            item8 = new Model("S/OTR RH", "E", "71120-TD060", "4222");
//            productList.add(item8);
//
//            item9 = new Model("FR DR LH", "F", "76003-TD000", "7231");
//            productList.add(item9);
//
//            item10 = new Model("FR DR RH", "G", "76004-TD000", "7241");
//            productList.add(item10);
//
//            item11 = new Model("RR DR LH", "H", "77003-TD000", "7211");
//            productList.add(item11);
//
//            item12 = new Model("RR DR RH", "I", "77004-TD000", "7221");
//            productList.add(item12);
//
//            item13 = new Model("ROOF FRT", "K", "67123-GO000", "6201");
//            productList.add(item13);
//
//            item14 = new Model("ROOF FRT", "K", "67123-GO060", "6202");
//            productList.add(item14);
//
//            item15 = new Model("ROOF RR", "L", "67133-GO000", "6211");
//            productList.add(item15);
//
//            item16 = new Model("ROOF RR", "L", "67133-GO060", "6212");
//            productList.add(item16);
//
//            item17 = new Model("RAIL-ROOF CTR NO.2", "N", "67141-TD000", "6221");
//            productList.add(item17);
//
//            item18 = new Model("RAIL-ROOF CTR NO.4", "M", "67172-GO000", "6231");
//            productList.add(item18);
        }
        else
        {

        }

    }
}