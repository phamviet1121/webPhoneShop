package com.example.phone_shop.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "cart")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;  // cột id (tự động tăng)

    @Column(name = "id_user", nullable = false)
    private int userId;  // cột id_user (liên kết bảng users)

    @Column(name = "id_phone", nullable = false)
    private int phoneId; // cột id_phone (liên kết bảng phones)

    @Column(nullable = false)
    private int quantity = 1; // cột quantity

    @Column(name = "added_at", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime addedAt; // cột added_at

    @Column(name = "note")
    private String note; // cột note
}
