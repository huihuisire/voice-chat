package com.xc.voicechat4j.service;

import com.xc.voicechat4j.entity.ChatMessage;
import com.xc.voicechat4j.tts.TTService;
import com.xc.voicechat4j.util.MessageHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.ByteBuffer;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;

@Slf4j
public class DialogManager {
    private final ChatClient chatClient;
    private final TTService ttService;

    public DialogManager(ChatClient chatClient, TTService ttService) {
        this.chatClient = chatClient;
        this.ttService = ttService;
    }


    public void processCompleteSentence(String text, WebSocketSession session) {
        try {
            // 流式获取LLM回复
            StringBuilder fullResponse = new StringBuilder();
            StringBuilder ttsBuffer = new StringBuilder();
            StringBuilder displayedTextBuffer = new StringBuilder();
            Flux<String> content = chatClient.prompt(text)
                    .advisors(a -> a.param(CHAT_MEMORY_CONVERSATION_ID_KEY, session.getId()))
                    .stream().content();

            content.subscribe(
                    chunk -> {
                        try {
                            fullResponse.append(chunk);
                            // 文本消息推送
                            ChatMessage message = new ChatMessage(fullResponse.toString(), false);
                            session.sendMessage(new TextMessage(message.toJson()));

                            // TTS断句逻辑
                            ttsBuffer.append(chunk);
                            String regex = "(?<=[。！？?!.])(?![。！？?!.])|(?<=[，、；;,])(?=\\s*\\S)";
                            String[] sentences = ttsBuffer.toString().split(regex);
                            for (int i = 0; i < sentences.length - 1; i++) {
                                String sentence = sentences[i].trim();
                                if (!sentence.isEmpty()) {
                                    streamTTSAndSubtitle(sentence, session, displayedTextBuffer);
                                }
                            }
                            // 保留最后不完整的部分
                            String remaining = sentences.length > 0 ? sentences[sentences.length - 1] : "";
                            ttsBuffer.setLength(0);
                            ttsBuffer.append(remaining);
                            // 检查缓冲区是否过长，避免内存问题
                            if (ttsBuffer.length() > 500) {
                                streamTTSAndSubtitle(ttsBuffer.toString(), session, displayedTextBuffer);
                                ttsBuffer.setLength(0);
                            }
                        } catch (IOException e) {
                            log.error("大模型流式回复处理错误", e);
                        }
                    },
                    error -> {
                        log.error("大模型调用发生错误", error);
                        MessageHandler.sendErrorAndAudio(session);
                    },
                    () -> {
                        try {
                            // 文本完成推送
                            String last = ttsBuffer.toString().trim();
                            if (!last.isEmpty()) {
                                streamTTSAndSubtitle(last, session, displayedTextBuffer);
                            }
                            // 最后推送完整字幕和完成标记
                            ChatMessage completeMessage = new ChatMessage(displayedTextBuffer.toString(), true);
                            session.sendMessage(new TextMessage(completeMessage.toJson()));
                        } catch (IOException e) {
                            log.error("发送对话结束消息错误", e);
                        }
                    }
            );
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            MessageHandler.sendErrorAndAudio(session);
        }
    }

    @SneakyThrows
    private void streamTTSAndSubtitle(String sentence, WebSocketSession session, StringBuilder displayedTextBuffer) {
        ByteBuffer audio = ttService.streamTTS(sentence);
        try {
            if (audio != null) {
                session.sendMessage(new BinaryMessage(audio));
                // 音频推送后再推送字幕
                displayedTextBuffer.append(sentence);
                ChatMessage syncMessage = new ChatMessage(displayedTextBuffer.toString(), false);
                session.sendMessage(new TextMessage(syncMessage.toJson()));
            }
        } catch (IOException e) {
            log.error("发送音频字节和流式字幕出错", e);
        }
    }

    public void sendWelcome(String welcomeMessage, WebSocketSession session) {
        if (welcomeMessage != null && !welcomeMessage.trim().isEmpty()) {
            streamTTSAndSubtitle(welcomeMessage, session, new StringBuilder());
        }
    }
}