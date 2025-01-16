package com.example.rfidwriter;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

import android.content.Intent;
import android.net.http.UrlRequest;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.Base64;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends AppCompatActivity {
    SqlConnectionClass sqlConnectionClass;
    Connection con;
    EditText User_ID, Password;
    Button loginbtn;
    TextView StatusCHK;
    private String data;
    private String Name;
    private int columncount;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        User_ID = findViewById(R.id.User_ID);
        Password = findViewById(R.id.Password);
        loginbtn = findViewById(R.id.loginbtn);
        StatusCHK = findViewById(R.id.StatusCheck);

        sqlConnectionClass = new SqlConnectionClass();
        try {
            con = sqlConnectionClass.CONN();
            if(con != null) {
                StatusCHK.setTextColor(GREEN);
                StatusCHK.setText("ONLINE");
            }
        } catch (Exception e) {
            e.printStackTrace();
            con = null;
            StatusCHK.setTextColor(RED);
            StatusCHK.setText("OFFLINE");
            Toast.makeText(this, "Database connection failed.", Toast.LENGTH_LONG).show();
        }



        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ID = User_ID.getText().toString();
                String PW = Password.getText().toString();
                if(con == null)
                {
                    OfflineMode(ID,PW);
                }
                else
                {
                    OnlineMode(ID,PW);
                }
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    @Override
    public void onBackPressed() {
        // 필요한 커스텀 동작 실행
        Toast.makeText(this, "Back button pressed.", Toast.LENGTH_SHORT).show();

        // 특정 조건에서만 super.onBackPressed 호출
        // 예: 특정 상황에서는 기본 동작을 유지
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
    private void OnlineMode(String ID, String PW)
    {
        try {
            CallableStatement callableStatement = con.prepareCall("{call PDA_LOGIN(@WORKGB=?, @USERID=?, @PASSWD=?)}");
            callableStatement.setString(1, "LOGIN");
            callableStatement.setString(2, ID);
            callableStatement.setString(3, md5Encrypt(PW));
            ResultSet resultSet = callableStatement.executeQuery();
            if(resultSet.next()){
                ResultSetMetaData metaData = resultSet.getMetaData();
                columncount = metaData.getColumnCount();
                if(columncount > 1)
                {
                    data = resultSet.getString(1);
                    Name = resultSet.getString(2);
                }
                else{
                    data = resultSet.getString(1);
                }
            }
            if(data.equals("ERROR:X")){
                Toast.makeText(Login.this, "PW wrong", Toast.LENGTH_LONG).show();
            }
            else if(data.equals("ERROR:ID is not exited"))
            {
                Toast.makeText(Login.this, "ID do not exist", Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Login.this,MainActivity.class);
                intent.putExtra("EMPNO", data);
                intent.putExtra("Name", Name);
                startActivity(intent);
            }
            callableStatement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "SQLException 발생", Toast.LENGTH_LONG).show();
        }

    }
    private void OfflineMode(String ID, String PW)
    {

        //Toast.makeText(Login.this, "Entered with OFFLINE Mode. Only READ/WRITE available", Toast.LENGTH_LONG).show();
        if(ID.equals("offline") && PW.equals("Sewon12!@"))
        {
            Intent intent = new Intent(Login.this,MainActivity.class);
            intent.putExtra("EMPNO", ID);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(Login.this, "Please connect to Sewon Network", Toast.LENGTH_LONG).show();
        }
    }


    private static String md5Encrypt(String str) {
        try {
            // 1. 입력 문자열을 UTF-16 LE 바이트 배열로 변환
            byte[] data = str.getBytes("UTF-16LE");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(data);
            String base64Encoded = Base64.getEncoder().encodeToString(hash);
            return base64Encoded;
        } catch (Exception e) {
            throw new RuntimeException("MD5 encryption failed", e);
        }
    }
    }



