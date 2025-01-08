package com.example.rfidwriter;

import static android.app.ProgressDialog.show;
import static java.lang.Integer.parseInt;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Objects;


public class LoadScan extends AppCompatActivity {
    SqlConnectionClass sqlConnectionClass;
    Connection con;
    private TextView ShippingNo;
    private TextView ShipTo;
    private TextView TruckNo;
    private ListView list;
    private Button ConfirmButton;
    private Button manual_button;
    private EditText vendorBox;
    private String RFIDYN;
    private String ManualYN;
    private String CANCELYN;
    private String OrderNo;
    private Button ManualButton;
    private String Sachrkey = "";
    private ObjectAnimator blinkAnimator;
    private Button ScanDetails;
    private String barcode;
    private String LotNo;
    private Button ClearScan;
    private boolean chkexists;
    private TextView LoadingMSG;
    private String EMPNO;
    private String DockMapValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_loadscan);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sqlConnectionClass = new SqlConnectionClass();
        con = sqlConnectionClass.CONN();
        list = findViewById(R.id.listView);
        Intent rcvIntent = getIntent();
        LoadingMSG = findViewById(R.id.LoadingMSG);
        ShippingNo = findViewById(R.id.ShippingNo);
        ShippingNo.setText(rcvIntent.getStringExtra("SHIPNO"));
        OrderNo = rcvIntent.getStringExtra("SHIPNO");
        ShipTo = findViewById(R.id.ShipTo);
        ShipTo.setText(rcvIntent.getStringExtra("ShipToNo"));
        TruckNo = findViewById(R.id.TruckNo);
        TruckNo.setText(rcvIntent.getStringExtra("TrailerNO"));
        EMPNO =  rcvIntent.getStringExtra("EMPNO");
        DockMapValue = rcvIntent.getStringExtra("DockMapValue");
        String Name =  rcvIntent.getStringExtra("Name");
        ShipDetails();




        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            private View previousSelectedView = null;

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (previousSelectedView != null) {
                    previousSelectedView.setBackgroundColor(Color.TRANSPARENT); // 기본 배경색으로 초기화
                }
                view.setBackgroundColor(Color.LTGRAY); // 선택된 항목 하이라이트
                previousSelectedView = view;
                LoadScanModel selectedItem = (LoadScanModel) parent.getItemAtPosition(position);
                Sachrkey = selectedItem.getSachrkey();
            }
        });

        // 버튼 클릭 시 sachrkey 처리
        manual_button = findViewById(R.id.Manualbutton);
        manual_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ManualButton();
            }
        });

        vendorBox = findViewById(R.id.Vendor);
        vendorBox.requestFocus();
        ClearScan = findViewById(R.id.ClearScanButton);
        ClearScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                vendorBox.setText("");
            }
        });
        ConfirmButton = findViewById(R.id.ConfirmButton);
        ConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Confirm_Button();
                //Toast.makeText(getApplicationContext(), "Confirm", Toast.LENGTH_LONG).show();
            }
        });





// 키보드를 무조건 숨기도록 설정
        vendorBox.setShowSoftInputOnFocus(false); // 키보드가 뜨지 않도록 설정 (API 21 이상에서 지원)

// 또는 아래와 같이 InputMethodManager를 통해 키보드를 숨기는 방법을 사용할 수 있습니다.
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(vendorBox.getWindowToken(), 0); // 처음 포커스될 때 키보드 숨김
        }

        vendorBox.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // 키보드가 절대 뜨지 않도록 설정
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(vendorBox.getWindowToken(), 0); // 키보드 숨김
                    }
                }
                return true;
            }
        });


        vendorBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                // 입력이 끝나고 호출됩니다.

                barcode = s.toString();
                if (barcode.length() == 0) {
                    // 값이 비어 있으면 아무 동작도 하지 않음
                    return;
                }
                if(barcode.length() ==10)
                {
                    if(barcode.substring(0,1).equals("R"))
                    {
                        if(RFIDYN.equals("Y")){
                            SearchLot();
                        } else{
                            Mobis_SearchLot();
                        }
                    } else{
                        Toast.makeText(LoadScan.this, "Barcode startletter wrong" + barcode.length(), Toast.LENGTH_LONG).show();
                    }

                } else{
                    Toast.makeText(LoadScan.this, "Barcode Length not right" + barcode.length()  , Toast.LENGTH_LONG).show();
                }
            }
        });


