package com.example.phone_shop.repositories;

import com.example.phone_shop.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findAll();

    // Lấy danh sách tất cả sản phẩm trong giỏ của 1 người dùng
    List<Cart> findByUserId(int userId);

    // Lấy 1 sản phẩm cụ thể trong giỏ hàng của người dùng (theo userId và phoneId)
    Optional<Cart> findByUserIdAndPhoneId(int userId, int phoneId);
    
    //Xóa một sản phẩm khỏi giỏ hàng dựa trên userId và phoneId
    @Transactional
    void deleteByUserIdAndPhoneId(int userId, int phoneId);

    
}
