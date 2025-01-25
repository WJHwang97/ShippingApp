package com.example.rfidwriter;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

public class ScanDetails extends AppCompatActivity {

    private ListView list;
    SqlConnectionClass sqlConnectionClass;
    Connection con;
    private TextView LotNoTextBox;
    private String TKNUM;
    private String Sachrkey;
    private String LOTNO;
    private Button DeleteButton;
    private String EMPNO;
    private Button CloseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_scandetails);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        list = findViewById(R.id.LoadList);
        Intent rcvIntent = getIntent();
        LotNoTextBox = findViewById(R.id.LotNOTextBox);
        Sachrkey = (rcvIntent.getStringExtra("SACHRKEY"));
        TKNUM = (rcvIntent.getStringExtra("TKNUM"));
        LOTNO = (rcvIntent.getStringExtra("LOTNO"));
        EMPNO = rcvIntent.getStringExtra("EMPNO");
        CloseButton = findViewById(R.id.CloseButton);
        LoadTable();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private View previousSelectedView = null;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (previousSelectedView != null) {
                    previousSelectedView.setBackgroundColor(Color.TRANSPARENT); // 기본 배경색으로 초기화
                }
                view.setBackgroundColor(Color.LTGRAY); // 선택된 항목 하이라이트
                previousSelectedView = view;
                ScanDetailsModel selectedItem = (ScanDetailsModel) parent.getItemAtPosition(position);
                LOTNO = selectedItem.getLOTNO();
                TextView LotNOTextBox = findViewById(R.id.LotNOTextBox);
                TextView QTYTextBox = findViewById(R.id.QTYTextBox);
                LotNOTextBox.setText(selectedItem.getLOTNO());
                QTYTextBox.setText(selectedItem.getQTY());

            }
        });


        DeleteButton = findViewById(R.id.DeleteButton);
        DeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
        CloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    private void LoadTable() {
        sqlConnectionClass = new SqlConnectionClass();
        con = sqlConnectionClass.CONN();
        try {
            CallableStatement callableStatement = con.prepareCall("{call sp_PdaA010FrmB_SA_POP2(@WORKGB=?, @SACHRKEY=?, @TKNUM=?, @RTNYN=?, @vcTRANGB=?)}");
            callableStatement.setString(1, "SEARCH_SCANLIST");
            callableStatement.setString(2, Sachrkey);
            callableStatement.setString(3, TKNUM);
            callableStatement.setString(4, "N");
            callableStatement.setString(5, "1");
            //Toast.makeText(SalesLoadingScan.this, selectedMapValue, Toast.LENGTH_SHORT).show();

            //java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"))

            ResultSet resultSet = callableStatement.executeQuery();
            ArrayList<ScanDetailsModel> results = new ArrayList<>();
            ScanDetailsAdapter adapter = new ScanDetailsAdapter(ScanDetails.this, results);
            list.setAdapter(adapter);

            while (resultSet.next()) {
                String LotNo = resultSet.getString("LOTNO");
                String SANPO = resultSet.getString("SANPO");
                int lotQty = (int) Double.parseDouble(resultSet.getString("LOTQTY")); // 문자열을 숫자로 변환 후 소수점 제거
                String LOTQTY = String.valueOf(lotQty);
                /*String LOTQTY = Objects.toString(resultSet.getString("LOTQTY"), "");*/
                results.add(new ScanDetailsModel(LotNo, SANPO, LOTQTY));
            }

            if (results.isEmpty()) {
                Toast.makeText(ScanDetails.this, "No results found.", Toast.LENGTH_SHORT).show();
            }

        } catch (SQLException e) {
            Toast.makeText(this, "Internet Connection Failed" , Toast.LENGTH_LONG).show();
            Intent First_Page = new Intent(this, Login.class);
            startActivity(First_Page);
        }
    }

    private void showDeleteConfirmationDialog() {
        // AlertDialog 생성
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete this item?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Yes를 선택한 경우에만 Button_Delete() 메서드를 호출하여 쿼리 실행
                        Button_Delete();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // No를 선택한 경우, 대화상자 닫기
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void Button_Delete() {
        sqlConnectionClass = new SqlConnectionClass();
        con = sqlConnectionClass.CONN();
        try {
                    CallableStatement callableStatement = con.prepareCall("{call sp_PdaA010FrmB(@WORKGB=?, @SACHRKEY=?, @LOTNO=?, @RTNYN=?, @vcTRANGB=?, @FUSER=?, @FFORM=?)}");
                    callableStatement.setString(1, "SCAN_DELETE");
                    callableStatement.setString(2, Sachrkey);
                    callableStatement.setString(3, LOTNO);
                    callableStatement.setString(4, "Y");
                    callableStatement.setString(5, "0");
                    callableStatement.setString(6, EMPNO);
                    callableStatement.setString(7, "");
                    callableStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(ScanDetails.this, "Select Load to delete", Toast.LENGTH_LONG).show();
        }





        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                LoadTable();
            }
        });
    }
}
