package com.example.phone_shop.repositories;

import com.example.phone_shop.models.Promotions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PromotionsRepository extends JpaRepository<Promotions, Long> {
    // Tìm theo trạng thái
    List<Promotions> findByStatus(String status);

    // Tìm theo id điện thoại
    List<Promotions> findByPhone_Id(Long phoneId);
    Optional<Promotions> findByPhoneId(Long phoneId);
    
    
}

