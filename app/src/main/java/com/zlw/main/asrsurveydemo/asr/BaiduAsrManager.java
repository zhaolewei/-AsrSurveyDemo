package com.zlw.main.asrsurveydemo.asr;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import com.baidu.speech.VoiceRecognitionService;
import com.zlw.main.asrsurveydemo.MyApp;
import com.zlw.main.asrsurveydemo.utils.Logger;

public class BaiduAsrManager {
    private static final String TAG = BaiduAsrManager.class.getSimpleName();
    private volatile static BaiduAsrManager ins;

    private SpeechRecognizer speechRecognizer;

    //baidu ASR 回调
    private RecognitionListener recognitionListener = new RecognitionListener() {

        @Override
        public void onReadyForSpeech(Bundle params) {
            // 准备就绪
            Logger.d(TAG, "[baidu] 准备就绪");
        }

        @Override
        public void onBeginningOfSpeech() {
            // 开始说话处理
            Logger.d(TAG, "[baidu] 开始说话处理-----");
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
    };

    private BaiduAsrManager() {
        init();
    }

    public static BaiduAsrManager getInstance() {
        if (ins == null) {
            synchronized (BaiduAsrManager.class) {
                if (ins == null) {
                    ins = new BaiduAsrManager();
                }
            }
        }
        return ins;
    }


    private void init() {
        // 创建识别器
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MyApp.getInstance(), new ComponentName(MyApp.getInstance(), VoiceRecognitionService.class));
        // 注册监听器
        speechRecognizer.setRecognitionListener(recognitionListener);
    }

    public void startASR() {
        //buidu
        Intent intent = new Intent();
        speechRecognizer.startListening(intent);
    }

    public void destory() {
        speechRecognizer.destroy();
        ins = null;
    }
}
