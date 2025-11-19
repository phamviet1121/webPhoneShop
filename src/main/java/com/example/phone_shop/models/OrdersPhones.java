package com.example.phone_shop.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "orders_phones")
public class OrdersPhones {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int userId;
    private int phoneId;
    private int quantity;
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    private Boolean isReviewed=false;
    
    public enum OrderStatus {
        ORDERED,     // Đã đặt hàng
        CONFIRMED,   // Đã xác nhận
        CANCELED,    // Đã hủy
        REJECTED,    // Shop từ chối do lý do khác
        SUCCESS
    }
}
