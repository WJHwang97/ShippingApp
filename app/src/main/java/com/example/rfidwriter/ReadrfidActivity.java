package com.example.rfidwriter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.keyence.autoid.sdk.notification.Notification;
import com.keyence.autoid.sdk.rfid.Filter;
import com.keyence.autoid.sdk.rfid.RFIDReader;
import com.keyence.autoid.sdk.rfid.ReadResult;
import com.keyence.autoid.sdk.rfid.Status;
import com.keyence.autoid.sdk.rfid.params.MemoryBank;
import com.keyence.autoid.sdk.rfid.params.ReadParams;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReadrfidActivity extends AppCompatActivity  {

    private RFIDReader mRFIDReader;
    private Notification mNotification;
    private TextView rfidResult;


    private TextView rfidResultLen;

    private TextView rfidResultItem;

    private TextView rfidResultPLT;

    private TextView rfidResultPart;

    private TextView rfidResultModel;

    private TextView rfidResultQTY;

    private TextView rfidResultDate;

    private TextView rfidResultTime;


    Button button_read;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_readrfid);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        button_read = findViewById(R.id.button_read);

        final LoadingDialog loadingDialog = new LoadingDialog(ReadrfidActivity.this);

        button_read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingDialog.startLoadingDialog();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadingDialog.dismissDialog();
                    }
                },5000);

                v.setEnabled(false);

                ExecutorService executor = Executors.newSingleThreadExecutor();
                Handler handler_1 = new Handler(Looper.getMainLooper());
                executor.execute(() -> {
                    //Background work here
                    handler_1.post(() -> {
                        ReadRfid(v);
                        loadingDialog.dismissDialog();
                    });
                });

                v.setEnabled(true);
            }
        });
    }

    public void onClickBtnInit(View v) {

        rfidResult = findViewById(R.id.rfidResult);
        rfidResultLen = findViewById(R.id.rfidResultLen);
        rfidResultItem = findViewById(R.id.rfidResultItem);
        rfidResultPLT = findViewById(R.id.rfidResultPLT);
        rfidResultPart = findViewById(R.id.rfidResultPart);
        rfidResultModel = findViewById(R.id.rfidResultModel);
        rfidResultQTY = findViewById(R.id.rfidResultQTY);
        rfidResultDate = findViewById(R.id.rfidResultDate);
        rfidResultTime = findViewById(R.id.rfidResultTime);

        rfidResult.setText("");
        rfidResultLen.setText("");
        rfidResultItem.setText("");
        rfidResultPLT.setText("");
        rfidResultPart.setText("");
        rfidResultModel.setText("");
        rfidResultQTY.setText("");
        rfidResultDate.setText("");
        rfidResultTime.setText("");

    }

    private void ReadRfid(View v)
    {
        mRFIDReader = RFIDReader.createRFIDReader(this);
        mNotification = Notification.createNotification(this);
        Charset charset = StandardCharsets.US_ASCII;

        try
        {
            Filter filter = null;
            ReadParams readParams = new ReadParams();
            readParams.memoryBank = MemoryBank.USER;
            readParams.offset = 0;
            readParams.size = 32;
            //readParams.size = 8;
            readParams.accessPassword = "00000000";
            readParams.timeout = 5000;
            // Read the memory bank.
            ReadResult readResult = mRFIDReader.readTag(filter, readParams);

            rfidResult = findViewById(R.id.rfidResult);
            rfidResultLen = findViewById(R.id.rfidResultLen);
            rfidResultItem = findViewById(R.id.rfidResultItem);
            rfidResultPLT = findViewById(R.id.rfidResultPLT);
            rfidResultPart = findViewById(R.id.rfidResultPart);
            rfidResultModel = findViewById(R.id.rfidResultModel);
            rfidResultQTY = findViewById(R.id.rfidResultQTY);
            rfidResultDate = findViewById(R.id.rfidResultDate);
            rfidResultTime = findViewById(R.id.rfidResultTime);

            if (readResult.getStatus() == Status.STATUS_OK) {
                // Acquire the data of the read memory bank.

                byte[] bankData = readResult.getBankData();

                String s = new String(bankData,charset);
                String Item = s.substring(1,2);
                String PLT = s.substring(2,5);
                String Part = s.substring(5,9);
                String Model = s.substring(9,13);
                String QTY = s.substring(13,15);
                String Date = s.substring(15,23);
                String Time = s.substring(23,27);

                if(Model.equals("NE1a")) {
                    switch(Item) {
                        case "A":
                            Item = Item + "(RF)";
                            break;
                        case "B":
                            Item = Item + "(CF)";
                            break;
                        case "C":
                            Item = Item + "(Back)";
                            break;
                        case "D":
                            Item = Item + "(QTR LH)";
                            break;
                        case "E":
                            Item = Item + "(QTR RH)";
                            break;
                        case "F":
                            Item = Item + "(EXTN LH)";
                            break;
                        case "H":
                            Item = Item + "(EXTN RH)";
                            break;
                        case "I":
                            Item = Item = "(HOOD)";
                            break;
                        case "J":
                            Item = Item + "(T/GATE)";
                            break;

                        default :
                            Item = "";
                            break;
                    }}
                else if(Model.equals("ME1a")){
                    switch(Item){
                        case "K":
                            Item = Item + "(CF)";
                            break;
                        case "L":
                            Item = Item + "(RF)";
                            break;
                        case "M":
                            Item = Item + "(QTR LH)";
                            break;
                        case "N":
                            Item = Item + "(QTR RH)";
                            break;
                        case "O":
                            Item = Item + "(EXTN LH)";
                            break;
                        case "P":
                            Item = Item + "(EXTN RH)";
                            break;
                        case "Q":
                            Item = Item + "(ROOF)";
                            break;
                        case "R":
                            Item = Item + "(T/GATE)";
                            break;
                        case "S":
                            Item = Item + "(HOOD)";
                            break;
                        case "T":
                            Item = Item + "(FENDER LH)";
                            break;
                        case "U":
                            Item = Item + "(FENDER RH)";
                            break;

                        default :
                            Item = "";
                            break;
                    }}


                if(Model.equals("NE1a")) {
                    switch(Part) {
                        case "2111":
                            Part = Part + "(65500-PI810)";
                            break;
                        case "2112":
                            Part = Part + "(65500-PI710)";
                            break;
                        case "2113":
                            Part = Part + "(65500-PI800)";
                            break;
                        case "2114":
                            Part = Part + "(65500-PI700)";
                            break;
                        case "2101":
                            Part = Part + "(65100-PI200)";
                            break;
                        case "2102":
                            Part = Part + "(65100-PI700)";
                            break;
                        case "2131":
                            Part = Part + "(69100-PI000)";
                            break;
                        case "3131":
                            Part = Part + "(71601-PI000)";
                            break;
                        case "4131":
                            Part = Part + "(71602-PI000)";
                            break;
                        case "3141":
                            Part = Part + "(71550-PI000)";
                            break;
                        case "3142":
                            Part = Part + "(71550-PI020)";
                            break;
                        case "4141":
                            Part = Part + "(71560-PI000)";
                            break;
                        case "4142":
                            Part = Part + "(71560-PI020)";
                            break;
                        case "7151":
                            Part = Part + "(66400-PI000)";
                            break;
                        case "7101":
                            Part = Part + "(72801-PI000)";
                            break;
                        case "7102":
                            Part = Part + "(72801-PI010)";
                            break;
                        default :
                            Part = Part + "";
                            break;
                    }}
                else if(Model.equals("ME1a")){
                    switch(Part){
                        case "2201":
                            Part = Part + "(65100-TD000)";
                            break;
                        case "2215":
                            Part = Part + "(65500-TD000)";
                            break;
                        case "2216":
                            Part = Part + "(65500-TD020)";
                            break;
                        case "3231":
                            Part = Part + "(71601-TD000)";
                            break;
                        case "3232":
                            Part = Part + "(71601-TD060)";
                            break;
                        case "4231":
                            Part = Part + "(71602-TD000)";
                            break;
                        case "4232":
                            Part = Part + "(71602-TD060)";
                            break;
                        case "4234":
                            Part = Part + "(71602-TD070)";
                            break;
                        case "3241":
                            Part = Part + "(71550-TD000)";
                            break;
                        case "4241":
                            Part = Part + "(71560-TD000)";
                            break;
                        case "5202":
                            Part = Part + "(67110-TD060)";
                            break;
                        case "7202":
                            Part = Part + "(72801-TD000)";
                            break;
                        case "7251":
                            Part = Part + "(66401-TD000)";
                            break;
                        case "7261":
                            Part = Part + "(66310-TD000)";
                            break;
                        case "7271":
                            Part = Part + "(66320-TD000)";
                            break;
                        default :
                            Part = Part + "";
                            break;

                    }}




                rfidResult.setText(s);
                rfidResultLen.setText(String.valueOf(s.length()));
                rfidResultItem.setText("Item: " + Item);
                rfidResultPLT.setText("PLT Code: " + PLT);
                rfidResultPart.setText("Part No: " + Part);
                rfidResultModel.setText("Model: " + Model);
                rfidResultQTY.setText("Quantity: " + QTY);
                rfidResultDate.setText("Date: " + Date);
                rfidResultTime.setText("Time: " + Time);

                mNotification.startBuzzer(16, 100, 0, 1);

            }
            else
            {
                rfidResult.setText(readResult.getStatus().toString());
                rfidResultLen.setText("0");
                rfidResultPart.setText("0");
                mNotification.startBuzzer(1, 100, 100, 2);
            }
        }
        catch (RuntimeException e)
        {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        finally
        {
            mRFIDReader.releaseRFIDReader();
            mNotification.releaseNotification();
        }

    }


}