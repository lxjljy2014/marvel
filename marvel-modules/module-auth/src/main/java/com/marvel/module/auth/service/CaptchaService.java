package com.marvel.module.auth.service;

import com.marvel.common.constant.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 数学运算验证码：生成 SVG 图片，答案存 Redis，2 分钟有效。无第三方依赖。
 */
@Service
@RequiredArgsConstructor
public class CaptchaService {

    private final RedisTemplate<String, Object> redisTemplate;

    public Map<String, String> generateCaptcha() {
        int a = ThreadLocalRandom.current().nextInt(1, 20);
        int b = ThreadLocalRandom.current().nextInt(1, 20);
        boolean plus = ThreadLocalRandom.current().nextBoolean();
        int result = plus ? a + b : Math.max(a, b) - Math.min(a, b);
        int x = plus ? Math.min(a, b) : Math.max(a, b);
        int y = plus ? Math.max(a, b) : Math.min(a, b);
        String expression = x + " " + (plus ? "+" : "-") + " " + y + " = ?";

        String uuid = UUID.randomUUID().toString().replace("-", "");
        redisTemplate.opsForValue().set(Constants.CAPTCHA_KEY_PREFIX + uuid, String.valueOf(result),
                Duration.ofMinutes(2));

        String svg = buildSvg(expression);
        return Map.of("uuid", uuid, "img", svg);
    }

    public boolean verify(String uuid, String code) {
        if (!StringUtils.hasText(uuid) || !StringUtils.hasText(code)) {
            return false;
        }
        String key = Constants.CAPTCHA_KEY_PREFIX + uuid;
        Object answer = redisTemplate.opsForValue().get(key);
        redisTemplate.delete(key);
        return answer != null && answer.toString().equals(code.trim());
    }

    private String buildSvg(String expression) {
        SecureRandom random = new SecureRandom();
        int rotation = random.nextInt(9) - 4;
        return """
                <svg xmlns="http://www.w3.org/2000/svg" width="120" height="40" viewBox="0 0 120 40">
                  <rect width="120" height="40" fill="#f0f2f5" rx="4"/>
                  <line x1="10" y1="%d" x2="110" y2="%d" stroke="#d0d4da" stroke-width="1"/>
                  <line x1="15" y1="%d" x2="105" y2="%d" stroke="#d0d4da" stroke-width="1"/>
                  <text x="60" y="26" font-size="18" font-family="monospace" fill="#333" text-anchor="middle"
                        transform="rotate(%d 60 20)" font-weight="bold">%s</text>
                </svg>
                """.formatted(
                random.nextInt(5, 35), random.nextInt(5, 35),
                random.nextInt(5, 35), random.nextInt(5, 35),
                rotation, expression).replace("\n", "").replaceAll("\\s+", " ");
    }
}
