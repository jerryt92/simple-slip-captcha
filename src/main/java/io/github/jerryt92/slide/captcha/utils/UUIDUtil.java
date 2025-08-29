package io.github.jerryt92.slide.captcha.utils;

public final class UUIDUtil {
    public static String randomUUID() {
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
