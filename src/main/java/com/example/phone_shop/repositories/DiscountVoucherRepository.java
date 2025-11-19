package com.example.phone_shop.repositories;

import com.example.phone_shop.models.DiscountVoucher;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscountVoucherRepository extends JpaRepository<DiscountVoucher, Long> {
    List<DiscountVoucher> findAll();
    DiscountVoucher findByCode(String code);
    @Query("SELECT v FROM DiscountVoucher v WHERE v.status <> 'inactive' AND (v.expiredAt < :now OR v.quantity <= 0)")
    List<DiscountVoucher> findVouchersToDeactivate(@Param("now") LocalDateTime now);
}
