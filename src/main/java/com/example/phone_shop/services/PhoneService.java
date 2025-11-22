package com.example.phone_shop.services;

//import com.example.phone_shop.models.OrdersPhones;
import com.example.phone_shop.models.Phone;
import com.example.phone_shop.models.Promotions;
import com.example.phone_shop.repositories.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

@Service
public class PhoneService {
    @Autowired
    private PhoneRepository phoneRepository;
    @Autowired
    private PromotionsService promotionsService;

    // Lấy tất cả điện thoại
    public List<Phone> getAllPhones() {
        return phoneRepository.findAll();
    }

    // Lấy điện thoại theo ID
    public Optional<Phone> getPhoneById(Long id) {
        return phoneRepository.findById(id);
    }

    // update status dữ liệu điện thoại 
    public void updateStatus()
    {
        List<Phone> AllPhones=phoneRepository.findAll();
        for (Phone phone : AllPhones) {
            if (phone.getStock() == 0 && !"inactive".equals(phone.getStatus())) {
                phone.setStatus("inactive");
                phoneRepository.save(phone);
            }
        }
    }

    // Lưu điện thoại mới
    public Phone savePhone(Phone phone) {
        return phoneRepository.save(phone);
    }
    // cập nhật 
    @Transactional
    public Phone updatePhone(Phone phone) {
        if (!phoneRepository.existsById(phone.getId())) {
            throw new IllegalArgumentException("Không tìm thấy điện thoại với ID " + phone.getId());
        }
        return phoneRepository.save(phone); // Cập nhật luôn bằng save()
    }
      
     // Xóa điện thoại theo ID
    @Transactional
    public void deletePhone(Long id) {
        if (phoneRepository.existsById(id)) {
            phoneRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Điện thoại với ID " + id + " không tồn tại!");
        }
    }
    public List<Phone> searchByNameOrBrand(String keyword) {
        return phoneRepository.findByNameContainingOrBrandContaining(keyword, keyword);
    }

    // public List<Map<String, Object>> getAllPhones_discount() {
    // List<Phone> phones = phoneRepository.findAll();
    // List<Map<String, Object>> discountPhones = new ArrayList<>();

    //     for (Phone phone : phones) {
    //          if (phone != null && phone.getDiscount() != null && phone.getDiscount().compareTo(BigDecimal.ZERO) > 0) {
    //             Map<String, Object> orderInfo = new HashMap<>();
    //             orderInfo.put("PhoneId", phone.getId());
    //             orderInfo.put("ImageUrlPhoneId", phone.getImageUrl());
    //             orderInfo.put("NamePhoneId", phone.getName());
    //             orderInfo.put("PricePhone", phone.getPrice());

    //             // Tính giá sau khi giảm
    //             double finalPrice = phone.getPrice() - (phone.getDiscount().doubleValue() / 100) * phone.getPrice();
    //             orderInfo.put("FinalPrice", finalPrice);

    //             orderInfo.put("StockPhone", phone.getStock());
    //             orderInfo.put("DiscountPhone", phone.getDiscount());
    //             orderInfo.put("StatusPhone", phone.getStatus());

    //             discountPhones.add(orderInfo);
    //         }
    //     }
    //     return discountPhones;
    // }
    
    // Lấy điện thoại theo ID
    public Optional<Phone> getPhoneById_phone(Long id) {
        return phoneRepository.findById(id);
    }

