package io.github.jerryt92.slide.captcha.dto;

public class Track {
    /**
     * 光标在屏幕上的x坐标
     */
    private float pointerX;
    /**
     * 光标在屏幕上的y坐标
     */
    private float pointerY;
    /**
     * 时间戳，单位毫秒
     */
    private long t;

    public float getPointerX() {
        return pointerX;
    }

    public float getPointerY() {
        return pointerY;
    }

    public long getT() {
        return t;
    }
}
