package com.xc.voicechat.asr;

import com.alibaba.dashscope.audio.asr.recognition.RecognitionResult;

import java.io.IOException;

/**
 * ASR流回调接口
 */
public interface ASRStreamCallback {
    /**
     * 处理识别结果
     */
    void onNext(RecognitionResult result) throws IOException;
    

    /**
     * 处理错误
     */
    void onError(Throwable error);
    
    /**
     * 处理完成事件
     */
    void onComplete();
}