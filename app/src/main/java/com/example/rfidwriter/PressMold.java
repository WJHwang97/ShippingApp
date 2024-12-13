package com.example.rfidwriter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.keyence.autoid.sdk.rfid.params.WriteParams;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PressMold extends AppCompatActivity {


    private TextView RFIDRead;
    private Button button_write;
    private EditText RFIDWrite;
    private Button button_read;
    private String writeString;
    private RFIDReader mRFIDReader;
    private Notification mNotification;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pressmold);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        RFIDRead = findViewById(R.id.RFIDRead);
        button_write = findViewById(R.id.button_write);
        RFIDWrite = findViewById(R.id.RFIDWrite);
        button_read = findViewById(R.id.button_read);

        final LoadingDialog loadingDialog = new LoadingDialog(PressMold.this);
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

    }
    public void onClickBtnInit(View v) {

        RFIDRead.setText("");


    }

    private String asciiToHex(String asciiStr) {
        StringBuilder hex = new StringBuilder();
        for (char c : asciiStr.toCharArray()) {
            hex.append(String.format("%02X", (int) c)); // 각 문자에 대해 16진수 변환
        }
        return hex.toString();
    }
    private String padHexTo24Digits(String hexStr) {
        StringBuilder paddedHex = new StringBuilder(hexStr);
        while (paddedHex.length() < 24) { // 24자리 Hexadecimal이 될 때까지
            paddedHex.append("20"); // 20을 추가 (ASCII 공백)
        }
        return paddedHex.toString();
    }
    private byte[] hexStringToByteArray(String hexStr) {
        int len = hexStr.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexStr.charAt(i), 16) << 4)
                    + Character.digit(hexStr.charAt(i + 1), 16));
        }
        return data;
    }

    private void WriteRfid(View v) {
        writeString = RFIDWrite.getText().toString();

        mRFIDReader = RFIDReader.createRFIDReader(this);
        mNotification = Notification.createNotification(this);

        try {
            Filter filter = null;
            WriteParams writeParams = new WriteParams();

            // 1단계: EPC 메모리를 빈 값으로 덮어 기존 데이터 삭제
            writeParams.memoryBank = MemoryBank.EPC;
            writeParams.offset = 2; // EPC 데이터 부분부터 시작

            // 빈 데이터 생성 (EPC 메모리 크기에 맞춰 조정, 예: 16 바이트)
            byte[] emptyData = new byte[16]; // 모든 값을 0으로 초기화된 빈 바이트 배열
            writeParams.data = emptyData;
            writeParams.accessPassword = "00000000";
            writeParams.timeout = 5000;

            // 빈 데이터로 덮어쓰기
            Status clearStatus = mRFIDReader.writeTag(filter, writeParams);

            if (clearStatus == Status.STATUS_OK) {
                Toast.makeText(this, "Memory cleared successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Clear failed: " + clearStatus.toString(), Toast.LENGTH_SHORT).show();
                return; // 만약 초기화 실패 시 새 데이터 쓰기 작업 중지
            }
            String hexString = (asciiToHex(writeString));
            byte[] hexData = hexStringToByteArray(hexString);
            writeParams.data = hexData;
            Status writeStatus = mRFIDReader.writeTag(filter, writeParams);
            if (writeStatus == Status.STATUS_OK) {
                mNotification.startBuzzer(16, 100, 0, 1);
                Toast.makeText(this, "Success write: " + hexString, Toast.LENGTH_SHORT).show();
            } else {
                mNotification.startBuzzer(1, 100, 100, 2);
                Toast.makeText(this, "Write failed: " + writeStatus.toString(), Toast.LENGTH_SHORT).show();
            }


        } catch (RuntimeException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            mRFIDReader.releaseRFIDReader();
            mNotification.releaseNotification();
        }
    }


    private void ReadRfid(View v) {
        mRFIDReader = RFIDReader.createRFIDReader(this);
        mNotification = Notification.createNotification(this);

        try {
            Filter filter = null;
            ReadParams readParams = new ReadParams();

            // EPC 메모리 뱅크 설정 및 오프셋 설정
            readParams.memoryBank = MemoryBank.EPC;
            readParams.offset = 2; // EPC 메모리의 데이터 부분부터 시작
            readParams.size = 6;  // 읽기 크기 설정 (필요 시 조정)
            readParams.accessPassword = "00000000";
            readParams.timeout = 5000;

            // 데이터 읽기
            ReadResult readResult = mRFIDReader.readTag(filter, readParams);

            if (readResult.getStatus() == Status.STATUS_OK) {
                byte[] bankData = readResult.getBankData();
                String asciiData = new String(bankData, StandardCharsets.US_ASCII);

                RFIDRead.setText(asciiData);
                Toast.makeText(this, asciiData, Toast.LENGTH_LONG).show();
                Toast.makeText(this, "RFID 읽기 성공!", Toast.LENGTH_SHORT).show();
                mNotification.startBuzzer(16, 100, 0, 1);
            } else {
                RFIDRead.setText("");
                Toast.makeText(this, "RFID 읽기 실패: " + readResult.getStatus(), Toast.LENGTH_SHORT).show();
                mNotification.startBuzzer(1, 100, 100, 2);
            }
        } catch (RuntimeException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            mRFIDReader.releaseRFIDReader();
            mNotification.releaseNotification();
        }
    }
}