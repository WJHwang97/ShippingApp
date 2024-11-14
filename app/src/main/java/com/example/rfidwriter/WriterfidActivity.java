package com.example.rfidwriter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import com.keyence.autoid.sdk.rfid.params.WriteParams;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WriterfidActivity extends AppCompatActivity  {

    private RFIDReader mRFIDReader;
    private Notification mNotification;
    private TextView txtPart;
    private TextView txtCar;
    private TextView txtCat;
    private TextView txtCatUno;
    private TextView txtPLTUNo;
    private EditText editTextNumberPLT ;
    private EditText editTextNumberQty ;
    private EditText editTextDate ;
    private EditText editTextTime ;
    private String writeString;
    private TextView rfidResult;
    private TextView rfidResultLen;

    Button button_read;
    Button button_write;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_writerfid);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent rcvIntent = getIntent();

        txtPart = findViewById(R.id.txtPart);
        txtPart.setText(rcvIntent.getStringExtra("part"));
        txtCar = findViewById(R.id.txtCar);
        txtCar.setText(rcvIntent.getStringExtra("car"));
        txtCat = findViewById(R.id.txtCat);
        txtCat.setText(rcvIntent.getStringExtra("cat"));
        txtCatUno = findViewById(R.id.txtCatUno);
        txtCatUno.setText(rcvIntent.getStringExtra("no"));
        txtPLTUNo = findViewById(R.id.txtPLTUNo);
        txtPLTUNo.setText(rcvIntent.getStringExtra("plt"));

        editTextNumberPLT = findViewById(R.id.editTextNumberPLT);
        editTextNumberQty = findViewById(R.id.editTextNumberQty);
        editTextDate = findViewById(R.id.editTextDate);
        editTextTime = findViewById(R.id.editTextTime);


        if(editTextNumberPLT.getText().toString().length() == 1)
        {

        }



        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault());
        String currentDateTime = dateFormat.format(new Date());
        String currentDate = currentDateTime.substring(0, 8);  // yyyyMMdd
        String currentTime = currentDateTime.substring(8, 12); // HH
        editTextDate.setText(currentDate);
        editTextTime.setText(currentTime);

        button_read = findViewById(R.id.button_read);
        button_write = findViewById(R.id.button_write);

        final LoadingDialog loadingDialog = new LoadingDialog(WriterfidActivity.this);

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

        button_write.setOnClickListener(new View.OnClickListener() {
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
                        WriteRfid(v);
                        loadingDialog.dismissDialog();
                    });
                });

                v.setEnabled(true);
            }
        });

        editTextNumberPLT.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            public void onFocusChange(View v, boolean gainFocus)
            {
                if (gainFocus)
                {
                    v.setBackgroundColor(Color.parseColor("purple"));
                }
                else
                {
                    v.setBackgroundColor(Color.parseColor("#3F51B5"));
                }
            }
        });

        editTextNumberQty.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            public void onFocusChange(View v, boolean gainFocus)
            {
                if (gainFocus)
                {
                    v.setBackgroundColor(Color.parseColor("purple"));
                }
                else
                {
                    v.setBackgroundColor(Color.parseColor("#3F51B5"));
                }
            }
        });

        editTextDate.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            public void onFocusChange(View v, boolean gainFocus)
            {
                if (gainFocus)
                {
                    v.setBackgroundColor(Color.parseColor("purple"));
                }
                else
                {
                    v.setBackgroundColor(Color.parseColor("#3F51B5"));
                }
            }
        });

        editTextTime.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            public void onFocusChange(View v, boolean gainFocus)
            {
                if (gainFocus)
                {
                    v.setBackgroundColor(Color.parseColor("purple"));
                }
                else
                {
                    v.setBackgroundColor(Color.parseColor("#3F51B5"));
                }
            }
        });

    }
//        public void onClickBtnWrite(View v) {
//
//            if (txtPLTUNo.getText() == "" || txtPart.getText() == "" ||
//                    txtCat.getText() == "" || txtCatUno.getText() == "")
//            {
//                Toast.makeText(this, "Rfid Info. Error", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//            if (editTextNumberPLT.getText().toString().isEmpty() ||
//                editTextNumberPLT.getText().length() != 3)
//            {
//                Toast.makeText(this, "PLT No. Error", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//            if (editTextNumberQty.getText().toString().isEmpty() ||
//                    editTextNumberQty.getText().length() != 2)
//            {
//                Toast.makeText(this, "Load Qty. Error", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//            if (editTextDate.getText().toString().isEmpty() ||
//                    editTextDate.getText().length() != 8)
//            {
//                Toast.makeText(this, "Make Date error", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//            if (editTextTime.getText().toString().isEmpty() ||
//                    editTextTime.getText().length() != 4)
//            {
//                Toast.makeText(this, "Make Time error", Toast.LENGTH_LONG).show();
//                return;
//            }
//            writeString = "";
//            writeString = "S" + txtCatUno.getText() + editTextNumberPLT.getText().toString() +
//                          txtPLTUNo.getText() + "NE1a" + editTextNumberQty.getText().toString() +
//                          editTextDate.getText().toString() + editTextTime.getText().toString() +
//                          "                                     ";
//            //Toast.makeText(this, writeString, Toast.LENGTH_LONG).show();
//
//            if (writeString.length() != 64)
//            {
//                Toast.makeText(this, "Make Time error", Toast.LENGTH_LONG).show();
//                return;
//            }
//
//            WriteRfid(v);
//        }

