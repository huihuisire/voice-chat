package com.xc.voicechat.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage extends ChatResponse {

    private String text;
    private Boolean isComplete;
    private String type = "chat";

    public ChatMessage(String text) {
        this(text, true);
    }

    public ChatMessage(String text, Boolean isComplete) {
        this.text = text;
        this.isComplete = isComplete;
    }

    public static ChatMessage error() {
        return new ChatMessage("不好意思，我临时有事，待会再聊吧。");
    }


}
