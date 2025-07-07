# Voice Chat 4J 🎙️

一个基于Spring Boot的实时语音对话系统，集成了阿里云语音识别(ASR)、通义千问大语言模型和语音合成(TTS)服务。

[演示视频](https://github.com/user-attachments/assets/4b6a6360-b420-449b-87a2-27a4a87dc9f7)

## ✨ 特性

- 🎯 实时语音识别 (ASR)
- 💬 智能对话 (基于通义千问)
- 🔊 自然语音合成 (TTS)
- 🔄 WebSocket实时通信
- 🛡️ 多客户端并发支持
- 🔍 智能错误恢复机制

## 🚀 快速开始

### 环境要求

- JDK 17+
- IntelliJ IDEA 2023+
- Spring Boot 3.2+

### 配置
- 在阿里云百炼平台设置api-key
在 `application.yml` 中配置以下参数：

```yaml
spring:
  ai:
    dashscope:
      api-key: your-api-key-here

server:
  port: 9533
```

### 运行项目

1. 使用IDEA打开项目
2. 等待Maven下载依赖
3. 运行 `VoiceChatApplication` 主类
4. 访问 `http://localhost:9533` 即可使用语音对话系统

## 🎮 使用方式

系统提供了完整的Web界面，支持以下功能：

- 实时语音识别
- 智能对话
- 语音合成播放
- 对话历史记录

## 📱手机使用

- 确保手机与后端服务在同一局域网（比如连同一个WiFi）
- 修改index.html中后端url为局域网地址


## ！注意
- 页面目前使用HTTP方式，只有localhost访问时才能获取麦克风权限
- 使用IP地址访问时，要么部署https方式，要么跳过安全控制（浏览器自行搜索http方式获取麦克风权限）

## 🏗️ 系统架构

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  客户端     │     │  WebSocket  │     │  服务端     │
│  (浏览器)   │◄────┤   服务器    │◄────┤  (Spring)   │
└─────────────┘     └─────────────┘     └─────────────┘
                                                    │
                                                    ▼
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  语音生成   │◄────┤  通义千问   │◄────┤ 语音识别    │
│  (TTS)      │     │  (LLM)      │     │  (ASR)      │
└─────────────┘     └─────────────┘     └─────────────┘
```

## RAG
- 项目中能快速使用阿里云知识库，快速实现RAG

## 🔧 技术栈

- **后端框架**: Spring Boot 3.2
- **WebSocket**: Spring WebSocket
- **语音识别**: 阿里云ASR
- **大语言模型**: 通义千问
- **语音合成**: 阿里云TTS
- **构建工具**: Maven
- **日志框架**: SLF4J + Logback

## 📦 项目结构

```
voice-chat4j/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/xc/voicechat4j/
│   │   │       ├── asr/          # 语音识别服务
│   │   │       ├── tts/          # 语音合成服务
│   │   │       └── websocket/    # WebSocket处理
│   │   └── resources/
│   │       ├── static/          # 静态资源（Web界面）
│   │       └── application.yml  # 配置文件
│   └── test/                    # 测试代码
├── pom.xml                      # 项目依赖
└── README.md                    # 项目文档
```

## 🔍 错误处理

系统实现了智能的错误恢复机制：

- ASR服务自动重连
- 资源自动清理
- 异常状态恢复
- 详细的错误日志

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📝 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情

## 👥 作者

- **雪光军** - *跑得比马快的牛* - [gitee](https://gitee.com/huihuisire)

## 🙏 致谢

- 阿里云语音服务
- 通义千问大语言模型
- Spring Boot 团队
- 所有贡献者 
