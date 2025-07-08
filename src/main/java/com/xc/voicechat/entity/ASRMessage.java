package com.xc.voicechat.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ASRMessage extends ChatResponse{

    private String text;

    private Boolean isEnd;


    public ASRMessage(String text, Boolean isEnd) {
        this.setType("asr");
        this.text = text;
        this.isEnd = isEnd;
    }
}
