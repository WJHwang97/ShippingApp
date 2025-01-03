package com.example.rfidwriter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.keyence.autoid.sdk.notification.Notification;
import com.keyence.autoid.sdk.rfid.Filter;
import com.keyence.autoid.sdk.rfid.RFIDReader;
import com.keyence.autoid.sdk.rfid.ReadResult;
import com.keyence.autoid.sdk.rfid.Status;
import com.keyence.autoid.sdk.rfid.params.MemoryBank;
import com.keyence.autoid.sdk.rfid.params.ReadParams;

import org.w3c.dom.Text;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.transform.Result;

public class ScanDialog extends Activity {

    SqlConnectionClass sqlConnectionClass;
    Connection con;
    private Button button_read;
    private Button button_close;
    private RFIDReader mRFIDReader;
    private Notification mNotification;
    private TextView rfidResult;
    private TextView Item;
    private TextView Part;
    private TextView Model;
    private TextView QTY;
    private TextView PLTBox;
    private TextView MESPart;
    private TextView MESQTY;
    private String QTYValue;
    private String SABUNValue;
    private String SACHRKEYValue;
    private String MESLOTValue;
    private String RFIDYN;
    private String TKNUM;
    private String RFIDNOFULL;
    private java.sql.ResultSet ResultSet;
    private ResultSet resultSet;
    private boolean chkexists;
    private String SACHRKEY;
    private TextView SACHRKEYTEXTVIEW;
    private TextView ERRMSG;
    private TextView MESLOTTEXTVIEW;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scandialog);
        sqlConnectionClass = new SqlConnectionClass();
        con = sqlConnectionClass.CONN();
        button_read = findViewById(R.id.button_read);
        button_close = findViewById(R.id.button_close);
        Item = findViewById(R.id.Item);
        Part = findViewById(R.id.Part);
        Model = findViewById(R.id.Model);
        QTY = findViewById(R.id.QTY);
        rfidResult = findViewById(R.id.rfidResult);
        PLTBox = findViewById(R.id.PLT);
        ERRMSG = findViewById(R.id.ERRMSG);
        // 초기 텍스트 설정
        Item.setText("");
        Part.setText("");
        Model.setText("");
        QTY.setText("");
        PLTBox.setText("");
        rfidResult.setText("");
        button_read.setOnClickListener(v -> RFID_Read());
        Intent rcvIntent = getIntent();
        QTYValue = rcvIntent.getStringExtra("QTYValue");
        SABUNValue = rcvIntent.getStringExtra("SABUNValue");
        SACHRKEYValue = rcvIntent.getStringExtra("SACHRKEYValue");
        SACHRKEY = SACHRKEYValue;
        MESLOTValue = rcvIntent.getStringExtra("MESLOTValue");
        RFIDYN = rcvIntent.getStringExtra("RFIDYNValue");
        TKNUM = rcvIntent.getStringExtra("TKNUM");
        MESPart = findViewById(R.id.MESPart);
        MESQTY = findViewById(R.id.MESQTY);
        MESLOTTEXTVIEW = findViewById(R.id.LOTNO);
        SACHRKEYTEXTVIEW = findViewById(R.id.SACHRKEY);

        MESPart.setText(SABUNValue);
        MESQTY.setText(QTYValue);
        MESLOTTEXTVIEW.setText(MESLOTValue);
        SACHRKEYTEXTVIEW.setText(SACHRKEY);

        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();  // 현재 액티비티 종료하고 이전 액티비티로 돌아감
            }
        });

    }

    // RFID Read 기능 구현
    private void RFID_Read() {
        final LoadingDialog loadingDialog = new LoadingDialog(this);

        loadingDialog.startLoadingDialog();

        // 5초 뒤에 로딩 다이얼로그 종료
        Handler handler = new Handler();
        handler.postDelayed(loadingDialog::dismissDialog, 5000);

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler_1 = new Handler(Looper.getMainLooper());

        button_read.setEnabled(false);

        executor.execute(() -> {
            // RFID 읽기 작업 수행
            handler_1.post(() -> {
                // RFID 읽기 기능 호출
                performRFIDRead();
                loadingDialog.dismissDialog();
                button_read.setEnabled(true);
            });
        });
    }

    // 실제 RFID 리딩 기능 수행
    private void performRFIDRead() {
        mRFIDReader = RFIDReader.createRFIDReader(this);  // 액티비티의 Context 사용
        mNotification = Notification.createNotification(this);  // 액티비티의 Context 사용
        Charset charset = StandardCharsets.US_ASCII;
        //rfidDigits = null;
        try {
            Filter filter = null;
            ReadParams readParams = new ReadParams();
            readParams.memoryBank = MemoryBank.USER;
            readParams.offset = 0;
            readParams.size = 32;
            readParams.accessPassword = "00000000";
            readParams.timeout = 5000;
            // Read the memory bank
            ReadResult readResult = mRFIDReader.readTag(filter, readParams);
            if (readResult.getStatus() == Status.STATUS_OK) {
                // 읽은 데이터 처리
                byte[] bankData = readResult.getBankData();
                String s = new String(bankData, charset);
                String ItemResult = s.substring(1, 2);
                String PartResult = s.substring(5, 9);
                String ModelResult = s.substring(9, 13);
                String QTYResult = s.substring(13, 15);
                String PLT = s.substring(2,5);
                String rfidDigits = s.substring(0,27);

                if (ModelResult.equals("NE1a")) {
                    switch (ItemResult) {
                        case "A":
                            ItemResult = ItemResult + "(RF)";
                            break;
                        case "B":
                            ItemResult = ItemResult + "(CF)";
                            break;
                        case "C":
                            ItemResult = ItemResult + "(Back)";
                            break;
                        case "D":
                            ItemResult = ItemResult + "(QTR LH)";
                            break;
                        case "E":
                            ItemResult = ItemResult + "(QTR RH)";
                            break;
                        case "F":
                            ItemResult = ItemResult + "(EXTN LH)";
                            break;
                        case "H":
                            ItemResult = ItemResult + "(EXTN RH)";
                            break;
                        case "I":
                            ItemResult = ItemResult = "(HOOD)";
                            break;
                        case "J":
                            ItemResult = ItemResult + "(T/GATE)";
                            break;

                        default:
                            ItemResult = "";
                            break;
                    }
                } else if (ModelResult.equals("ME1a")) {
                    switch (ItemResult) {
                        case "K":
                            ItemResult = ItemResult + "(CF)";
                            break;
                        case "L":
                            ItemResult = ItemResult + "(RF)";
                            break;
                        case "M":
                            ItemResult = ItemResult + "(QTR LH)";
                            break;
                        case "N":
                            ItemResult = ItemResult + "(QTR RH)";
                            break;
                        case "O":
                            ItemResult = ItemResult + "(EXTN LH)";
                            break;
                        case "P":
                            ItemResult = ItemResult + "(EXTN RH)";
                            break;
                        case "Q":
                            ItemResult = ItemResult + "(ROOF)";
                            break;
                        case "R":
                            ItemResult = ItemResult + "(T/GATE)";
                            break;
                        case "S":
                            ItemResult = ItemResult + "(HOOD)";
                            break;
                        case "T":
                            ItemResult = ItemResult + "(FENDER LH)";
                            break;
                        case "U":
                            ItemResult = ItemResult + "(FENDER RH)";
                            break;

                        default:
                            ItemResult = "";
                            break;
                    }
                }


                if (ModelResult.equals("NE1a")) {
                    switch (PartResult) {
                        case "2111":
                            PartResult = PartResult + "(65500-PI810)";
                            break;
                        case "2112":
                            PartResult = PartResult + "(65500-PI710)";
                            break;
                        case "2113":
                            PartResult = PartResult + "(65500-PI800)";
                            break;
                        case "2114":
                            PartResult = PartResult + "(65500-PI700)";
                            break;
                        case "2101":
                            PartResult = PartResult + "(65100-PI200)";
                            break;
                        case "2102":
                            PartResult = PartResult + "(65100-PI700)";
                            break;
                        case "2131":
                            PartResult = PartResult + "(69100-PI000)";
                            break;
                        case "3131":
                            PartResult = PartResult + "(71601-PI000)";
                            break;
                        case "4131":
                            PartResult = PartResult + "(71602-PI000)";
                            break;
                        case "3141":
                            PartResult = PartResult + "(71550-PI000)";
                            break;
                        case "3142":
                            PartResult = PartResult + "(71550-PI020)";
                            break;
                        case "4141":
                            PartResult = PartResult + "(71560-PI000)";
                            break;
                        case "4142":
                            PartResult = PartResult + "(71560-PI020)";
                            break;
                        case "7151":
                            PartResult = PartResult + "(66400-PI000)";
                            break;
                        case "7101":
                            PartResult = PartResult + "(72801-PI000)";
                            break;
                        case "7102":
                            PartResult = PartResult + "(72801-PI010)";
                            break;
                        default:
                            PartResult = PartResult + "";
                            break;
                    }
                } else if (ModelResult.equals("ME1a")) {
                    switch (PartResult) {
                        case "2201":
                            PartResult = PartResult + "(65100-TD000)";
                            break;
                        case "2215":
                            PartResult = PartResult + "(65500-TD000)";
                            break;
                        case "2216":
                            PartResult = PartResult + "(65500-TD020)";
                            break;
                        case "3231":
                            PartResult = PartResult + "(71601-TD000)";
                            break;
                        case "3232":
                            PartResult = PartResult + "(71601-TD060)";
                            break;
                        case "4231":
                            PartResult = PartResult + "(71602-TD000)";
                            break;
                        case "4232":
                            PartResult = PartResult + "(71602-TD060)";
                            break;
                        case "4234":
                            PartResult = PartResult + "(71602-TD070)";
                            break;
                        case "3241":
                            PartResult = PartResult + "(71550-TD000)";
                            break;
                        case "4241":
                            PartResult = PartResult + "(71560-TD000)";
                            break;
                        case "5202":
                            PartResult = PartResult + "(67110-TD060)";
                            break;
                        case "7202":
                            PartResult = PartResult + "(72801-TD000)";
                            break;
                        case "7251":
                            PartResult = PartResult + "(66401-TD000)";
                            break;
                        case "7261":
                            PartResult = PartResult + "(66310-TD000)";
                            break;
                        case "7271":
                            PartResult = PartResult + "(66320-TD000)";
                            break;
                        default:
                            PartResult = PartResult + "";
                            break;

                    }

                }

                RFIDNOFULL = rfidDigits;
                rfidResult.setText(RFIDNOFULL);
                Item.setText(ItemResult);
                Part.setText(PartResult);
                Model.setText(ModelResult);
                QTY.setText(QTYResult);
                PLTBox.setText(PLT);
               // Toast.makeText(this, rfidDigits.length(), Toast.LENGTH_LONG).show();
                // 성공적인 리딩 시 알림 (버저)
                mNotification.startBuzzer(16, 100, 0, 1);
            } else {
                // 실패 시 상태를 표시
                rfidResult.setText(readResult.getStatus().toString());
                mNotification.startBuzzer(1, 100, 100, 2);
            }

            try {
                CallableStatement callableStatement = con.prepareCall("{call sp_PdaA010FrmB_SARE_POP1(@WORKGB=?, @RFIDNOFULL=?, @TKNUM=?, @MESLOT=?, @LOTNO=?, @RFIDYN=?, @SACHRKEY=?)}");
                callableStatement.setString(1, "SEARCH_LOT");
                callableStatement.setString(2, RFIDNOFULL);
                callableStatement.setString(3,  TKNUM);
                callableStatement.setString(4, MESLOTValue);
                callableStatement.setString(5, RFIDNOFULL);
                callableStatement.setString(6, "Y");
                callableStatement.setString(7, SACHRKEY);
                ResultSet resultSet = callableStatement.executeQuery();
                if(isThere(resultSet,"CHK")){
                    chkexists = true;
                } else{
                    chkexists = false;
                }
                if(resultSet.next()){
                    if(!chkexists){
                        resultSet.close();
                        callableStatement.close();
                        ERRMSG.setText("MES LOT  and RFID Scan Completed");
                        //finish();
                    }
                    else{
                        ERRMSG.setText(resultSet.getString("ERRMSG"));

                    }
                }


            } catch (SQLException e) {
                e.printStackTrace();
                Toast.makeText(ScanDialog.this, "Error fetching data: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        } catch (RuntimeException e) {
            // 에러 발생 시 Toast 메시지 표시
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            mRFIDReader.releaseRFIDReader();
            mNotification.releaseNotification();
        }
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
