package com.xc.voicechat.entity;

import lombok.Data;

@Data
public class ChatResponse {

    private String type;


    public String toJson() {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            return "{}";
        }
    }
}
