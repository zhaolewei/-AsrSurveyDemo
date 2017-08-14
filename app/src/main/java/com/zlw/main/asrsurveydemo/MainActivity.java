package com.zlw.main.asrsurveydemo;

import android.Manifest;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.zlw.main.asrsurveydemo.asr.BaiduAsrManager;
import com.zlw.main.asrsurveydemo.utils.Logger;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    BaiduAsrManager baiduAsrManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "==================>>MainActivity<<==================");
        setContentView(R.layout.activity_main);
        Button btStart = (Button) findViewById(R.id.btStart);

        baiduAsrManager = BaiduAsrManager.getInstance();

        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startASR();
            }
        });
        MainActivityPermissionsDispatcher.startASRWithCheck(this);
    }

    @Override
    protected void onDestroy() {
        baiduAsrManager.destory();
        super.onDestroy();
    }


    // 开始识别
    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    void startASR() {
        baiduAsrManager.startASR();
    }
}
