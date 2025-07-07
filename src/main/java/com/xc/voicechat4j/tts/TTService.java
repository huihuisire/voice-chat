package com.xc.voicechat4j.tts;

import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisAudioFormat;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesisParam;
import com.alibaba.dashscope.audio.ttsv2.SpeechSynthesizer;
import com.xc.voicechat4j.config.VoiceChatConfig;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

@Slf4j
public class TTService {

    private final SpeechSynthesisParam synthesisParam;

    private static final String MODEL = "cosyvoice-v2";
    private static final String VOICE = "longwan_v2";

    public TTService(VoiceChatConfig config) {
        this.synthesisParam = buildSpeechSynthesisParam(config);
    }


    @SneakyThrows
    public ByteBuffer streamTTS(String sentence) {
        if (sentence == null || sentence.trim().isEmpty()) {
            log.warn("输入空文本，语音生成跳过");
            return null;
        }
        SpeechSynthesizer synthesizer = new SpeechSynthesizer(synthesisParam, null);
        ByteBuffer audio = synthesizer.call(sentence);
        if (audio == null) {
            log.warn("语音生成返回空字节，sentence: [{}]", sentence);
            return null;
        }
        return audio;
    }

    private SpeechSynthesisParam buildSpeechSynthesisParam(VoiceChatConfig config) {
        return SpeechSynthesisParam.builder()
                .apiKey(config.getApiKey())
                .model(MODEL)
                .voice(VOICE)
                .format(SpeechSynthesisAudioFormat.PCM_16000HZ_MONO_16BIT)
                .build();
    }
}
