package com.example.phone_shop.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "user_voucher")
public class UserVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // khóa chính

    @Column(name = "user_id", nullable = false)
    private Integer userId;  // id của bảng users

    @Column(name = "voucher_id", nullable = false)
    private Long voucherId;  // id của bảng discount_voucher

    @Column(name = "is_used", nullable = false)
    private Boolean isUsed = false;  // đã sử dụng hay chưa

    @Column(name = "received_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime receivedAt;  // thời gian người dùng thu thập voucher

    @Column(name = "used_at")
    private LocalDateTime usedAt;  // thời gian sử dụng voucher (nếu có)
}
