package com.example.rfidwriter;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class SalesLoadingScan extends AppCompatActivity {

    SqlConnectionClass sqlConnectionClass;
    Connection con;
    private TextView CurrentDateTextView;
    private Button searchButton;
    private ListView list;
    public String dateString;
    public String DockMapValue;
    private TextView EMPNO;
    private TextView NAME;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_salesloadingscan);

        Intent rcvIntent = getIntent();
        String EmpNo = rcvIntent.getStringExtra("EMPNO");
        String Name = rcvIntent.getStringExtra("Name");

        EMPNO = findViewById(R.id.EmpNo);
        NAME = findViewById(R.id.Name);
        EMPNO.setText(EmpNo);
        NAME.setText(Name);



        // Window insets 설정
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        CurrentDateTextView = findViewById(R.id.CurrentDate);

        // 현재 날짜 설정
        Calendar calendar = Calendar.getInstance();
        updateDateTextView(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // DatePickerDialog 설정
        CurrentDateTextView.setOnClickListener(v -> showDatePickerDialog());

        sqlConnectionClass = new SqlConnectionClass();
        con = sqlConnectionClass.CONN();

        list = findViewById(R.id.list);
        searchButton = findViewById(R.id.searchButton);

        try {
            CallableStatement searchdocStatement = con.prepareCall("{call sp_PdaA010FrmB_SA(@WORKGB='SEARCH_DOCK')}");
            ResultSet resultSet = searchdocStatement.executeQuery();
            ArrayList<String> dockList = new ArrayList<>();
            ArrayList<String> dockmapList = new ArrayList<>();

            while (resultSet.next()) {
                String docks = resultSet.getString("LABEL");
                String docksmap = resultSet.getString("VALUE");
                dockList.add(docks);
                dockmapList.add(docksmap);
            }

            // Spinner에 dockList를 표시
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, dockList);
            Spinner spinner = findViewById(R.id.DockSpinner);
            spinner.setAdapter(arrayAdapter);
            spinner.setSelection(0);

            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                    ((TextView) adapterView.getChildAt(0)).setTextSize(13);
                    DockMapValue = dockmapList.get(position);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {
                }
            });
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(SalesLoadingScan.this, "Cannot Get Dock Info: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Search 버튼 클릭 시 ShipDetails 실행
        searchButton.setOnClickListener(v -> ShipDetails());

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private long lastClickTime = 0;
            private static final int DOUBLE_CLICK_TIME_DELTA = 300;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < DOUBLE_CLICK_TIME_DELTA) {
                    String SHIPNO = ((TextView) view.findViewById(R.id.SHIPNO)).getText().toString();
                    String ShipToNo = ((TextView) view.findViewById(R.id.ShipToNo)).getText().toString();
                    String TrailerNO = ((TextView) view.findViewById(R.id.TrailerNO)).getText().toString();

                    // LoadScan을 호출하고 결과를 받음
                    Intent i = new Intent(getApplicationContext(), LoadScan.class);
                    i.putExtra("SHIPNO", SHIPNO);
                    i.putExtra("ShipToNo", ShipToNo);
                    i.putExtra("TrailerNO", TrailerNO);
                    i.putExtra("EMPNO", EmpNo);
                    i.putExtra("Name", Name);
                    i.putExtra("DockMapValue",DockMapValue);
                    startActivityForResult(i, 1);  // requestCode 1로 설정
                }
                lastClickTime = clickTime;
            }
        });
    }

    // DatePickerDialog 표시
    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> updateDateTextView(selectedYear, selectedMonth, selectedDay),
                year, month, day);
        datePickerDialog.show();
    }

    // 선택한 날짜로 TextView 업데이트
    public void updateDateTextView(int year, int month, int day) {
        month = month + 1;
        dateString = String.format("%04d%02d%02d", year, month, day);  // 날짜 형식을 yyyyMMdd로 변환
        CurrentDateTextView.setText(String.format("%04d-%02d-%02d", year, month, day));
    }

    // 서버로부터 ShipDetails 불러오기
    private void ShipDetails() {
        try {
            CallableStatement callableStatement = con.prepareCall("{call sp_PdaA010FrmB_SA(@WORKGB=?, @FACTGB=?, @CHULDATE=?, @CUSTCD=?, @DOCKNO=?)}");
            callableStatement.setString(1, "SEARCH");
            callableStatement.setString(2, "6100");
            callableStatement.setString(3, dateString);
            callableStatement.setString(4, "");
            callableStatement.setString(5, DockMapValue);

            ResultSet resultSet = callableStatement.executeQuery();
            ArrayList<Model> results = new ArrayList<>();
            ListItemAdapter adapter = new ListItemAdapter(SalesLoadingScan.this, results);
            list.setAdapter(adapter);

            while (resultSet.next()) {
                String shipNumber = resultSet.getString("TKNUM");
                String dockNumber = resultSet.getString("DOCK");
                String shipTo = Objects.toString(resultSet.getString("CUSTNM"), "");
                String trailerNumber = resultSet.getString("DVHCLE");

                results.add(new Model(shipNumber, dockNumber, shipTo, trailerNumber));
            }

            if (results.isEmpty()) {
                Toast.makeText(SalesLoadingScan.this, "No results found.", Toast.LENGTH_SHORT).show();
            }

        } catch (SQLException e) {
            Toast.makeText(this, "Internet Connection Failed" , Toast.LENGTH_LONG).show();
            Intent First_Page = new Intent(this, Login.class);
            startActivity(First_Page);
        }
    }

    // onActivityResult 추가하여 LoadScan에서 돌아올 때 searchButton 클릭
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK) {
            runOnUiThread(() -> searchButton.performClick());
        }
    }
}
