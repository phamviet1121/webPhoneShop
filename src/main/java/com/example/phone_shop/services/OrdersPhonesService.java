package com.example.phone_shop.services;

import com.example.phone_shop.models.OrdersPhones;
import com.example.phone_shop.models.Phone;
import com.example.phone_shop.models.User;
import com.example.phone_shop.repositories.OrdersPhonesRepository;
import org.springframework.stereotype.Service;

//import com.example.phone_shop.services.UserService;

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
    public OrdersPhonesService(OrdersPhonesRepository repository,PhoneService phonesService,UserService userService) {
        this.repository = repository;
        this.phonesService = phonesService;
        this.userService = userService;
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
                orderInfo.put("status", order.getStatus());
                orderInfo.put("isReviewed", order.getIsReviewed());

                orderDetails.add(orderInfo);
            }
        }
        return orderDetails;
    }
    
    
}
