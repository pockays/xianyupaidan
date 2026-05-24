package com.xianyupaidan.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Slf4j
@Service
public class TaobaoOAuthService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${taobao.oauth.app-key}")
    private String appKey;

    @Value("${taobao.oauth.app-secret}")
    private String appSecret;

    @Value("${taobao.oauth.redirect-uri}")
    private String redirectUri;

    @Value("${taobao.oauth.authorize-url}")
    private String authorizeUrl;

    @Value("${taobao.oauth.token-url}")
    private String tokenUrl;

    public String getAuthorizeUrl(String state) {
        return authorizeUrl + "?response_type=code"
                + "&client_id=" + appKey
                + "&redirect_uri=" + URLEncoder.encode(redirectUri, StandardCharsets.UTF_8)
                + "&state=" + URLEncoder.encode(state, StandardCharsets.UTF_8)
                + "&view=web";
    }

    public Map<String, Object> exchangeCode(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", appKey);
        body.add("client_secret", appSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<String> response = restTemplate.postForEntity(
                tokenUrl, new HttpEntity<>(body, headers), String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalArgumentException("淘宝授权失败，请重试");
        }

        try {
            JsonNode json = objectMapper.readTree(response.getBody());
            if (json.has("error")) {
                throw new IllegalArgumentException("淘宝授权错误: " + json.get("error_description").asText());
            }
            return Map.of(
                    "taobaoUserId", json.get("taobao_user_id").asText(),
                    "taobaoUserNick", json.get("taobao_user_nick").asText(),
                    "accessToken", json.get("access_token").asText()
            );
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) throw (IllegalArgumentException) e;
            throw new IllegalArgumentException("解析淘宝授权响应失败: " + e.getMessage());
        }
    }
}
