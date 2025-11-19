package com.example.phone_shop.services;

import com.example.phone_shop.models.DiscountVoucher;
import com.example.phone_shop.repositories.DiscountVoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DiscountVoucherService {

    @Autowired
    private DiscountVoucherRepository voucherRepository;

    public List<DiscountVoucher> getAll() {
        return voucherRepository.findAll();
    }

    public DiscountVoucher save(DiscountVoucher voucher) {
        return voucherRepository.save(voucher);
    }

    public DiscountVoucher getById(Long id) {
        return voucherRepository.findById(id).orElse(null);
    }

    public DiscountVoucher update(DiscountVoucher voucher) {
        if (voucher.getId() == null) {
            throw new RuntimeException("ID voucher không tồn tại, không thể cập nhật!");
        }
        return voucherRepository.save(voucher);
    }

    public void delete(Long id) {
        voucherRepository.deleteById(id);
    }

    // Kiểm tra voucher có hết hạn không
    public boolean isExpired(DiscountVoucher voucher) {
        if (voucher.getExpiredAt() == null) return false;
        return voucher.getExpiredAt().isBefore(LocalDateTime.now());
    }

    // Giảm số lượng voucher sau khi sử dụng
    public void decreaseQuantity(Long id) {
        // Sử dụng findById để xử lý trường hợp không tìm thấy voucher
        DiscountVoucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy voucher với ID: " + id));

        if (voucher.getQuantity() != null && voucher.getQuantity() > 0) {
            voucher.setQuantity(voucher.getQuantity() - 1);
            voucherRepository.save(voucher);

            // QUAN TRỌNG: Sau khi giảm số lượng, gọi phương thức kiểm tra để cập nhật trạng thái
            checkAndUpdateVoucherStatus(voucher);
        }
    }

    // Tìm voucher theo mã để áp dụng
    public DiscountVoucher getByCode(String code) {
        return voucherRepository.findByCode(code);
    }
    public void checkAndUpdateVoucherStatus(DiscountVoucher voucher) {
        if (voucher == null) {
            return; // Hoặc ném ra một ngoại lệ nếu cần
        }
        
        // Kiểm tra xem voucher đã "inactive" chưa để tránh ghi vào DB không cần thiết
        if ("inactive".equalsIgnoreCase(voucher.getStatus())) {
            return;
        }

        boolean expired = isExpired(voucher);
        boolean outOfStock = voucher.getQuantity() != null && voucher.getQuantity() <= 0;

        // Nếu hết hạn hoặc hết số lượng, thì cập nhật trạng thái
        if (expired || outOfStock) {
            voucher.setStatus("inactive");
            voucherRepository.save(voucher);
        }
    }

    public void deactivateExpiredOrOutOfStockVouchers() {
        // Tìm tất cả các voucher cần được vô hiệu hóa bằng câu query đã viết trong Repository
        List<DiscountVoucher> vouchersToDeactivate = voucherRepository.findVouchersToDeactivate(LocalDateTime.now());

        // Nếu không có voucher nào thì thoát
        if (vouchersToDeactivate.isEmpty()) {
            return;
        }
        
        // Cập nhật trạng thái cho từng voucher tìm được
        for (DiscountVoucher voucher : vouchersToDeactivate) {
            voucher.setStatus("inactive");
        }

        // Lưu tất cả các thay đổi vào cơ sở dữ liệu trong một lần
        voucherRepository.saveAll(vouchersToDeactivate);
        
        // Ghi log để theo dõi (tùy chọn)
        System.out.println("Đã vô hiệu hóa " + vouchersToDeactivate.size() + " voucher.");
    }

    public boolean isVoucherValidForUse(Long id) {
        // Tìm voucher trong CSDL
        Optional<DiscountVoucher> voucherOptional = voucherRepository.findById(id);

        // Nếu không tìm thấy voucher, trả về false
        if (!voucherOptional.isPresent()) {
            return false;
        }

        DiscountVoucher voucher = voucherOptional.get();

        // Kiểm tra tất cả các điều kiện
        boolean isActive = !"inactive".equalsIgnoreCase(voucher.getStatus());
        boolean isNotExpired = voucher.getExpiredAt() == null || voucher.getExpiredAt().isAfter(LocalDateTime.now());
        boolean isInStock = voucher.getQuantity() == null || voucher.getQuantity() > 0;

        // Voucher chỉ hợp lệ khi tất cả các điều kiện trên đều đúng
        return isActive && isNotExpired && isInStock;
    }
}
