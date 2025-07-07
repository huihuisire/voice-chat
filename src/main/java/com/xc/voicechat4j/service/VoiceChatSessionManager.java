package com.xc.voicechat4j.service;

import com.xc.voicechat4j.config.VoiceChatConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class VoiceChatSessionManager {
    private final Map<String, VoiceChatSession> sessions = new ConcurrentHashMap<>();
    private final VoiceChatConfig config;
    private final ChatClient chatClient;
    
    public VoiceChatSessionManager(VoiceChatConfig config, ChatClient chatClient) {
        this.config = config;
        this.chatClient = chatClient;
    }
    
    public void createSession(WebSocketSession session) {
        try (VoiceChatSession chatSession = sessions.computeIfAbsent(session.getId(),
                id -> new VoiceChatSession(config, chatClient, session, sessions.size()))) {

            log.info("新建对话session，session_id={}", chatSession.getSessionId());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public void destroySession(WebSocketSession session) {
        VoiceChatSession voiceChatSession = sessions.remove(session.getId());
        if (voiceChatSession != null) {
            voiceChatSession.close();
        }
    }
    
    public void processAudioFrame(WebSocketSession session, byte[] audioData) {
        VoiceChatSession voiceChatSession = sessions.get(session.getId());
        if (voiceChatSession != null) {
            voiceChatSession.processAudioFrame(audioData);
        }
    }
    
    public void handleControlMessage(WebSocketSession session, String message) {
        VoiceChatSession voiceChatSession = sessions.get(session.getId());
        if (voiceChatSession != null) {
            if ("start".equals(message)) {
                voiceChatSession.startASR();
            } else if ("stop".equals(message)) {
                voiceChatSession.stopASR();
            }
        }
    }
}