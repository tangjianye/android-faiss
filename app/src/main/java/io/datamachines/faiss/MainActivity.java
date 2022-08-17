package io.datamachines.faiss;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("faiss");
    }

    Button StartTest;
    TextView tv;
    String data;
    int currentValue;

    void StartTesting() {
        File file = new File("/sdcard/tmp/");
        if (!file.exists()) {
            file.mkdirs();
        }
        StartTest.setEnabled(false);
        tv.setText("test...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    data = stringFromJNI(currentValue);
                    Log.e("tjy", "currentValue = " + currentValue + " ;data = " + data);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UpdateGUI();
                        }
                    });

                } catch (Exception e) {

                }
            }
        }).start();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        tv = findViewById(R.id.sample_text);
        tv.setText("Testing for now - Build time");

        StartTest = findViewById(R.id.button);

        //========================================
        //      动态获取权限
        //========================================
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                //需要的权限
                String[] permArr = {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                };
                boolean needReq = false;
                for (int i = 0; i < permArr.length; i++) {
                    if (ContextCompat.checkSelfPermission(this, permArr[i]) != PackageManager.PERMISSION_GRANTED) {
                        needReq = true;
                        break;
                    }
                }

                if (needReq) {
                    ActivityCompat.requestPermissions(this, permArr, 10);
                } else {
                    handlePreViewCallBack();
                }
            } catch (Exception e) {
                Log.e("动态申请权限时，发生异常。", e.toString());
            }
        } else {
            handlePreViewCallBack();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (10 == requestCode) {
            if (grantResults.length == 2
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                handlePreViewCallBack();
            } else {
                Toast.makeText(this, "没有获得必要的权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void handlePreViewCallBack() {
        StartTest.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                StartTesting();
            }
        });
    }

    public void UpdateGUI() {
        tv.setText(data);
        StartTest.setEnabled(true);
    }


    public static native String stringFromJNI(int a);

}
