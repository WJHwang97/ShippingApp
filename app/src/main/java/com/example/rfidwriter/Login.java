package com.example.rfidwriter;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.UrlRequest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;
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
import androidx.media3.exoplayer.offline.DownloadManager;
import com.keyence.autoid.sdk.rfid.BuildConfig;
import org.json.JSONException;
import org.json.JSONObject;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import android.net.Uri;
import android.os.Environment;

public class Login extends AppCompatActivity {
    SqlConnectionClass sqlConnectionClass;
    Connection con;
    EditText User_ID, Password;
    Button loginbtn;
    ImageButton refreshbtn;
    TextView StatusCHK;
    private String data;
    private String Name;
    private int columncount;
    private String updateUrl = "http://10.250.195.6/shipment/version.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        User_ID = findViewById(R.id.User_ID);
        Password = findViewById(R.id.Password);
        loginbtn = findViewById(R.id.loginbtn);
        StatusCHK = findViewById(R.id.StatusCheck);
        refreshbtn = findViewById(R.id.RefreshButton);
        Log.d("VersionCheck", "BuildConfig.VERSION_NAME: " + BuildConfig.VERSION_NAME);

        sqlConnectionClass = new SqlConnectionClass();
        try {
            con = sqlConnectionClass.CONN();
            if(con != null) {
                StatusCHK.setTextColor(GREEN);
                StatusCHK.setText("ONLINE");
                checkForUpdates();
            }
        } catch (Exception e) {
            e.printStackTrace();
            con = null;
            StatusCHK.setTextColor(RED);
            StatusCHK.setText("OFFLINE");
            Toast.makeText(this, "Database connection failed.", Toast.LENGTH_LONG).show();
        }
        refreshbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Login.this, "Refreshed", Toast.LENGTH_LONG).show();
                Intent First_Page = new Intent(Login.this, Login.class);
                startActivity(First_Page);
            }
        });
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
            //callableStatement.setString(3, md5Encrypt(PW));
            callableStatement.setString(3, (PW));
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
            Toast.makeText(Login.this, "Wrong ID/PW", Toast.LENGTH_LONG).show();
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
    private void checkForUpdates() {
        // OkHttp 클라이언트 생성
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(updateUrl) // 업데이트 정보를 가져올 URL
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 네트워크 실패 처리
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(Login.this, "Network error: " + e.getMessage(), Toast.LENGTH_LONG).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseBody = response.body().string();
                    try {
                        // 서버에서 받은 JSON 파싱
                        JSONObject json = new JSONObject(responseBody);
                        final String latestVersion = json.getString("version"); // 최신 버전
                        String apkUrl = json.getString("download_url"); // APK 다운로드 URL

                        // 현재 앱 버전 가져오기
                        PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                        final String currentVersion = pInfo.versionName; // 현재 앱 버전

                        // 버전 비교 및 업데이트 알림
                        if (!currentVersion.equals(latestVersion)) {
                            runOnUiThread(() -> openWebsite(apkUrl));
                        }
                    } catch (JSONException | PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        runOnUiThread(() -> Toast.makeText(Login.this, "Error parsing response", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    // 서버 응답 실패
                    runOnUiThread(() -> Toast.makeText(Login.this, "Failed to check for updates", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
    private void openWebsite(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }
}



