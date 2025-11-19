package com.example.phone_shop.services;

import com.example.phone_shop.models.DiscountVoucher;
import com.example.phone_shop.models.UserVoucher;
import com.example.phone_shop.repositories.UserVoucherRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserVoucherService {

    @Autowired
    private DiscountVoucherService discountVoucherService;

    private final UserVoucherRepository userVoucherRepository;

    // Lấy tất cả user_voucher (dành cho admin)
    public List<UserVoucher> getAllUserVouchers() {
        return userVoucherRepository.findAll();
    }

    // Lấy theo userId
    public List<UserVoucher> getUserVouchers(Integer userId) {
        return userVoucherRepository.findByUserId(userId);
    }
    // Lấy theo userId và voucherId
    public UserVoucher getUserVoucherVoucherId(Integer userId, Long voucherId) {
        return userVoucherRepository.findByUserIdAndVoucherId(userId, voucherId);
    }
    // Lấy theo userId và Id
    public UserVoucher getUserVoucherId(Integer userId, Long id) {
        return userVoucherRepository.findByUserIdAndId(userId, id);
    }

    public boolean hasCollected(Integer userId, Long voucherId) {
        return userVoucherRepository.existsByUserIdAndVoucherId(userId, voucherId);
    }

    public UserVoucher collectVoucher(Integer userId, Long voucherId) {
        UserVoucher uv = new UserVoucher();
        uv.setUserId(userId);
        uv.setVoucherId(voucherId);
        uv.setIsUsed(false);
        uv.setReceivedAt(LocalDateTime.now());
        return userVoucherRepository.save(uv);
    }

    public void markVoucherUsed(Integer userId, Long voucherId) {
        UserVoucher uv = userVoucherRepository.findByUserIdAndId(userId, voucherId);
        if (uv != null) {
            uv.setIsUsed(true);
            uv.setUsedAt(LocalDateTime.now());
            userVoucherRepository.save(uv);
        }
    }

    public List<Map<String, Object>> getgetUserVouchersDiscount(Integer userId) {
        List<UserVoucher> userVouchers = userVoucherRepository.findByUserId(userId);
        List<Map<String, Object>> userVouchersDiscounts = new ArrayList<>();

        for (UserVoucher userVoucher : userVouchers) {

            DiscountVoucher discountVoucher = discountVoucherService.getById(userVoucher.getVoucherId());
            Map<String, Object> userVoucherInfo = new HashMap<>();
                userVoucherInfo.put("id", userVoucher.getId());
                userVoucherInfo.put("userId", userVoucher.getUserId());
                userVoucherInfo.put("isUsed", userVoucher.getIsUsed());
                userVoucherInfo.put("receivedAt", userVoucher.getReceivedAt());
                userVoucherInfo.put("usedAt", userVoucher.getUsedAt());

                userVoucherInfo.put("voucherId", discountVoucher.getId());
                userVoucherInfo.put("code", discountVoucher.getCode());
                userVoucherInfo.put("discountPercent", discountVoucher.getDiscountPercent());
                userVoucherInfo.put("maxDiscount", discountVoucher.getMaxDiscount());
                userVoucherInfo.put("minOrderValue", discountVoucher.getMinOrderValue());
                userVoucherInfo.put("quantity", discountVoucher.getQuantity());
                userVoucherInfo.put("expiredAt", discountVoucher.getExpiredAt());
                userVoucherInfo.put("status", discountVoucher.getStatus());
                userVoucherInfo.put("description", discountVoucher.getDescription());
                userVoucherInfo.put("voucherType", discountVoucher.getVoucherType());

           userVouchersDiscounts.add(userVoucherInfo);

        }

        return userVouchersDiscounts;

    }
    public List<Map<String, Object>> getgetUserVouchersDiscountProduct(Integer userId,Long voucherId) {
        UserVoucher userVoucher = userVoucherRepository.findByUserIdAndId(userId,voucherId);
        List<Map<String, Object>> userVouchersDiscounts = new ArrayList<>();

            DiscountVoucher discountVoucher = discountVoucherService.getById(userVoucher.getVoucherId());
            Map<String, Object> userVoucherInfo = new HashMap<>();
                userVoucherInfo.put("id", userVoucher.getId());
                userVoucherInfo.put("userId", userVoucher.getUserId());
                userVoucherInfo.put("isUsed", userVoucher.getIsUsed());
                userVoucherInfo.put("receivedAt", userVoucher.getReceivedAt());
                userVoucherInfo.put("usedAt", userVoucher.getUsedAt());

                userVoucherInfo.put("voucherId", discountVoucher.getId());
                userVoucherInfo.put("code", discountVoucher.getCode());
                userVoucherInfo.put("discountPercent", discountVoucher.getDiscountPercent());
                userVoucherInfo.put("maxDiscount", discountVoucher.getMaxDiscount());
                userVoucherInfo.put("minOrderValue", discountVoucher.getMinOrderValue());
                userVoucherInfo.put("quantity", discountVoucher.getQuantity());
                userVoucherInfo.put("expiredAt", discountVoucher.getExpiredAt());
                userVoucherInfo.put("status", discountVoucher.getStatus());
                userVoucherInfo.put("description", discountVoucher.getDescription());
                userVoucherInfo.put("voucherType", discountVoucher.getVoucherType());
                userVouchersDiscounts.add(userVoucherInfo);

        return userVouchersDiscounts;

    }
}
