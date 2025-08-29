package io.github.jerryt92.slide.captcha.controller;

import io.github.jerryt92.slide.captcha.model.SlideCaptchaResp;
import io.github.jerryt92.slide.captcha.service.CaptchaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CaptchaController {
    private final CaptchaService captchaService;

    public CaptchaController(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    @GetMapping("/slide")
    public SlideCaptchaResp getCaptcha() {
        return captchaService.genSlideCaptcha();
    }

    @GetMapping("/validate")
    public String validateCaptcha(@RequestParam("slider-x") Float sliderX, @RequestParam("hash") String hash) {
        return captchaService.verifySlideCaptchaGetCaptchaCode(sliderX, hash);
    }
}
