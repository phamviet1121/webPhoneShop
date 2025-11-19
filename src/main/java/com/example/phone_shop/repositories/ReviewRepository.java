package com.example.phone_shop.repositories;

import com.example.phone_shop.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByPhoneId(long phoneId);
}
