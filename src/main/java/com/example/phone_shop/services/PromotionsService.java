package com.example.phone_shop.services;

import com.example.phone_shop.models.Phone;
import com.example.phone_shop.models.Promotions;
//import com.example.phone_shop.repositories.PhoneRepository;
import com.example.phone_shop.repositories.PromotionsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;


@Service
public class PromotionsService {
    @Autowired
    private PromotionsRepository promotionsRepository;

      @Transactional
    // Bỏ comment dòng dưới để hàm tự chạy mỗi giờ
    // @Scheduled(cron = "0 0 * * * ?")
    public void updatePromotionStatuses() {
        LocalDateTime now = LocalDateTime.now();

        // Lấy TẤT CẢ khuyến mại để kiểm tra
        List<Promotions> allPromotions = promotionsRepository.findAll();
        List<Promotions> promotionsToUpdate = new ArrayList<>();

        for (Promotions promo : allPromotions) {
            
            // >>> ĐIỀU KIỆN QUAN TRỌNG NHẤT <<<
            // Nếu khuyến mại đã ở trạng thái 'inactive', bỏ qua và không kiểm tra nữa.
            if ("inactive".equals(promo.getStatus())) {
                continue;
            }

            String originalStatus = promo.getStatus();

            // Ưu tiên 1: Chuyển thành 'inactive' nếu hết số lượng hoặc hết hạn
            if (promo.getQuantity() <= 0 || (promo.getEndTime() != null && now.isAfter(promo.getEndTime()))) {
                promo.setStatus("inactive");
            }
            // Ưu tiên 2: Chuyển thành 'active' nếu trong thời gian khuyến mại
            else if (now.isAfter(promo.getStartTime()) && (promo.getEndTime() == null || now.isBefore(promo.getEndTime()))) {
                promo.setStatus("active");
            }
            // Ưu tiên 3: Chuyển thành 'upcoming' nếu chưa đến thời gian
            else if (now.isBefore(promo.getStartTime())) {
                promo.setStatus("upcoming");
            }

            // Chỉ thêm vào danh sách cần cập nhật nếu trạng thái thực sự thay đổi
            if (!originalStatus.equals(promo.getStatus())) {
                promotionsToUpdate.add(promo);
            }
        }
        
        // Chỉ lưu vào cơ sở dữ liệu nếu có sự thay đổi, để tối ưu hóa hiệu suất
        if (!promotionsToUpdate.isEmpty()) {
            promotionsRepository.saveAll(promotionsToUpdate);
        }
    }

    // Lấy tất cả khuyến mại
    public List<Promotions> getAllPromotions() {
        return promotionsRepository.findAll();
    }

    // Lấy khuyến mại theo ID
    public Optional<Promotions> getPromotionById(Long id) {
        return promotionsRepository.findById(id);
    }

    // Lưu hoặc cập nhật khuyến mại
    @Transactional
    public Promotions savePromotion(Promotions promotion) {
        return promotionsRepository.save(promotion);
    }
    public Optional<Promotions> findByPhoneId(Long phoneId) {
        return promotionsRepository.findByPhoneId(phoneId);
    }

    // Xóa khuyến mại
    @Transactional
    public void deletePromotion(Long id) {
        promotionsRepository.deleteById(id);
    }

    // Lấy tất cả khuyến mại đang hoạt động
    public List<Promotions> getActivePromotions() {
        return promotionsRepository.findByStatus("active");
    }

    // Lấy khuyến mại theo điện thoại
    // public List<Promotions> getPromotionsByPhoneId(Long phoneId) {
    //     return promotionsRepository.findByPhone_Id(phoneId);
    // }
    public Optional<Promotions> getPromotionsByPhoneId(Long phoneId) {
        return promotionsRepository.findByPhoneId(phoneId);
    }

    // Lấy khuyến mại còn hiệu lực (endTime > hiện tại)
    public List<Promotions> getValidPromotions() {
        return promotionsRepository.findAll()
                .stream()
                .filter(p -> p.getEndTime() == null || p.getEndTime().isAfter(LocalDateTime.now()))
                .toList();
    }
    
    public List<Map<String, Object>> getAllPromotionsWithPhones() {
        List<Promotions> promotions = promotionsRepository.findAll();
        List<Map<String, Object>> promotionList = new ArrayList<>();

        for (Promotions promo : promotions) {
            Phone phone = promo.getPhone();
            if (phone != null) {
                Map<String, Object> orderInfo = new HashMap<>();

                // Thông tin từ bảng Promotions
                orderInfo.put("PromotionId", promo.getId());
                orderInfo.put("DiscountPercent", promo.getDiscountPercent());
                orderInfo.put("PromoQuantity", promo.getQuantity());
                orderInfo.put("StartTime", promo.getStartTime());
                orderInfo.put("EndTime", promo.getEndTime());
                orderInfo.put("RepeatIntervalDays", promo.getRepeatIntervalDays());
                orderInfo.put("PromotionStatus", promo.getStatus());

                // Thông tin từ bảng Phones
                orderInfo.put("PhoneId", phone.getId());
                orderInfo.put("ImageUrlPhoneId", phone.getImageUrl());
                orderInfo.put("NamePhoneId", phone.getName());
                orderInfo.put("PricePhone", phone.getPrice());
                orderInfo.put("StockPhone", phone.getStock());
                orderInfo.put("StatusPhone", phone.getStatus());

                // Tính giá sau giảm
                double finalPrice = phone.getPrice() -
                        (promo.getDiscountPercent().doubleValue() / 100) * phone.getPrice();
                orderInfo.put("FinalPrice", finalPrice);

                promotionList.add(orderInfo);
            }
        }
        return promotionList;
    }


}
