package com.xc.voicechat4j.util;

import com.xc.voicechat4j.config.VoiceChatConfig;
import com.xc.voicechat4j.entity.ChatMessage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;

@Slf4j
public class MessageHandler {



    @SneakyThrows
    public static void sendErrorAndAudio(WebSocketSession session) {
        session.sendMessage(new TextMessage(ChatMessage.error().toJson()));
        session.sendMessage(new BinaryMessage(errorAudio()));
    }



    private static ByteBuffer errorAudio() {
        try {
            // 1. 读取 WAV 文件
            AudioInputStream wavStream = AudioSystem.getAudioInputStream(new File(VoiceChatConfig.audioPath, "error.wav"));
            AudioFormat wavFormat = wavStream.getFormat();

            // 2. 验证输入格式是否为 16kHz 单声道 16-bit
            if (wavFormat.getSampleRate() != 16000 ||
                    wavFormat.getChannels() != 1 ||
                    wavFormat.getSampleSizeInBits() != 16) {
                throw new IllegalArgumentException("输入 WAV 格式不符合 16kHz 单声道 16-bit 要求");
            }

            // 3. 直接提取 PCM 数据（跳过 WAV 头）
            ByteArrayOutputStream pcmBuffer = new ByteArrayOutputStream();
            byte[] tempBuffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = wavStream.read(tempBuffer)) != -1) {
                pcmBuffer.write(tempBuffer, 0, bytesRead);
            }

            // 4. 转换为 ByteBuffer
            ByteBuffer byteBuffer = ByteBuffer.wrap(pcmBuffer.toByteArray());
            wavStream.close();
            return byteBuffer;
        } catch (Exception e) {
            log.error("加载错误响应音频出错");
            throw new RuntimeException(e);
        }
    }

}
