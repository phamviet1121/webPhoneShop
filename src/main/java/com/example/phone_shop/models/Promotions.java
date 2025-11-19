package com.example.phone_shop.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "promotions", schema = "phone_shop")
public class Promotions {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Liên kết tới điện thoại
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phone_id", nullable = false)
    private Phone phone;

    // % khuyến mại (0–100)
    @Column(name = "discount_percent", precision = 5, scale = 2, nullable = false)
    private BigDecimal discountPercent;

    // Số lượng áp dụng khuyến mại
    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    // Khoảng thời gian lặp lại (ngày), NULL = không lặp
    @Column(name = "repeat_interval_days")
    private Integer repeatIntervalDays;

    @Column(nullable = false, columnDefinition = "ENUM('active','inactive','upcoming') DEFAULT 'active'")
    private String status;

    @PrePersist
    protected void prePersist() {
        if (this.status == null || this.status.isEmpty()) {
            this.status = "active";
        }
    }
}
