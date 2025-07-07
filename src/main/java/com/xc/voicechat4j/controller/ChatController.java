package com.xc.voicechat4j.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

  private final ChatClient dashScopeChatClient;

    @GetMapping("/simple")
  public String simpleChat(String query) {
    return dashScopeChatClient.prompt(query).call().content();
  }
 }