//        public void onClickBtnRead(View v) {
//            ReadRfid(v);
//        }

    public void onClickBtnInit(View v) {

        rfidResult = findViewById(R.id.rfidResult);
        rfidResultLen = findViewById(R.id.rfidResultLen);

        rfidResult.setText("");
        rfidResultLen.setText("");

    }
    private void WriteRfid(View v)
    {
        if (txtPLTUNo.getText() == "" || txtPart.getText() == "" ||
                txtCat.getText() == "" || txtCatUno.getText() == "")
        {
            Toast.makeText(this, "Rfid Info. Error", Toast.LENGTH_LONG).show();
            return;
        }

        if(editTextNumberPLT.getText().length() == 1) {
            String currentText = editTextNumberPLT.getText().toString();
            editTextNumberPLT.setText("00" + currentText);
        }
        else if(editTextNumberPLT.getText().length() == 2) {
            String currentText = editTextNumberPLT.getText().toString();
            editTextNumberPLT.setText("0" + currentText);
        }

        if (editTextNumberPLT.getText().toString().isEmpty() ||
                editTextNumberPLT.getText().length() != 3)
        {
            Toast.makeText(this, "PLT No. Error", Toast.LENGTH_LONG).show();
            return;
        }

        if(editTextNumberQty.getText().length() == 1) {
            String currentText = editTextNumberQty.getText().toString();
            editTextNumberQty.setText("0" + currentText);
        }

        if (editTextNumberQty.getText().toString().isEmpty() ||
                editTextNumberQty.getText().length() != 2)
        {
            Toast.makeText(this, "Load Qty. Error", Toast.LENGTH_LONG).show();
            return;
        }

        if (editTextDate.getText().toString().isEmpty() ||
                editTextDate.getText().length() != 8)
        {
            Toast.makeText(this, "Make Date error", Toast.LENGTH_LONG).show();
            return;
        }

        if (editTextTime.getText().toString().isEmpty() ||
                editTextTime.getText().length() != 4)
        {
            Toast.makeText(this, "Make Time error", Toast.LENGTH_LONG).show();
            return;
        }
        writeString = "";
        writeString = "S" + txtCatUno.getText() + editTextNumberPLT.getText().toString() +
                txtPLTUNo.getText() + txtCar.getText() + editTextNumberQty.getText().toString() +
                editTextDate.getText().toString() + editTextTime.getText().toString() +
                "                                     ";
//?袁⑹춭
//            writeString = "A" + txtCatUno.getText() + editTextNumberPLT.getText().toString() +
//                    txtPLTUNo.getText() + txtCar.getText() + editTextNumberQty.getText().toString() +
//                    editTextDate.getText().toString() + editTextTime.getText().toString() +
//                    "                                     ";

        //Toast.makeText(this, writeString, Toast.LENGTH_LONG).show();

        if (writeString.length() != 64)
        {
            Toast.makeText(this, "Make Time error", Toast.LENGTH_LONG).show();
            return;
        }

        mRFIDReader = RFIDReader.createRFIDReader(this);
        mNotification = Notification.createNotification(this);

        try
        {
            //Create a filter for selecting the specific tag ID.
            //Filter filter = Filter.createTagIDFilter("06133000000000000000174C");
            Filter filter = null;
            // Specify the parameter for reading the memory bank.
            WriteParams writeParams = new WriteParams();
            writeParams.memoryBank = MemoryBank.USER;
            writeParams.offset = 0;
            //writeParams.data = "SA0012111NE1a020".getBytes(StandardCharsets.US_ASCII);
            writeParams.data = writeString.getBytes(StandardCharsets.US_ASCII);
            writeParams.accessPassword = "00000000";
            writeParams.timeout = 5000;

            // Write data to the memory bank.

            Status status = mRFIDReader.writeTag(filter, writeParams);
            if (status == Status.STATUS_OK)
            {
                mNotification.startBuzzer(16, 100, 0, 1);
                Toast.makeText(this, "Success write", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mNotification.startBuzzer(1, 100, 100, 2);
                Toast.makeText(this, status.toString(), Toast.LENGTH_SHORT).show();
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

            if (readResult.getStatus() == Status.STATUS_OK) {
                // Acquire the data of the read memory bank.

                byte[] bankData = readResult.getBankData();
                String s = new String(bankData,charset);
                rfidResult.setText(s);
                rfidResultLen.setText(String.valueOf(s.length()));

                mNotification.startBuzzer(16, 100, 0, 1);

            }
            else
            {
                rfidResult.setText(readResult.getStatus().toString());
                rfidResultLen.setText("0");
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

    protected void onDestroy() {
        super.onDestroy();
        // Destroy the instance of the DeviceInfo class and release the resource.

        //mRFIDReader.releaseRFIDReader();
        //mNotification.releaseNotification();
    }
}