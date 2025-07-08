package com.xc.voicechat.asr;

import com.alibaba.dashscope.audio.asr.recognition.Recognition;
import com.alibaba.dashscope.audio.asr.recognition.RecognitionParam;
import com.xc.voicechat.config.VoiceChatConfig;
import io.reactivex.disposables.Disposable;
import io.reactivex.processors.PublishProcessor;
import lombok.Setter;
import lombok.SneakyThrows;

import java.nio.ByteBuffer;

public class ASRService implements AutoCloseable {
    private final Recognition recognizer = new Recognition();
    private final RecognitionParam recognitionParam;
    @Setter
    private ASRStreamCallback callback;
    private PublishProcessor<ByteBuffer> audioInputProcessor;
    private Disposable asrStreamDisposable;
    
    public ASRService(VoiceChatConfig config) {
        this.recognitionParam = buildRecognitionParam(config);
    }

    @SneakyThrows
    public void start() {
        if (audioInputProcessor != null) {
            stop();
        }
        
        audioInputProcessor = PublishProcessor.create();
        asrStreamDisposable = recognizer.streamCall(recognitionParam, audioInputProcessor)
                .subscribe(
                        result -> callback.onNext(result),
                        error -> callback.onError(error),
                        callback::onComplete
                );
    }
    
    public void stop() {
        if (audioInputProcessor != null) {
            audioInputProcessor.onComplete();
            audioInputProcessor = null;
        }
        if (asrStreamDisposable != null) {
            asrStreamDisposable.dispose();
            asrStreamDisposable = null;
        }
    }
    
    public void processAudioFrame(byte[] audioData) {
        if (audioInputProcessor == null) {
            throw new IllegalStateException("ASR流未启动");
        }
        
        ByteBuffer buffer = ByteBuffer.allocate(audioData.length);
        buffer.put(audioData);
        buffer.flip();
        audioInputProcessor.onNext(buffer);
    }
    
    @Override
    public void close() {
        stop();
    }
    
    private RecognitionParam buildRecognitionParam(VoiceChatConfig config) {
        return RecognitionParam.builder()
                .model("paraformer-realtime-v2")
                .apiKey(config.getApiKey())
                .format("pcm")
                .sampleRate(16000)
                .parameter("language_hints", new String[]{"zh"})
                .build();
    }
}