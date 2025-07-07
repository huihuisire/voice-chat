package com.xc.voicechat4j.client;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
@Slf4j
public class ChatClientConfig {

    private static String prompt = """
             ##基本角色
             你是用户的好朋友. 你的回答将通过逼真的文字转语音技术阅读.
             ##回答规则
             1.对话风格
                像真正的朋友一样自然交流,不使用敬语称呼
                不要总是附和用户
                但是要简洁, 适当使用口语词汇
             2.回答长度与形式
                 保持内容简短,大部分以一句话回复即可
                 避免使用列表或列举表达
                 不要回复太多内容,多用句号引导对话
             3.身份定位
                 要像真实的人一样思考和回复
                 不要暴露自己是"语言模型"或"人工智能"
             4.话题延续
                 每轮对话结束时要延伸相同相关新话题
                 避免问泛泛的问题如"有什么要聊的"
                 不要结束对话
            """;

    @Resource
    private Advisor ragCloudAdvisor;

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        ClassPathResource resource = new ClassPathResource("static/prompt/localization2.txt");
        if (resource.exists()) {
            log.info("使用prompt文件：{}", "localization.txt");
            try (InputStream inputStream = resource.getInputStream()) {
                byte[] bytes = FileCopyUtils.copyToByteArray(inputStream);
                prompt = new String(bytes, StandardCharsets.UTF_8);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }

        return builder.defaultSystem(prompt)
                .defaultAdvisors(new MessageChatMemoryAdvisor(new InMemoryChatMemory()))
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultOptions(DashScopeChatOptions.builder()
                                .withTopP(0.7)
                                .build()
                )
                //设置云知识库
//                .defaultAdvisors(ragCloudAdvisor)
                .build();
    }

}