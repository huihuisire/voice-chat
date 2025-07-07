package com.xc.voicechat4j.service;

import com.alibaba.dashscope.audio.asr.recognition.RecognitionResult;
import com.xc.voicechat4j.asr.ASRService;
import com.xc.voicechat4j.asr.ASRStreamCallback;
import com.xc.voicechat4j.config.VoiceChatConfig;
import com.xc.voicechat4j.entity.ASRMessage;
import com.xc.voicechat4j.tts.TTService;
import com.xc.voicechat4j.util.MessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class VoiceChatSession implements AutoCloseable {
    private final ASRService asrService;
    private final DialogManager dialogManager;
    private final WebSocketSession session;
    private final AtomicReference<String> currentText = new AtomicReference<>("");
    private final String unicode;


    public VoiceChatSession(VoiceChatConfig config,
                            ChatClient chatClient,
                            WebSocketSession session,
                            int sessionCount) {
        this.session = session;
        this.asrService = new ASRService(config);
        this.dialogManager = new DialogManager(chatClient, new TTService(config));
        dialogManager.sendWelcome(config.getWelcomeMessage(), session);

        unicode = "标准对话-编号-" + ++sessionCount;
        initASRCallback();
    }

    private void initASRCallback() {
        asrService.setCallback(new ASRStreamCallback() {
            @Override
            public void onNext(RecognitionResult result) throws IOException {
                boolean isEnd = result.isSentenceEnd();
                currentText.set(result.getSentence().getText());

                session.sendMessage(new TextMessage(new ASRMessage(currentText.get(), isEnd).toJson()));

                if (isEnd) {
                    dialogManager.processCompleteSentence(currentText.get(), session);
                }
            }

            @Override
            public void onError(Throwable error) {
                log.error("ASR错误: {}", error.getMessage());
                MessageHandler.sendErrorAndAudio(session);
            }

            @Override
            public void onComplete() {
                log.info("ASR流已完成");
            }
        });
    }

    public void startASR() {
        asrService.start();
    }

    public void stopASR() {
        asrService.stop();
    }

    public void processAudioFrame(byte[] audioData) {
        asrService.processAudioFrame(audioData);
    }

    public String getSessionId() {
        return unicode;
    }

    @Override
    public void close() {
        asrService.close();
    }
}