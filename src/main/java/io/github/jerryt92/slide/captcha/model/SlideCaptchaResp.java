package io.github.jerryt92.slide.captcha.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * 滑动验证码
 */
public class SlideCaptchaResp {

    private @Nullable String hash;

    private @Nullable String puzzleUrl;

    private @Nullable Integer width;

    private @Nullable Integer height;

    private @Nullable String sliderUrl;

    private @Nullable Integer sliderSize;

    private @Nullable Float sliderY;

    public SlideCaptchaResp hash(String hash) {
        this.hash = hash;
        return this;
    }

    /**
     * 验证码签名
     *
     * @return hash
     */
    @JsonProperty("hash")
    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public SlideCaptchaResp puzzleUrl(String puzzleUrl) {
        this.puzzleUrl = puzzleUrl;
        return this;
    }

    /**
     * 凹槽图片url
     *
     * @return puzzleUrl
     */
    @JsonProperty("puzzleUrl")
    public String getPuzzleUrl() {
        return puzzleUrl;
    }

    public void setPuzzleUrl(String puzzleUrl) {
        this.puzzleUrl = puzzleUrl;
    }

    public SlideCaptchaResp width(Integer width) {
        this.width = width;
        return this;
    }

    /**
     * 凹槽图片宽度
     *
     * @return width
     */
    @JsonProperty("width")
    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public SlideCaptchaResp height(Integer height) {
        this.height = height;
        return this;
    }

    /**
     * 凹槽图片高度
     *
     * @return height
     */
    @JsonProperty("height")
    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public SlideCaptchaResp sliderUrl(String sliderUrl) {
        this.sliderUrl = sliderUrl;
        return this;
    }

    /**
     * 滑块图片url
     *
     * @return sliderUrl
     */
    @JsonProperty("sliderUrl")
    public String getSliderUrl() {
        return sliderUrl;
    }

    public void setSliderUrl(String sliderUrl) {
        this.sliderUrl = sliderUrl;
    }

    public SlideCaptchaResp sliderSize(Integer sliderSize) {
        this.sliderSize = sliderSize;
        return this;
    }

    /**
     * 滑块图片宽度
     *
     * @return sliderSize
     */
    @JsonProperty("sliderSize")
    public Integer getSliderSize() {
        return sliderSize;
    }

    public void setSliderSize(Integer sliderSize) {
        this.sliderSize = sliderSize;
    }

    public SlideCaptchaResp sliderY(Float sliderY) {
        this.sliderY = sliderY;
        return this;
    }

    /**
     * 滑块图片高度
     *
     * @return sliderY
     */
    @JsonProperty("sliderY")
    public Float getSliderY() {
        return sliderY;
    }

    public void setSliderY(Float sliderY) {
        this.sliderY = sliderY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SlideCaptchaResp slideCaptchaResp = (SlideCaptchaResp) o;
        return Objects.equals(this.hash, slideCaptchaResp.hash) &&
                Objects.equals(this.puzzleUrl, slideCaptchaResp.puzzleUrl) &&
                Objects.equals(this.width, slideCaptchaResp.width) &&
                Objects.equals(this.height, slideCaptchaResp.height) &&
                Objects.equals(this.sliderUrl, slideCaptchaResp.sliderUrl) &&
                Objects.equals(this.sliderSize, slideCaptchaResp.sliderSize) &&
                Objects.equals(this.sliderY, slideCaptchaResp.sliderY);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash, puzzleUrl, width, height, sliderUrl, sliderSize, sliderY);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class SlideCaptchaResp {\n");
        sb.append("    hash: ").append(toIndentedString(hash)).append("\n");
        sb.append("    puzzleUrl: ").append(toIndentedString(puzzleUrl)).append("\n");
        sb.append("    width: ").append(toIndentedString(width)).append("\n");
        sb.append("    height: ").append(toIndentedString(height)).append("\n");
        sb.append("    sliderUrl: ").append(toIndentedString(sliderUrl)).append("\n");
        sb.append("    sliderSize: ").append(toIndentedString(sliderSize)).append("\n");
        sb.append("    sliderY: ").append(toIndentedString(sliderY)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    /**
     * Convert the given object to string with each line indented by 4 spaces
     * (except the first line).
     */
    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}

