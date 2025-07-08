package com.xc.voicechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.ConfigurableEnvironment;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@SpringBootApplication
public class VoiceChatApplication {

    public static void main(String[] args) {
        ConfigurableEnvironment env = SpringApplication.run(VoiceChatApplication.class, args).getEnvironment();

        String serverPort = env.getProperty("server.port");
        String web = String.format("Access Url :http://localhost:%s", serverPort);
        colorPrint(web);
    }


    private static void colorPrint(String text) {
        System.out.printf("\u001B[32m%s\u001B[0m%n", text);
    }



    @EventListener(ApplicationReadyEvent.class)
    public void printLocalIpAddress() {
        try {
            System.out.println("======= 局域网IP地址 =======");
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                // 过滤掉回环接口、虚拟接口和未启用的接口
                if (networkInterface.isLoopback() || networkInterface.isVirtual() || !networkInterface.isUp()) {
                    continue;
                }

                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    // 只打印IPv4地址
                    if (address.getHostAddress().contains(":")) {
                        continue;
                    }
                    System.out.println("手机访问: http://"+address.getHostAddress()+":9533");
                }
            }
            System.out.println("=========================");
        } catch (Exception e) {
            System.err.println("获取IP地址时出错: " + e.getMessage());
        }
    }
}