    public List<Map<String, Object>> getAllPhones_list_phones() {
        List<Phone> phones = phoneRepository.findAll();
        List<Map<String, Object>> discountPhones = new ArrayList<>();

        for (Phone phone : phones) {
            if (phone == null) {
                continue;
            }

            // Tìm khuyến mãi theo phoneId
            Optional<Promotions> promotionOpt = promotionsService.getPromotionsByPhoneId(phone.getId());

            Map<String, Object> phoneInfo = new HashMap<>();

            // 1. Thông tin cơ bản của điện thoại
            phoneInfo.put("PhoneId", phone.getId());
            phoneInfo.put("ImageUrlPhoneId", phone.getImageUrl());
            phoneInfo.put("NamePhoneId", phone.getName());
            phoneInfo.put("BrandPhoneId", phone.getBrand());
            phoneInfo.put("PricePhone", phone.getPrice());
            phoneInfo.put("StockPhone", phone.getStock());
            phoneInfo.put("StatusPhone", phone.getStatus());

            // 2. Xử lý logic khuyến mãi
            if (promotionOpt.isPresent()) {
                Promotions promotion = promotionOpt.get();

                if (!"inactive".equalsIgnoreCase(promotion.getStatus())) {
                    // Có khuyến mãi hoạt động
                    phoneInfo.put("DiscountPhone", promotion.getDiscountPercent());
                    phoneInfo.put("QuantityPhone", promotion.getQuantity());
                    phoneInfo.put("StatusDiscountPhone", promotion.getStatus());

                    // Tính giá sau giảm
                    if (!"upcoming".equalsIgnoreCase(promotion.getStatus())) {
                        double finalPrice = phone.getPrice()
                                - (promotion.getDiscountPercent().doubleValue() / 100) * phone.getPrice();
                        phoneInfo.put("FinalPrice", finalPrice);
                    }else{
                        phoneInfo.put("FinalPrice", phone.getPrice());
                    }

                    // Thời gian còn lại
                    if (promotion.getEndTime() != null) {
                        LocalDateTime now = LocalDateTime.now();
                        if (promotion.getEndTime().isAfter(now)) {
                            Duration duration = Duration.between(now, promotion.getEndTime());
                            long days = duration.toDays();
                            long hours = duration.toHours() % 24;
                            long minutes = duration.toMinutes() % 60;
                            String remainingTime = String.format("%d ngày %d giờ %d phút", days, hours, minutes);
                            phoneInfo.put("TimeDiscountphone", remainingTime);
                        } else {
                            phoneInfo.put("TimeDiscountphone", "Đã hết hạn");
                        }
                    } else {
                        phoneInfo.put("TimeDiscountphone", null);
                    }
                } else {
                    // Khuyến mãi tồn tại nhưng inactive
                    phoneInfo.put("DiscountPhone", null);
                    phoneInfo.put("QuantityPhone", null);
                    phoneInfo.put("StatusDiscountPhone", promotion.getStatus());
                    phoneInfo.put("FinalPrice", phone.getPrice());
                    phoneInfo.put("TimeDiscountphone", null);
                }
            } else {
                // Không có khuyến mãi
                phoneInfo.put("DiscountPhone", null);
                phoneInfo.put("QuantityPhone", null);
                phoneInfo.put("StatusDiscountPhone", null);
                phoneInfo.put("FinalPrice", phone.getPrice());
                phoneInfo.put("TimeDiscountphone", null);
            }

            discountPhones.add(phoneInfo);
        }
        return discountPhones;
    }


