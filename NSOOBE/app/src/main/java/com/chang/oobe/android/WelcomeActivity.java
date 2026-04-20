package com.chang.oobe.android;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class WelcomeActivity extends AppCompatActivity {

    private static final String TAG = "WelcomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 需要注释development/apps/SdkSetup/src/com.android.sdksetup/DefaultActivity.java中针对模拟器预制为1的两行代码在放开下面
//        if (Settings.Secure.getInt(getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 0) == 1) {
//            // 导航切换设置完成会回到该界面，判断如果开机引导设置完成直接关闭进入桌面
//            Log.e(TAG, "已设置，直接关闭");
//            finish();
//        }
        setContentView(R.layout.activity_welcome);

        findViewById(R.id.btn_next).setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, SystemNavActivity.class);
            startActivity(intent);
        });
    }
}