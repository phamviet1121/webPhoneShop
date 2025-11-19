package com.example.phone_shop.repositories;

import com.example.phone_shop.models.UserVoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserVoucherRepository extends JpaRepository<UserVoucher, Long> {

    // Lấy danh sách voucher mà user đã thu thập
    List<UserVoucher> findByUserId(Integer userId);

    // Kiểm tra user đã thu thập voucher này chưa
    boolean existsByUserIdAndVoucherId(Integer userId, Long voucherId);

    // Lấy theo user + voucher (phục vụ xác nhận dùng voucher)
    UserVoucher findByUserIdAndVoucherId(Integer userId, Long voucherId);
    // Lấy theo user + voucher (phục vụ xác nhận dùng voucher)
    UserVoucher findByUserIdAndId(Integer userId, Long id);
}