    public Map<String, Object> getPhoneWithPromotionById(Long id) {
        Optional<Phone> phoneOpt = phoneRepository.findById(id);
        if (phoneOpt.isEmpty()) {
            return null; // hoặc throw exception tùy nhu cầu
        }

        Phone phone = phoneOpt.get();

        // Tìm khuyến mãi theo phoneId
        Optional<Promotions> promotionOpt = promotionsService.getPromotionsByPhoneId(phone.getId());

        Map<String, Object> phoneInfo = new HashMap<>();

        // 1. Thông tin cơ bản của điện thoại
        phoneInfo.put("PhoneId", phone.getId());
        phoneInfo.put("ImageUrlPhoneId", phone.getImageUrl());
        phoneInfo.put("NamePhoneId", phone.getName());
        phoneInfo.put("BrandPhoneId", phone.getBrand());
        phoneInfo.put("PricePhone", phone.getPrice());
        phoneInfo.put("StockPhone", phone.getStock());
        phoneInfo.put("StatusPhone", phone.getStatus());
        phoneInfo.put("DescriptionPhone", phone.getDescription());


        // 2. Xử lý logic khuyến mãi
        if (promotionOpt.isPresent()) {
            Promotions promotion = promotionOpt.get();

            if (!"inactive".equalsIgnoreCase(promotion.getStatus())) {
                // Có khuyến mãi hoạt động
                phoneInfo.put("DiscountPhone", promotion.getDiscountPercent());
                phoneInfo.put("QuantityPhone", promotion.getQuantity());
                phoneInfo.put("StatusDiscountPhone", promotion.getStatus());

                // Tính giá sau giảm
                if (!"upcoming".equalsIgnoreCase(promotion.getStatus())) {
                    double finalPrice = phone.getPrice()
                            - (promotion.getDiscountPercent().doubleValue() / 100) * phone.getPrice();
                    phoneInfo.put("FinalPrice", finalPrice);
                } else {
                    phoneInfo.put("FinalPrice", phone.getPrice());
                }

                // Thời gian còn lại
                if (promotion.getEndTime() != null) {
                    LocalDateTime now = LocalDateTime.now();
                    if (promotion.getEndTime().isAfter(now)) {
                        Duration duration = Duration.between(now, promotion.getEndTime());
                        long days = duration.toDays();
                        long hours = duration.toHours() % 24;
                        long minutes = duration.toMinutes() % 60;
                        String remainingTime = String.format("%d ngày %d giờ %d phút", days, hours, minutes);
                        phoneInfo.put("TimeDiscountphone", remainingTime);
                    } else {
                        phoneInfo.put("TimeDiscountphone", "Đã hết hạn");
                    }
                } else {
                    phoneInfo.put("TimeDiscountphone", null);
                }
            } else {
                // Khuyến mãi tồn tại nhưng inactive
                phoneInfo.put("DiscountPhone", null);
                phoneInfo.put("QuantityPhone", null);
                phoneInfo.put("StatusDiscountPhone", promotion.getStatus());
                phoneInfo.put("FinalPrice", phone.getPrice());
                phoneInfo.put("TimeDiscountphone", null);
            }
        } else {
            // Không có khuyến mãi
            phoneInfo.put("DiscountPhone", null);
            phoneInfo.put("QuantityPhone", null);
            phoneInfo.put("StatusDiscountPhone", null);
            phoneInfo.put("FinalPrice", phone.getPrice());
            phoneInfo.put("TimeDiscountphone", null);
        }

        return phoneInfo;
    }

    public List<Map<String, Object>> searchAndGetPhonesWithPromotions(String keyword) {
        // 1️⃣ Lấy danh sách điện thoại theo từ khóa
        List<Phone> matchedPhones = phoneRepository.findByNameContainingOrBrandContaining(keyword, keyword);

        // 2️⃣ Lấy danh sách toàn bộ điện thoại (đã có logic khuyến mãi)
        List<Map<String, Object>> allPhones = getAllPhones_list_phones();

        // 3️⃣ Lọc danh sách allPhones dựa trên các ID tìm thấy
        Set<Long> matchedIds = matchedPhones.stream()
                .filter(Objects::nonNull)
                .map(Phone::getId)
                .collect(Collectors.toSet());

        // 4️⃣ Lọc các điện thoại khớp ID
        List<Map<String, Object>> filteredPhones = allPhones.stream()
                .filter(map -> {
                    Object idObj = map.get("PhoneId");
                    if (idObj instanceof Long) {
                        return matchedIds.contains((Long) idObj);
                    } else if (idObj instanceof Integer) {
                        return matchedIds.contains(((Integer) idObj).longValue());
                    }
                    return false;
                })
                .collect(Collectors.toList());

        return filteredPhones;
    }


}
