package com.xc.voicechat4j.websocket;

import com.xc.voicechat4j.config.VoiceChatConfig;
import com.xc.voicechat4j.service.VoiceChatSessionManager;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Slf4j
@Component
public class VoiceChatWebSocketHandler extends TextWebSocketHandler implements InitializingBean {
    
    private VoiceChatSessionManager sessionManager;

    @Resource
    private VoiceChatConfig voiceChatConfig;

    @Resource
    private ChatClient chatClient;
    

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        sessionManager.createSession(session);
    }

    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) {
        sessionManager.processAudioFrame(session, message.getPayload().array());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        sessionManager.handleControlMessage(session, message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        sessionManager.destroySession(session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        log.error("WebSocket transport error", exception);
        sessionManager.destroySession(session);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    @Override
    public void afterPropertiesSet() {
        sessionManager = new VoiceChatSessionManager(voiceChatConfig, chatClient);
    }
}