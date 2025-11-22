package com.example.phone_shop.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime; // <-- THÊM IMPORT NÀY
import org.hibernate.annotations.CreationTimestamp; // <-- THÊM IMPORT NÀY

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

    @Column(name = "useddiscount", nullable = false) 
    private int usedDiscount;

    @Column(name = "is_visible_to_user", nullable = false)
    private Boolean isVisibleToUser = true; 
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at") 
    private LocalDateTime updatedAt;
    
    public enum OrderStatus {
        ORDERED,     // Đã đặt hàng
        CONFIRMED,   // Đã xác nhận
        CANCELED,    // Đã hủy
        REJECTED,    // Shop từ chối do lý do khác
        SUCCESS
    }
    
}
