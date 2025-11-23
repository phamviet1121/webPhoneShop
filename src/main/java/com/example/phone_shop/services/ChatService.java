package com.example.phone_shop.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ChatService {

   
    @Autowired
    private PhoneService phoneService; 

    @Autowired
    private GeminiService geminiService;

    public String processChat(String userQuestion) {
        // A. Lấy toàn bộ danh sách từ hàm bạn đã viết
        List<Map<String, Object>> allPhones = phoneService.getAllPhones_list_phones();

        // B. Tìm từ khóa trong câu hỏi (Ví dụ: "iphone")
        String keyword = extractKeyword(userQuestion);

        // C. Lọc danh sách trên RAM (Rất nhanh)
        List<Map<String, Object>> filteredList = allPhones.stream()
                .filter(p -> {
                    String name = (String) p.get("NamePhoneId");
                    // Nếu không tìm thấy tên hãng thì lấy tất cả (hoặc lấy top 5 sản phẩm đầu)
                    return keyword.isEmpty() || (name != null && name.toLowerCase().contains(keyword));
                })
                .limit(5) // QUAN TRỌNG: Chỉ lấy 5 cái để không bị quá tải
                .collect(Collectors.toList());

        // D. Chuyển dữ liệu thành văn bản
        String dataContext = convertToString(filteredList);

        // E. Tạo câu lệnh cho AI
        String prompt = String.format("""
            Bạn là nhân viên bán điện thoại. Dưới đây là sản phẩm cửa hàng đang có:
            %s
            
            Khách hỏi: "%s"
            
            Yêu cầu:
            1. Trả lời ngắn gọn (dưới 80 từ).
            2. Ưu tiên báo giá 'FinalPrice' (Giá sau giảm).
            3. Nếu có khuyến mãi (TimeDiscountphone), hãy nhắc khách mua nhanh.
            4. Chỉ giới thiệu sản phẩm khớp nhất.
            """, dataContext, userQuestion);

        // F. Gọi AI
        return geminiService.callGemini(prompt);
    }

    // Hàm phụ: Biến đổi Map thành chuỗi
    private String convertToString(List<Map<String, Object>> list) {
        if (list.isEmpty()) return "Không tìm thấy sản phẩm nào phù hợp.";
        
        StringBuilder sb = new StringBuilder();
        NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));

        for (Map<String, Object> p : list) {
            String name = (String) p.get("NamePhoneId");
            // Ép kiểu an toàn
            double price = Double.parseDouble(p.get("FinalPrice").toString());
            String time = (String) p.get("TimeDiscountphone");
            
            sb.append("- ").append(name).append(": ").append(fmt.format(price)).append(" đ");
            if (time != null) sb.append(" (Ưu đãi còn: ").append(time).append(")");
            sb.append("\n");
        }
        return sb.toString();
    }

    // Hàm phụ: Tách từ khóa
    private String extractKeyword(String text) {
        text = text.toLowerCase();
        if (text.contains("iphone")) return "iphone";
        if (text.contains("samsung")) return "samsung";
        if (text.contains("xiaomi")) return "xiaomi";
        if (text.contains("oppo")) return "oppo";
        return ""; 
    }
}