package com.example.rfidwriter;

import android.annotation.SuppressLint;
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

public class OrderDialog extends AppCompatActivity {

    private Button returnbutton;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orderdialog);

        returnbutton = findViewById(R.id.returnbutton);

        returnbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button();  // 현재 액티비티 종료하고 이전 액티비티로 돌아감
            }
        });
    }

    private void button(){
        Intent intent = new Intent(getApplicationContext(), SalesLoadingScan.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        if (shouldAllowBackPress()) {
            super.onBackPressed();
        }
    }
    // 백버튼 동작 조건을 처리하는 메서드
    private boolean shouldAllowBackPress() {
        // 조건에 따라 true 또는 false 반환
        // 예: 특정 상태에서만 뒤로가기 동작 허용
        return false; // 기본적으로 true 설정
    }
}