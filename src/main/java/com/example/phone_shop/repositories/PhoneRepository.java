package com.example.phone_shop.repositories;


import com.example.phone_shop.models.Phone; // Đúng đường dẫn package
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {
  List<Phone> findByNameContainingOrBrandContaining(String name, String brand);
  
}