package com.example.phone_shop.repositories;

import com.example.phone_shop.models.ContactMessage;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ContactRepository extends JpaRepository<ContactMessage, Integer> {
    // Sau này bạn có thể thêm hàm tìm các tin chưa xóa
    // List<ContactMessage> findByDeletedAtIsNull();
    // 1. Lấy tất cả tin nhắn chưa bị xóa (deletedAt là null), sắp xếp mới nhất lên đầu
    List<ContactMessage> findByDeletedAtIsNullOrderByCreatedAtDesc();

    // 2. Đếm số tin nhắn mới (Status = 0) và chưa bị xóa (để làm thống kê trên Dashboard)
    long countByStatusAndDeletedAtIsNull(Integer status);
}