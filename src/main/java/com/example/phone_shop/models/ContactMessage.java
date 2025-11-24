package com.example.phone_shop.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "contact_messages")
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id; // Map với INT(11)

    @Column(name = "full_name", nullable = false)
    private String fullName; // Map với full_name

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String email;

    private String subject;

    @Column(columnDefinition = "TEXT")
    private String message;

    // Trạng thái: 0 = Chưa xem, 1 = Đã xem, 2 = Đã xử lý
    // Gán mặc định = 0 để khi lưu Java tự hiểu là tin mới
    @Column(nullable = false)
    private Integer status = 0; 

    // Tự động lấy giờ hiện tại khi insert
    @CreationTimestamp 
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Tự động cập nhật giờ khi update
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Thời gian xóa mềm (mặc định null)
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}