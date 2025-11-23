package com.example.phone_shop.services;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    // 1. DÁN KEY CỦA BẠN VÀO ĐÂY
    // Lưu ý: Key phải bắt đầu bằng "AIzaSy..."
    private final String MY_API_KEY = ""; 

    // 2. URL ĐÃ CẬP NHẬT SANG 'gemini-2.5-pro'
    private final String URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-pro:generateContent?key=" + MY_API_KEY;

    private final RestTemplate restTemplate = new RestTemplate();

    public String callGemini(String content) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Tạo JSON body đơn giản nhất để tránh lỗi cú pháp
        // Escape ký tự xuống dòng và ngoặc kép để tránh lỗi JSON
        String cleanContent = content.replace("\"", "'").replace("\n", " ");
        String requestJson = String.format("{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}", cleanContent);

        HttpEntity<String> entity = new HttpEntity<>(requestJson, headers);

        try {
            System.out.println("Đang gửi tin nhắn đến Gemini 2.5 Pro...");
            
            // Gửi request POST
            ResponseEntity<Map> response = restTemplate.postForEntity(URL, entity, Map.class);
            
            Map body = response.getBody();
            
            // Xử lý kết quả trả về
            if (body != null && body.containsKey("candidates")) {
                List<Map> candidates = (List<Map>) body.get("candidates");
                if (!candidates.isEmpty()) {
                    Map contentResponse = (Map) candidates.get(0).get("content");
                    List<Map> parts = (List<Map>) contentResponse.get("parts");
                    return (String) parts.get(0).get("text");
                }
            }
            return "Bot đã nhận tin nhưng không phản hồi.";
        } catch (Exception e) {
            e.printStackTrace(); // Xem lỗi chi tiết ở Console nếu có
            return "Lỗi kết nối Gemini: " + e.getMessage();
        }
    }
}