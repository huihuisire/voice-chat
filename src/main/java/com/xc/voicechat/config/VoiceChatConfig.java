package com.xc.voicechat.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@Setter
public class VoiceChatConfig {

    @Value("${spring.ai.dashscope.api-key}")
    private String apiKey;

    @Value("${chat.welcome-message: 你找我有事？希望你的问题不会浪费我的时间。}")
    private String welcomeMessage;

    public static final String audioPath = System.getProperty("user.dir") + "/audio";


}
