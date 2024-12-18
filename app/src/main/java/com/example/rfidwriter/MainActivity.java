package com.example.rfidwriter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String str;
    GridView coursesGV;
    ArrayList<GridViewModel> gridViewModelArrayList;

    private Button mSendbutton;
    private Button mClosebutton;
    private TextView EMPNo;
    private TextView Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mClosebutton = findViewById(R.id.button_close);
        mClosebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                moveTaskToBack(true); // 태스크를 백그라운드로 이동
                finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기

                System.exit(0);
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent rcvIntent = getIntent();
        String ID = rcvIntent.getStringExtra("EMPNO");
        String NAME = rcvIntent.getStringExtra("Name");
        EMPNo = findViewById(R.id.EMPNO);
        Name = findViewById(R.id.Name);
        EMPNo.setText(ID);
        Name.setText(NAME);
       /* Intent intent = new Intent(MainActivity.this, SalesLoadingScan.class);
        intent.putExtra("EMPNO", ID);
        intent.putExtra("NAME", NAME);
        startActivity(intent);*/


        coursesGV = findViewById(R.id.idGVcourses);
        gridViewModelArrayList = new ArrayList<GridViewModel>();
        gridViewModelArrayList.add(new GridViewModel("Write RFID", R.drawable.menu ));
        gridViewModelArrayList.add(new GridViewModel("Read RFID", R.drawable.menu ));
        //gridViewModelArrayList.add(new GridViewModel("Sales Loading Scan", R.drawable.menu ));
        //gridViewModelArrayList.add(new GridViewModel("Change QTY", R.drawable.menu ));
        //gridViewModelArrayList.add(new GridViewModel("Mold Read", R.drawable.menu ));
//        gridViewModelArrayList.add(new GridViewModel("LOTTE", R.drawable.menu ));
//        gridViewModelArrayList.add(new GridViewModel("CINEMA", R.drawable.menu ));
//        gridViewModelArrayList.add(new GridViewModel("LOTTE1", R.drawable.menu ));
//        gridViewModelArrayList.add(new GridViewModel("LOTTE2", R.drawable.menu ));

        GridViewAdapter adapter = new GridViewAdapter(this, gridViewModelArrayList, ID, NAME);
        coursesGV.setAdapter(adapter);


    }
}