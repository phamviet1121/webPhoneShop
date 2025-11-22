package com.example.phone_shop.models;

import java.math.BigDecimal;
import java.time.LocalDateTime; // <-- THÊM IMPORT NÀY

import org.hibernate.annotations.CreationTimestamp; // <-- THÊM IMPORT NÀY
import org.hibernate.annotations.UpdateTimestamp;   // <-- THÊM IMPORT NÀY

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "phones")
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String brand;
    private double price;
    private int stock;
    private String imageUrl; 
    
    // @Column(precision = 5, scale = 2, nullable = false)
    // private BigDecimal discount = BigDecimal.ZERO;  // Mặc định 0.00

    @Column(columnDefinition = "TEXT")
        private String description; // Thêm cột mô tả sản phẩm

    @Column(nullable = false, columnDefinition = "ENUM('active', 'inactive') DEFAULT 'active'")
        private String status; // Trạng thái

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "category_id", nullable = false)
        private Long categoryId; // Thêm cột categoryId


    @PrePersist
    protected void prePersist() {
        if (this.status == null || this.status.isEmpty()) {
            this.status = "active"; // Chỉ đặt mặc định nếu chưa có giá trị nào
        }
    }

}
