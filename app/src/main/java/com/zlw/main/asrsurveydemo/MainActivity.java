package com.zlw.main.asrsurveydemo;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.zlw.main.asrsurveydemo.asr.AliyunAsrManager;
import com.zlw.main.asrsurveydemo.asr.BaiduAsrManager;
import com.zlw.main.asrsurveydemo.asr.ResultCallback;
import com.zlw.main.asrsurveydemo.utils.Logger;

import butterknife.BindView;
import butterknife.ButterKnife;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.rgASRServer)
    RadioGroup rgASRServer;
    @BindView(R.id.btStart)
    Button btStart;
    @BindView(R.id.tvResult)
    TextView tvResult;

    private StringBuffer resultBuff;

    private boolean baiduEnable = true;
    private boolean aliyunEnable = false;

    private BaiduAsrManager baiduAsrManager;
    private AliyunAsrManager aliyunAsrManager;

    private boolean isAsr = false; // 是否正在识别

    private ResultCallback resultCallback = new ResultCallback() {
        @Override
        public void onResult(String result) {
            resultBuff.append(result);
            resultBuff.append("\n");
            refreshLogView(resultBuff.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.d(TAG, "==================>>MainActivity<<==================");
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        tvResult.setMovementMethod(ScrollingMovementMethod.getInstance());
        resultBuff = new StringBuffer();
        initASR();
        initEvent();
        MainActivityPermissionsDispatcher.startASRWithCheck(this);
    }

    private void initEvent() {
        btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAsr) {
                    startASR();
                    btStart.setText("关闭");
                } else {
                    stopASR();
                    btStart.setText("开始");
                }
                isAsr = !isAsr;
            }
        });

        rgASRServer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                switch (checkedId) {
                    case R.id.rbBaiduASR:
                        baiduEnable = true;
                        aliyunEnable = false;
                        resetASR();
                        break;
                    case R.id.rbAliyunASR:
                        baiduEnable = false;
                        aliyunEnable = true;
                        resetASR();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        destoryASR();
        super.onDestroy();
    }

    private void destoryASR() {
        if (baiduAsrManager != null) {
            baiduAsrManager.destroy();
            baiduAsrManager = null;
        }
        if (aliyunAsrManager != null) {
            aliyunAsrManager.destroy();
            aliyunAsrManager = null;
        }
    }

    private void initASR() {
        if (baiduEnable) {
            Logger.d(TAG, "初始化：Baidu");
            baiduAsrManager = BaiduAsrManager.getInstance();
            baiduAsrManager.setResultCallback(resultCallback);
        }
        if (aliyunEnable) {
            Logger.d(TAG, "初始化：Aliyun");
            aliyunAsrManager = AliyunAsrManager.getInstance();
            aliyunAsrManager.setResultCallback(resultCallback);
        }
    }

    private void resetASR() {
        destoryASR();
        initASR();
        resultBuff.setLength(0);
        tvResult.setText(resultBuff.toString());

        btStart.setText("开始");
        isAsr = false;
    }

    // 开始识别
    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    void startASR() {
        if (baiduAsrManager != null) {
            baiduAsrManager.startASR();
        }
        if (aliyunAsrManager != null) {
            aliyunAsrManager.startASR();
        }
    }

    void stopASR() {
        if (baiduAsrManager != null) {
            baiduAsrManager.stopASR();
        }
        if (aliyunAsrManager != null) {
            aliyunAsrManager.stopASR();
        }
    }

    void refreshLogView(String msg) {
        tvResult.setText(msg);

        //实现TextView 自动滚动至底部
        int offset = tvResult.getLineCount() * tvResult.getLineHeight();
        if (offset > tvResult.getHeight()) {
            tvResult.scrollTo(0, offset - tvResult.getHeight());
        }
    }
}
