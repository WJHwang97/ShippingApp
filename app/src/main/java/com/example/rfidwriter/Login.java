package com.example.rfidwriter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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

    private String data;
    private String Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // SQL 연결
        sqlConnectionClass = new SqlConnectionClass();
        con = sqlConnectionClass.CONN();

        User_ID = findViewById(R.id.User_ID);
        Password = findViewById(R.id.Password);
        loginbtn = findViewById(R.id.loginbtn);
        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    CallableStatement callableStatement = con.prepareCall("{call PDA_LOGIN(@WORKGB=?, @USERID=?, @PASSWD=?)}");
                    String ID = User_ID.getText().toString();
                    String PW = Password.getText().toString();
                    callableStatement.setString(1, "LOGIN");
                    callableStatement.setString(2, ID);
                    callableStatement.setString(3, PW);
                    ResultSet resultSet = callableStatement.executeQuery();

                    if(resultSet.next()){
                        data = resultSet.getString(1);
                        Name = resultSet.getString(2);
                    }
                    if(data.equals("ERROR:X")){
                        Toast.makeText(Login.this, "ID/PW wrong", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_LONG).show();

                    }

                    Intent intent = new Intent(Login.this,MainActivity.class);
                    intent.putExtra("EMPNO", data);
                    intent.putExtra("Name", Name);
                    startActivity(intent);
                    /*
                    callableStatement.execute();

                    String errgb = callableStatement.getString(5);
                    String str;
                    if ("O".equals(errgb)) {
                        str = "로그인 성공";
                        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(Login.this,MainActivity.class);
                        startActivity(intent);
                    } else {
                        str = "로그인 실패";
                        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_LONG).show();
                    }*/

                    callableStatement.close();

                } catch (SQLException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "SQLException 발생", Toast.LENGTH_LONG).show();
                }
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    }



