package com.example.phone_shop.repositories;

import com.example.phone_shop.models.OrdersPhones;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersPhonesRepository extends JpaRepository<OrdersPhones, Integer> {
    List<OrdersPhones> findAll();
    List<OrdersPhones> findByUserId(int userId);
}
