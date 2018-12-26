package com.clubank;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.WindowManager;

import com.clubank.club11test.R;
import com.clubank.device.BaseActivity;
import com.clubank.home.MainActivity;

public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);
        saveSetting("checkVersion", false);//启动页不进行版本检测，其他页面检测
        CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                String token = settings.getString("Token", "");
                if (!TextUtils.isEmpty(token)) {
//                    new MyAsyncTask(SplashActivity.this, GetUserInfo.class, false).run(token);
                } else {
                    openIntent(MainActivity.class, "");
                    finish();
                }
            }
        };
        countDownTimer.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
