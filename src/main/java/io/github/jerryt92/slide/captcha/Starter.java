package io.github.jerryt92.slide.captcha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.util.Collections;

@SpringBootApplication
public class Starter {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Starter.class, args);
        printStartupInfo(context);
    }

    private static void printStartupInfo(ConfigurableApplicationContext context) {
        Environment env = context.getEnvironment();
        String port = env.getProperty("server.port", "8080");
        String contextPath = env.getProperty("server.servlet.context-path", "");
        String host = "localhost";
        String[] profiles = env.getActiveProfiles();
        String profileInfo = profiles.length > 0 ? String.join(",", profiles) : "default";
        System.out.println("\n" + String.join("", Collections.nCopies(60, "=")));
        System.out.println("🎉 Application started successfully!");
        System.out.println("🌐 Application URL: http://" + host + ":" + port + contextPath);
        System.out.println("📁 Profile(s): " + profileInfo);
        System.out.println("⏰ Started at: " + java.time.LocalDateTime.now());
        System.out.println(String.join("", Collections.nCopies(60, "=")));
    }
}