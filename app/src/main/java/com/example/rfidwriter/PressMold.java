package com.example.rfidwriter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PressMold extends AppCompatActivity {

    private Button Exit;

    private Button BarcodeTagReg;
    private Button DieInfo;
    private Button DieExistChk;
    private Button DieMaintIncome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_press_mold);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Exit = findViewById(R.id.Exit);
        BarcodeTagReg = findViewById(R.id.BarcodeTagReg);
        DieInfo = findViewById(R.id.DieInfo);
        DieExistChk = findViewById(R.id.DieExistChk);
        DieMaintIncome = findViewById(R.id.DieMaintIncome);
        BarcodeTagReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PressMold.this,BarcodeTagReg.class);
                startActivity(intent);
            }
        });
        DieInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PressMold.this,DieInfo.class);
                startActivity(intent);
            }
        });
        DieExistChk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PressMold.this,DieExistChk.class);
                startActivity(intent);
            }
        });
        DieMaintIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PressMold.this,DieMaintIncome.class);
                startActivity(intent);
            }
        });
        Exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moveTaskToBack(true); // 태스크를 백그라운드로 이동
                finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                System.exit(0);
            }
        });


    }
}