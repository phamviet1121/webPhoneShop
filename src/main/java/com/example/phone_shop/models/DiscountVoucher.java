package com.example.phone_shop.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "discount_voucher")
public class DiscountVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // cột id

    @Column(nullable = false, unique = true)
    private String code; // mã voucher

    @Column(name = "discount_percent", nullable = false)
    private Integer discountPercent; // % giảm giá

    @Column(name = "max_discount")
    private Integer maxDiscount; // mức giảm tối đa

    @Column(name = "min_order_value")
    private Integer minOrderValue; // đơn hàng tối thiểu

    private Integer quantity; // số lượng còn lại (null = không giới hạn)

    @Column(name = "expired_at")
    private LocalDateTime expiredAt; // ngày hết hạn

    private String status; // active / inactive / expired

    private String description; // mô tả voucher

    @Column(name = "voucher_type", nullable = false)
    private String voucherType = "order"; // mặc định = order
}
