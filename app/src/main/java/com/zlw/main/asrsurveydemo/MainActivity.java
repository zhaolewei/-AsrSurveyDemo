package com.zlw.main.asrsurveydemo;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.idst.nls.realtime.NlsClient;
import com.alibaba.idst.nls.realtime.NlsListener;
import com.alibaba.idst.nls.realtime.StageListener;
import com.alibaba.idst.nls.realtime.internal.protocol.NlsRequest;
import com.alibaba.idst.nls.realtime.internal.protocol.NlsResponse;
import com.baidu.speech.VoiceRecognitionService;
import com.zlw.main.asrsurveydemo.utils.Logger;

import java.util.HashMap;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity extends AppCompatActivity implements RecognitionListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    //baudi
    SpeechRecognizer speechRecognizer;

    //aliyun
    private NlsClient mNlsClient;
    private NlsRequest mNlsRequest;
    private String id;
    private String secret;
    private String appKey;
    private HashMap<Integer, String> resultMap = new HashMap<Integer, String>();
    private int sentenceId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btStart = (Button) findViewById(R.id.btStart);
        initAliyunAsr();
//        // 创建识别器
//        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this, new ComponentName(this, VoiceRecognitionService.class));
//        // 注册监听器
//        speechRecognizer.setRecognitionListener(this);

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
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        super.onDestroy();
    }

    private void initAliyunAsr() {
        mNlsRequest = new NlsRequest();
        Intent intent = getIntent();
        appKey = "nls-service-en";
        id = "LTAIkPILb1xh02PB";
        secret = "3B9vbFQvNmlEI3CYrv5UQCIc7OAefd ";
        resultMap = new HashMap<Integer, String>();
        mNlsRequest.setAppkey(appKey);    //appkey请从 "快速开始" 帮助页面的appkey列表中获取
        mNlsRequest.setResponseMode("streaming");//流式为streaming,非流式为normal

        NlsClient.openLog(true);
        NlsClient.configure(getApplicationContext()); //全局配置
        mNlsClient = NlsClient.newInstance(this, mRecognizeListener, mStageListener, mNlsRequest);//实例化NlsClient
    }

    // 开始识别
    @NeedsPermission(Manifest.permission.RECORD_AUDIO)
    void startASR() {
        //buidu
//        Intent intent = new Intent();
//        bindParams(intent);
//        speechRecognizer.startListening(intent);

        //aliyun
        mNlsRequest.authorize(id, secret); //请替换为用户申请到的数加认证Access Key和Access Srcret，见上方文档
        mNlsClient.start();
    }

    void bindParams(Intent intent) {
        // 设置识别参数
    }

    private NlsListener mRecognizeListener = new NlsListener() {
        @Override
        public void onRecognizingResult(int status, NlsResponse result) {
            switch (status) {
                case NlsClient.ErrorCode.SUCCESS:
                    if (result != null) {
                        if (result.getResult() != null) {
                            //获取句子id对应结果。
                            if (sentenceId != result.getSentenceId()) {
                                sentenceId = result.getSentenceId();
                            }
                            resultMap.put(sentenceId, result.getText());
                            Logger.i(TAG, "[aliyun] 结果 :" + result.getResult().getText());
                        }
                    } else {
                        Logger.i(TAG, "[aliyun]  onRecognizResult finish!");
                    }
                    break;
                case NlsClient.ErrorCode.RECOGNIZE_ERROR:
                    Logger.e(TAG, "[aliyun] RECOGNIZE_ERROR :" + status);
                    break;
                case NlsClient.ErrorCode.RECORDING_ERROR:
                    Logger.e(TAG, "[aliyun] RECORDING_ERROR :" + status);
                    break;
                case NlsClient.ErrorCode.NOTHING:
                    Logger.e(TAG, "[aliyun] NOTHING :" + status);
                    break;
            }
        }
    };

    private StageListener mStageListener = new StageListener() {
        @Override
        public void onStartRecognizing(NlsClient recognizer) {
            super.onStartRecognizing(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
            Logger.d(TAG, "[aliyun]  :开始识别");
        }

        @Override
        public void onStopRecognizing(NlsClient recognizer) {
            super.onStopRecognizing(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
            Logger.d(TAG, "[aliyun]  :停止识别");
            mNlsClient.stop();
        }

        @Override
        public void onStartRecording(NlsClient recognizer) {
            Logger.d(TAG, "[aliyun]  :开始录音");
            super.onStartRecording(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void onStopRecording(NlsClient recognizer) {
            Logger.d(TAG, "[aliyun]  :停止录音");
            super.onStopRecording(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
        }

        @Override
        public void onVoiceVolume(int volume) {
            super.onVoiceVolume(volume);
        }
    };

    //RecognitionListener
    @Override
    public void onReadyForSpeech(Bundle params) {
        // 准备就绪
        Logger.d(TAG, "[baidu] 准备就绪");
    }

    @Override
    public void onBeginningOfSpeech() {
        // 开始说话处理
        Logger.d(TAG, "[baidu] 开始说话处理");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        // 音量变化处理
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        // 录音数据传出处理
    }

    @Override
    public void onEndOfSpeech() {
        // 说话结束处理
        Logger.d(TAG, "[baidu] 说话结束");
    }

    @Override
    public void onError(int error) {
        Logger.d(TAG, "[baidu] onError：" + error);
    }

    @Override
    public void onResults(Bundle results) {
        Logger.i(TAG, "[baidu] 结果：：" + results.toString());
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        // 临时结果处理
//        Logger.d(TAG, "临时结果：：" + partialResults.toString());
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        // 处理事件回调
        Logger.d(TAG, "[baidu] onEvent：：" + eventType);
    }
}
