package com.example.phone_shop.services;

import com.example.phone_shop.models.OrdersPhones;
import com.example.phone_shop.models.Phone;
import com.example.phone_shop.models.Promotions;
import com.example.phone_shop.models.User;
import com.example.phone_shop.repositories.OrdersPhonesRepository;

import jakarta.persistence.Column;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrdersPhonesService {
    private final OrdersPhonesRepository repository;
    private final PhoneService phonesService;
    private final UserService userService;
    private final PromotionsService promotionsService;

    public OrdersPhonesService(OrdersPhonesRepository repository,PhoneService phonesService,UserService userService, PromotionsService promotionsService) {
        this.repository = repository;
        this.phonesService = phonesService;
        this.userService = userService;
        this.promotionsService=promotionsService;
    }

    public List<OrdersPhones> getAllOrdersPhones() {
        return repository.findAll();
    }
    public OrdersPhones saveOrder(OrdersPhones order) {
        return repository.save(order);
    }
    public List<OrdersPhones> getOrdersByUserId(int userId) {
        return repository.findByUserId(userId);
    }
    public Optional<OrdersPhones> getOrderById(int id) {
        return repository.findById(id);
    }
    public void deleteOrder(int id) {
        repository.deleteById(id);
    }
    @Transactional // Đảm bảo toàn vẹn dữ liệu
    public void hideOrderForUser(int orderId) {
        // 1. Tìm đơn hàng và xác thực nó thuộc về đúng người dùng
        OrdersPhones order = repository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng hoặc bạn không có quyền thực hiện thao tác này."));

        // 2. Thay đổi trạng thái hiển thị
        order.setIsVisibleToUser(false);

        // 3. Lưu lại thay đổi
        repository.save(order);
    }
    public void Refundquantity(int id) {
        Optional<OrdersPhones> orderOptional = repository.findById(id);

        if (orderOptional.isPresent()) {
            OrdersPhones order = orderOptional.get();
            long idphone = order.getPhoneId();
            int quantity = order.getQuantity();
            int usedDiscount = order.getUsedDiscount();

            LocalDateTime createdAtOrder = order.getCreatedAt();
            LocalDateTime updatedAt = LocalDateTime.now();;

            // hoàn stock
            phonesService.getPhoneById(idphone).ifPresent(phone -> {
                phone.setStock(phone.getStock() + quantity);
                phonesService.savePhone(phone);
            });

            // hoàn khuyến mãi
            promotionsService.getPromotionsByPhoneId(idphone).ifPresent(promotion -> {
                int quantityPromotions = promotion.getQuantity();

                LocalDateTime start = promotion.getStartTime();
                LocalDateTime end   = promotion.getEndTime();

                // kiểm tra nằm trong thời gian KM
                boolean isIn = 
                    !createdAtOrder.isBefore(start) &&
                    !updatedAt.isAfter(end);

                if (usedDiscount > 0 && isIn) {

                    // bật lại KM nếu nó đang inactive
                    if ("inactive".equals(promotion.getStatus()) && quantityPromotions <= 0) {
                        promotion.setStatus("active");
                    }

                    promotion.setQuantity(quantityPromotions + usedDiscount);
                    promotionsService.savePromotion(promotion);
                }
            });
        }
    }
    

    public List<Map<String, Object>> getOrdersWithPhoneDetailsByUserId(int userId) {
        List<OrdersPhones> orders = repository.findByUserId(userId);
        User user = userService.findById(Long.valueOf(userId));
        
        List<Map<String, Object>> orderDetails = new ArrayList<>();

        for (OrdersPhones order : orders) {
            Phone phone = phonesService.getPhoneById((long) order.getPhoneId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy điện thoại!"));

         
           
            if (phone != null) {
                Map<String, Object> orderInfo = new HashMap<>();
                orderInfo.put("Id", order.getId());
                orderInfo.put("userId", order.getUserId());

                orderInfo.put("username", user.getNameUser());
                orderInfo.put("usergmail", user.getGmailUser());
                orderInfo.put("userphone", user.getPhoneNumber());
                orderInfo.put("useraddress", user.getAddress());

                orderInfo.put("phoneId", order.getPhoneId());
                orderInfo.put("imageUrl", phone.getImageUrl());
                orderInfo.put("name", phone.getName());
                orderInfo.put("quantity", order.getQuantity());
                orderInfo.put("price", order.getPrice());
                orderInfo.put("status", order.getStatus());
                orderInfo.put("isReviewed", order.getIsReviewed());

                orderDetails.add(orderInfo);
            }
        }
        return orderDetails;
    }
    
    public List<Map<String, Object>> getAllOrdersWithPhoneDetails() {
        List<OrdersPhones> orders = repository.findAll(); // Lấy tất cả đơn hàng
        List<Map<String, Object>> orderDetails = new ArrayList<>();
    
        for (OrdersPhones order : orders) {
            User user = userService.findById((long) order.getUserId());
            Phone phone = phonesService.getPhoneById((long) order.getPhoneId()).orElse(null);
    
            if (user != null && phone != null) {
                Map<String, Object> orderInfo = new HashMap<>();
                orderInfo.put("Id", order.getId());
                orderInfo.put("userId", user.getIdUser());
                orderInfo.put("username", user.getNameUser());
                orderInfo.put("usergmail", user.getGmailUser());
                orderInfo.put("userphone", user.getPhoneNumber());
                orderInfo.put("useraddress", user.getAddress());
    
                orderInfo.put("phoneId", phone.getId());
                orderInfo.put("imageUrl", phone.getImageUrl());
                orderInfo.put("name", phone.getName());
                orderInfo.put("quantity", order.getQuantity());
                orderInfo.put("price", order.getPrice());
                orderInfo.put("usedDiscount", order.getUsedDiscount());
                orderInfo.put("createdAt", order.getCreatedAt());
                orderInfo.put("updatedAt", order.getUpdatedAt());
                orderInfo.put("status", order.getStatus());
                orderInfo.put("isReviewed", order.getIsReviewed());

                orderDetails.add(orderInfo);
            }
        }
        return orderDetails;
    }

    public List<Map<String, Object>> getOrdersWithPhoneDetailsByUserIdIsVisibleToUser(int userId) {
        List<OrdersPhones> orders = repository.findByUserId(userId);
        User user = userService.findById(Long.valueOf(userId));
        
        List<Map<String, Object>> orderDetails = new ArrayList<>();

        for (OrdersPhones order : orders) {
            if(order.getIsVisibleToUser()==true)
            {
                Phone phone = phonesService.getPhoneById((long) order.getPhoneId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy điện thoại!"));
            
                if (phone != null) {
                    Map<String, Object> orderInfo = new HashMap<>();
                    orderInfo.put("Id", order.getId());
                    orderInfo.put("userId", order.getUserId());

                    orderInfo.put("username", user.getNameUser());
                    orderInfo.put("usergmail", user.getGmailUser());
                    orderInfo.put("userphone", user.getPhoneNumber());
                    orderInfo.put("useraddress", user.getAddress());

                    orderInfo.put("phoneId", order.getPhoneId());
                    orderInfo.put("imageUrl", phone.getImageUrl());
                    orderInfo.put("name", phone.getName());
                    orderInfo.put("quantity", order.getQuantity());
                    orderInfo.put("price", order.getPrice());
                    orderInfo.put("status", order.getStatus());
                    orderInfo.put("isReviewed", order.getIsReviewed());

                    orderDetails.add(orderInfo);
                }
            }
            
        }
        return orderDetails;
    }
    
    
}