// 바코드 처리 메소드





        ScanDetails = findViewById(R.id.ScanDetailButton);
        ScanDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Sachrkey.equals("")){
                    Toast.makeText(getApplicationContext(), "Select Item to check", Toast.LENGTH_SHORT).show();
                }
                else {
                    String LotNo = vendorBox.getText().toString();
                    Intent intent = new Intent(getApplicationContext(), ScanDetails.class);
                    intent.putExtra("TKNUM", OrderNo);
                    intent.putExtra("SACHRKEY", Sachrkey);
                    intent.putExtra("LOTNO", vendorBox.toString());
                    intent.putExtra("EMPNO", EMPNO);
                    startActivity(intent);
                }
            }
        });
    }

    protected void onResume() {
        super.onResume();
        // 화면이 다시 나타나면 ShipDetails()를 호출하여 데이터를 갱신
        ShipDetails();
    }
    private void SearchLot() {
        try {
            // 데이터베이스 연결 및 CallableStatement 준비
            CallableStatement callableStatement = con.prepareCall("{call sp_PdaA010FrmB_SARE_POP1(@WORKGB = ?, @FACTGB = ?, @LOTNO = ?, @RFIDYN = ?, @TKNUM = ?, @RTNYN = ?, @vcTRANGB = ?, @FUSER = ?, @FFORM = ?)}");
            // 프로시저 파라미터 설정 및 실행 로직...
            callableStatement.setString(1, "SEARCH_LOT");
            callableStatement.setString(2, "6100");
            callableStatement.setString(3, barcode);
            callableStatement.setString(4, RFIDYN);
            callableStatement.setString(5, OrderNo);
            callableStatement.setString(6, "Y");
            callableStatement.setString(7, "");
            callableStatement.setString(8, EMPNO);
            callableStatement.setString(9, "");
            ResultSet resultSet = callableStatement.executeQuery();

            if (isThere(resultSet, "CHK")) {
                chkexists = true;
            }
            else
            {
                chkexists = false;
            }
            if (resultSet.next()) {
                if (!chkexists) {
                    if(RFIDYN.equals("Y")) {
                        Intent intent = new Intent(LoadScan.this, ScanDialog.class);
                        intent.putExtra("QTYValue", resultSet.getString("QTY"));
                        intent.putExtra("SABUNValue", resultSet.getString("SABUN"));
                        intent.putExtra("SACHRKEYValue", resultSet.getString("SACHRKEY"));
                        intent.putExtra("MESLOTValue", resultSet.getString("MESLOT"));
                        intent.putExtra("TKNUM", ShippingNo.getText().toString());
                        intent.putExtra("RFIDYN", RFIDYN);
                        startActivity(intent);
                        vendorBox.setText("");  // 입력 필드 초기화
                    }} else {
                    Toast.makeText(LoadScan.this, resultSet.getString("ERRMSG"), Toast.LENGTH_LONG).show();
                    vendorBox.setText("");  // 오류 발생 시 필드 초기화
                }
            }
            resultSet.close();
            callableStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            vendorBox.setText("");  // 예외 발생 시 필드 초기화
        }

    }

    private void Mobis_SearchLot() {
        try {
            // 데이터베이스 연결 및 CallableStatement 준비
            CallableStatement callableStatement = con.prepareCall("{call sp_PdaA010FrmB_SA_POP1(@WORKGB = ?, @FACTGB = ?, @LOTNO = ?, @TKNUM = ?, @RTNYN = ?, @vcTRANGB = ?, @FUSER = ?, @FFORM = ?)}");
            // 프로시저 파라미터 설정 및 실행 로직...
            callableStatement.setString(1, "SEARCH_LOT");
            callableStatement.setString(2, "6100");
            callableStatement.setString(3, barcode);
            callableStatement.setString(4, OrderNo);
            callableStatement.setString(5, "Y");
            callableStatement.setString(6, "");
            callableStatement.setString(7, EMPNO);
            callableStatement.setString(8, "");
            ResultSet resultSet = callableStatement.executeQuery();
            boolean chkexists = isThere(resultSet, "CHK");


            if (isThere(resultSet, "CHK")) { chkexists = true;}
            else {chkexists = false;}
            if (resultSet.next()) {
                if (chkexists) {
                    Toast.makeText(LoadScan.this, resultSet.getString("ERRMSG"), Toast.LENGTH_LONG).show();
                    vendorBox.setText("");
                }
            }
            resultSet.close();
            callableStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            vendorBox.setText("");  // 예외 발생 시 필드 초기화
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                vendorBox.setText("");
                ShipDetails();
            }
        });
    }


    private void ShipDetails() {

        CallableStatement callableStatement = null;
        ResultSet resultSet = null;

        try {
            callableStatement = con.prepareCall("{call sp_PdaA010FrmB_SARE_POP1(@WORKGB=?, @TKNUM=?, @DOCKNO=?, @RTNYN=?, @FUSER =?, @vcTRANGB=?)}");
            callableStatement.setString(1, "SEARCH");
            callableStatement.setString(2, OrderNo);
            callableStatement.setString(3, DockMapValue);
            callableStatement.setString(4, "Y");
            callableStatement.setString(5, EMPNO);
            callableStatement.setString(6, "1");

            // 첫 번째 ResultSet 처리

            resultSet = callableStatement.executeQuery();
            int resultSetCount = 1;  // 첫 번째 ResultSet이 있으므로 1부터 시작
            ArrayList<LoadScanModel> results = new ArrayList<>();
            LoadScanAdapter adapter = new LoadScanAdapter(LoadScan.this, results);
            list.setAdapter(adapter);

            while (resultSet.next()) {
                String PartNumber = resultSet.getString("SABUN");
                String SEQNumber = resultSet.getString("POSNR");
                int orderNumberInt = (int) Double.parseDouble(resultSet.getString("NQTY")); // NQTY 소수점 제거
                String OrderNumber = String.valueOf(orderNumberInt); // 다시 문자열로 변환
                int scanNumberInt = (int) Double.parseDouble(resultSet.getString("CHULQTY")); // CHULQTY 소수점 제거
                String ScanNumber = String.valueOf(scanNumberInt); // 다시 문자열로 변환
                int manualNumberInt = (int) Double.parseDouble(resultSet.getString("CHULQTY1")); // CHULQTY1 소수점 제거
                String ManualNumber = String.valueOf(manualNumberInt); // 다시 문자열로 변환
                String sachrkeyFromResultSet = resultSet.getString("SACHRKEY");
                results.add(new LoadScanModel(PartNumber, SEQNumber, OrderNumber, ScanNumber, ManualNumber, sachrkeyFromResultSet));
            }

            if (results.isEmpty()) {
                Toast.makeText(LoadScan.this, "No results found.", Toast.LENGTH_SHORT).show();
            }

            // 추가적인 ResultSet 처리
            boolean resultsAvailable = callableStatement.getMoreResults();
            //ResultSet lastResultSet = null;  // 마지막 ResultSet을 저장할 변수

            while (resultsAvailable) {
                ResultSet currentResultSet = callableStatement.getResultSet();

                // 결과 세트가 있을 때마다 카운트 증가
                resultSetCount++;

                if (resultSetCount == 2 && currentResultSet != null) {
                    // 두 번째 ResultSet 처리 (EDIYN 관련)
                    if (currentResultSet.next()) {
                        RFIDYN = currentResultSet.getString("RFIDYN");
                        //Toast.makeText(LoadScan.this, "Selected EDI" + EDIYN, Toast.LENGTH_LONG).show();
                        CheckBox MESCheckBox = findViewById(R.id.MESCheck);
                        CheckBox RFIDCheckBox = findViewById(R.id.RFIDCheck);
                        if (RFIDYN.equals("N")) {
                            MESCheckBox.setVisibility(View.GONE);
                            RFIDCheckBox.setVisibility(View.GONE);
                        }
                    }
                }
                else if (resultSetCount == 3 && currentResultSet != null) {
                    // 세 번째 ResultSet 처리 (ManualYN 관련)
                    if (currentResultSet.next()) {
                        ManualYN = currentResultSet.getString("MANUALYN");
                        //Toast.makeText(LoadScan.this, "Selected MANUAL" + ManualYN, Toast.LENGTH_LONG).show();
                        Button ManualButton = findViewById(R.id.Manualbutton);
                        if (ManualYN.equals("N")) {
                            ManualButton.setVisibility(View.GONE);
                        }
                    }
                }
                // 현재의 마지막 ResultSet을 lastResultSet으로 저장
                ResultSet lastResultSet = currentResultSet;
                resultsAvailable = callableStatement.getMoreResults(Statement.KEEP_CURRENT_RESULT);
                if (!resultsAvailable) {

                    if (lastResultSet != null) {
                        // 마지막 ResultSet에 데이터가 있는지 체크
                        if (lastResultSet.next()) {
                            CANCELYN = Objects.toString(lastResultSet.getString("CANCELYN"), "N");
                            //Toast.makeText(LoadScan.this, "Selected CAMNELYN    " + CANCELYN, Toast.LENGTH_LONG).show();
                            if (CANCELYN.equals("Y")) {
                                Sachrkey = "";
                                vendorBox.setText("");
                                CheckBox MesCheck = findViewById(R.id.MESCheck);
                                CheckBox RFIDCheck = findViewById(R.id.RFIDCheck);
                                MesCheck.setChecked(false);
                                RFIDCheck.setChecked(false);
                                TextView Vendor = findViewById(R.id.Vendor);
                                Vendor.setText("");
                                Toast.makeText(LoadScan.this, "Selected shipping doc. has been cancelled", Toast.LENGTH_LONG).show();
                                ShipDetails();  // 다시 로드
                            }
                        }
                    }
                }

            }

            // 마지막 ResultSet 처리 (CANCELYN 값 가져오기)

        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(LoadScan.this, "SQL Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(LoadScan.this, "General Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
        finally
        {
            try {
                callableStatement.close();
                resultSet.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

    }
    public void ManualButton()
    {
        if (Sachrkey.isEmpty()) {
            Toast.makeText(LoadScan.this, "Tap for manual item", Toast.LENGTH_LONG).show();
        }
        try {
            CallableStatement callableStatement = con.prepareCall("{call sp_PdaA010FrmB_SARE_POP1(@WORKGB = ?, @FACTGB=?, @SACHRKEY=?, @TKNUM=?, @RTNYN=?, @vcTRANGB=?, @FUSER=?, @FFORM =?)}");
            callableStatement.setString(1, "INSERT_M");
            callableStatement.setString(2, "");
            callableStatement.setString(3, Sachrkey);
            callableStatement.setString(4, OrderNo);
            callableStatement.setString(5, "Y");
            callableStatement.setString(6, "0");
            callableStatement.setString(7, EMPNO);
            callableStatement.setString(8, "");
            ResultSet resultSet = callableStatement.executeQuery();

            if(resultSet.next()){
                String chkValue = resultSet.getString("CHK");
                String errmsg =resultSet.getString("ERRMSG");
                if(chkValue.equals("X")){
                    Toast.makeText(LoadScan.this, errmsg, Toast.LENGTH_LONG).show();
                    ShipDetails();
                }else if(chkValue.equals("O")){
                    //Toast.makeText(LoadScan.this, errmsg, Toast.LENGTH_LONG).show();
                    ShipDetails();
                }
            }
            resultSet.close();
            callableStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            vendorBox.setText("");
            Sachrkey = "";
            ShipDetails();
        }
    }


    private void Confirm_Button(){
        try {
            con.setAutoCommit(false);
            CallableStatement callableStatement = con.prepareCall("{call sp_PdaA010FrmB_SARE_POP1(@WORKGB=?, @FACTGB=?, @TKNUM=?, @RTNYN=?, @vcTRANGB=?, @FUSER=?, @FFORM=?)}");
            callableStatement.setString(1, "SAVE_CONFYN");
            callableStatement.setString(2, "6100");
            callableStatement.setString(3, OrderNo);
            callableStatement.setString(4, "Y");
            callableStatement.setString(5, "0");
            callableStatement.setString(6, EMPNO);
            callableStatement.setString(7, "");
            ResultSet resultSet = callableStatement.executeQuery();

            if (resultSet.next()) {
                String chkValue = resultSet.getString("CHK");
                String errmsg = resultSet.getString("ERRMSG");


                if (chkValue.equals("X")) {
                    LoadingMSG.setText(errmsg);
                    //Toast.makeText(LoadScan.this, errmsg, Toast.LENGTH_LONG).show();
                    // X인 경우, 화면 갱신
                    con.commit();
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("triggerSearch", true);
                    setResult(RESULT_OK, resultIntent);
                    ShipDetails();
                } else if (chkValue.equals("O")) {
                    // O인 경우, 대화상자 띄우고 종료
                    showDeleteConfirmationDialog();

                    return;  // 바로 리턴하여 아래 ShipDetails()가 실행되지 않도록 함
                }
                // ShipDetails()를 여기에서 호출하지 않음
            }
            resultSet.close();
            callableStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            vendorBox.setText("");
            Sachrkey = "";
            ShipDetails();
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to confirm this load?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            con.commit();
                            LoadingMSG.setText("Loading Completed");
                            LoadingMSG.setTextColor(Color.GREEN);
                            Intent intent = new Intent(getApplicationContext(), OrderDialog.class);
                            startActivity(intent);
                            ShipDetails();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            con.rollback();
                            // No를 선택한 경우, 대화상자 닫기
                            dialog.cancel();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private boolean isThere(ResultSet rs, String column){
        try{
            rs.findColumn(column);
            return true;
        } catch (SQLException sqlex){
        }

        return false;
    }
}