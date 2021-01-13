package com.zlw.main.asrsurveydemo.asr;

import com.alibaba.idst.nls.realtime.NlsClient;
import com.alibaba.idst.nls.realtime.NlsListener;
import com.alibaba.idst.nls.realtime.StageListener;
import com.alibaba.idst.nls.realtime.internal.protocol.NlsRequest;
import com.alibaba.idst.nls.realtime.internal.protocol.NlsResponse;
import com.zlw.main.asrsurveydemo.MyApp;
import com.zlw.main.asrsurveydemo.utils.Logger;

public class AliyunAsrManager {
    private static final String TAG = AliyunAsrManager.class.getSimpleName();
    private volatile static AliyunAsrManager ins;

    private ResultCallback resultCallback;

    private NlsClient mNlsClient;
    private NlsRequest mNlsRequest;
    private String id;
    private String secret;

    private AliyunAsrManager() {
        init();
    }

    public static AliyunAsrManager getInstance() {
        if (ins == null) {
            synchronized (AliyunAsrManager.class) {
                if (ins == null) {
                    ins = new AliyunAsrManager();
                }
            }
        }
        return ins;
    }

    private void init() {
        mNlsRequest = new NlsRequest();

        String appKey = "nls-service-shurufa16khzz"; //参考文档:https://help.aliyun.com/document_detail/53288.html?spm=5176.doc30420.6.557.OyG7wF
        id = "LTAIkPILb1xh02PBb";
        secret = "3B9vbFQvNmlEI3CYrv5UQCIc7OAefdd";

        mNlsRequest.setAppkey(appKey);    //appkey:  https://ak-console.aliyun.com/?spm=5176.doc53288.2.2.JucfOW#/accesskey
        mNlsRequest.setResponseMode("streaming");//流式为streaming,非流式为normal

        //设置热词相关属性
        //mNlsRequest.setVocabularyId("vocab_id");//详情参考热词相关接口

        NlsClient.openLog(true);
        NlsClient.configure(MyApp.getInstance());
        mNlsClient = NlsClient.newInstance(MyApp.getInstance(), mRecognizeListener, mStageListener, mNlsRequest);
    }

    public void startASR() {
        mNlsRequest.authorize(id, secret);
        mNlsClient.start();
    }


    public void stopASR() {
        mNlsClient.stop();
    }

    public void destroy() {
        mNlsClient.stop();
        mNlsClient = null;
        ins = null;
    }

    private NlsListener mRecognizeListener = new NlsListener() {
        @Override
        public void onRecognizingResult(int status, NlsResponse result) {
            switch (status) {
                case NlsClient.ErrorCode.SUCCESS:
                    if (result != null) {
                        if (result.getResult() != null) {
                            Logger.i(TAG, "[aliyun] 结果 :" + result.getResult().getText());
                            if (resultCallback != null) {
                                resultCallback.onResult(result.getResult().getText());
                            }
                        }
                    } else {
                        Logger.d(TAG, "[aliyun] 识别结束");
                    }
                    break;
                case NlsClient.ErrorCode.RECOGNIZE_ERROR:
                    Logger.e(TAG, "[aliyun] 识别错误");
                    break;
                case NlsClient.ErrorCode.RECORDING_ERROR:
                    Logger.e(TAG, "[aliyun] 录音错误");
                    break;
                case NlsClient.ErrorCode.NOTHING:
                    Logger.e(TAG, "[aliyun] 未知错误");
                    break;
            }
        }
    };

    private StageListener mStageListener = new StageListener() {
        @Override
        public void onStartRecognizing(NlsClient recognizer) {
            super.onStartRecognizing(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
            Logger.d(TAG, "[aliyun] 开始识别");
        }

        @Override
        public void onStopRecognizing(NlsClient recognizer) {
            super.onStopRecognizing(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
            if (mNlsClient != null) {
                mNlsClient.stop();
            }
            Logger.d(TAG, "[aliyun] 停止识别");
        }

        @Override
        public void onStartRecording(NlsClient recognizer) {
            super.onStartRecording(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
            Logger.d(TAG, "[aliyun] 开始录音");
        }

        @Override
        public void onStopRecording(NlsClient recognizer) {
            super.onStopRecording(recognizer);    //To change body of overridden methods use File | Settings | File Templates.
            Logger.d(TAG, "[aliyun] 停止录音");
        }

        @Override
        public void onVoiceVolume(int volume) {
            super.onVoiceVolume(volume);
        }
    };

    public void setResultCallback(ResultCallback resultCallback) {
        this.resultCallback = resultCallback;
    }
}
