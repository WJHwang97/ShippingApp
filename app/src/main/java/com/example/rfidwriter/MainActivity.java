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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    String str;
    GridView coursesGV;
    ArrayList<GridViewModel> gridViewModelArrayList;
    SqlConnectionClass sqlConnectionClass;
    Connection con;
    private Button mClosebutton;
    private TextView EMPNo;
    private TextView Name;
    private String workBan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        mClosebutton = findViewById(R.id.button_close);
        mClosebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        //오프라인 계정
        if (EMPNo.equals("offline")) {
            gridViewModelArrayList.add(new GridViewModel("Write RFID", R.drawable.menu));
            gridViewModelArrayList.add(new GridViewModel("Read RFID", R.drawable.menu));
            gridViewModelArrayList.add(new GridViewModel("Change QTY", R.drawable.menu));
        }

        if (!ID.equals("offline")) {
            sqlConnectionClass = new SqlConnectionClass();
            con = sqlConnectionClass.CONN();
            String query = "SELECT WORKBAN FROM COMEMP WHERE PERNNO = ?";
            try (PreparedStatement preparedStatement = con.prepareStatement(query)) {
                preparedStatement.setString(1, ID);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String workBan = resultSet.getString("WORKBAN");
                    //IT 계정
                    if(workBan.equals("gu050")){
                        gridViewModelArrayList.add(new GridViewModel("Write RFID", R.drawable.menu));
                        gridViewModelArrayList.add(new GridViewModel("Read RFID", R.drawable.menu));
                        gridViewModelArrayList.add(new GridViewModel("Sales Loading Scan", R.drawable.menu));
                        gridViewModelArrayList.add(new GridViewModel("Change QTY", R.drawable.menu));
                        gridViewModelArrayList.add(new GridViewModel("Mold Read", R.drawable.menu));
                    }
                    //영업 계정
                    else if(workBan.equals("gu080")){
                        gridViewModelArrayList.add(new GridViewModel("Write RFID", R.drawable.menu));
                        gridViewModelArrayList.add(new GridViewModel("Read RFID", R.drawable.menu));
                        gridViewModelArrayList.add(new GridViewModel("Sales Loading Scan", R.drawable.menu));
                        gridViewModelArrayList.add(new GridViewModel("Change QTY", R.drawable.menu));
                    }
                    //생산 계정
                    else if(workBan.equals("gu090") || workBan.equals("gu095") || workBan.equals("gu100")){

                        gridViewModelArrayList.add(new GridViewModel("Mold Read", R.drawable.menu));
                    }
                    //무권한자
                    else{
                        Toast.makeText(MainActivity.this, "No Permission", Toast.LENGTH_LONG).show();
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }





        }
        GridViewAdapter adapter = new GridViewAdapter(this, gridViewModelArrayList, ID, NAME);
        coursesGV.setAdapter(adapter);
    }}