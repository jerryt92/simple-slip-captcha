package io.github.jerryt92.slide.captcha.controller;

import io.github.jerryt92.slide.captcha.dto.ValidateCaptchaDto;
import io.github.jerryt92.slide.captcha.model.SlideCaptchaResp;
import io.github.jerryt92.slide.captcha.service.CaptchaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @PostMapping("/validate")
    public String validateCaptcha(@RequestBody ValidateCaptchaDto validateCaptchaDto) {
        return captchaService.verifySlideCaptchaGetCaptchaCode(validateCaptchaDto.sliderX, validateCaptchaDto.hash, validateCaptchaDto.track);
    }
}
