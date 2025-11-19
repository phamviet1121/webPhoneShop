package com.example.phone_shop.services;

// import com.example.phone_shop.models.OrdersPhones;
import com.example.phone_shop.models.Phone;
import com.example.phone_shop.models.Review;
import com.example.phone_shop.models.User;
import com.example.phone_shop.repositories.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.transaction.Transactional;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserService userService;
    
    @Autowired
    private PhoneService phonesService;

    // Lấy toàn bộ danh sách review
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
 

    public void save(Review review) {
        reviewRepository.save(review);
    }
    @Transactional
    public void deleteById(int id) {
        if (reviewRepository.existsById(id)) {
            reviewRepository.deleteById(id);
        } else {
            throw new IllegalArgumentException("Đánh giá với ID " + id + " không tồn tại!");
        }
    }
    
    
  public List<Map<String, Object>> getAllReviewsWithUserAndPhoneDetails() {
        List<Review> reviews = reviewRepository.findAll(); // Lấy tất cả đơn hàng
        List<Map<String, Object>> reviewDetails = new ArrayList<>();
    
        // B1: Tính trung bình rating theo phoneId
        Map<Integer, List<Integer>> ratingMap = new HashMap<>();
        for (Review review : reviews) {
            int phoneId = review.getPhoneId();
            ratingMap.putIfAbsent(phoneId, new ArrayList<>());
            ratingMap.get(phoneId).add(review.getRating());
        }

        Map<Integer, Double> avgRatingMap = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : ratingMap.entrySet()) {
            List<Integer> ratings = entry.getValue();
            double avg = ratings.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            avgRatingMap.put(entry.getKey(), avg);
        }




        for (Review review : reviews) {
            User user = userService.findById((long) review.getUserId());
            Phone phone = phonesService.getPhoneById((long) review.getPhoneId()).orElse(null);
    
            if (user != null && phone != null) {
                Map<String, Object> reviewInfo = new HashMap<>();
                reviewInfo.put("Id", review.getId());

                reviewInfo.put("userId", user.getIdUser());
                reviewInfo.put("username", user.getNameUser());
                reviewInfo.put("usergmail", user.getGmailUser());
                reviewInfo.put("userphone", user.getPhoneNumber());
                reviewInfo.put("useraddress", user.getAddress());
    
                reviewInfo.put("phoneId", phone.getId());
                reviewInfo.put("imageUrl", phone.getImageUrl());
                reviewInfo.put("name", phone.getName());
                reviewInfo.put("rating", review.getRating());
                reviewInfo.put("comment", review.getComment());
                
                reviewInfo.put("avgRating", avgRatingMap.getOrDefault(review.getPhoneId(), 0.0)); // ✅ Thêm dòng này

                reviewDetails.add(reviewInfo);
            }
        }
        return reviewDetails;
    }





    public List<Map<String, Object>> getReviewsByPhoneId(long targetPhoneId) {
        List<Review> reviews = reviewRepository.findAll();
        List<Map<String, Object>> reviewDetails = new ArrayList<>();
    
        // Tính trung bình rating theo phoneId
        Map<Integer, List<Integer>> ratingMap = new HashMap<>();
        for (Review review : reviews) {
            int phoneId = review.getPhoneId();
            ratingMap.putIfAbsent(phoneId, new ArrayList<>());
            ratingMap.get(phoneId).add(review.getRating());
        }
    
        Map<Integer, Double> avgRatingMap = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : ratingMap.entrySet()) {
            List<Integer> ratings = entry.getValue();
            double avg = ratings.stream().mapToInt(Integer::intValue).average().orElse(0.0);
            avgRatingMap.put(entry.getKey(), avg);
        }
    
        for (Review review : reviews) {
            if (review.getPhoneId() != targetPhoneId) continue; // ❌ Bỏ qua nếu không trùng
    
            User user = userService.findById((long) review.getUserId());
            Phone phone = phonesService.getPhoneById((long) review.getPhoneId()).orElse(null);
    
            if (user != null && phone != null) {
                Map<String, Object> reviewInfo = new HashMap<>();
                reviewInfo.put("Id", review.getId());
    
                reviewInfo.put("userId", user.getIdUser());
                reviewInfo.put("username", user.getNameUser());
                reviewInfo.put("usergmail", user.getGmailUser());
                reviewInfo.put("userphone", user.getPhoneNumber());
                reviewInfo.put("useraddress", user.getAddress());
    
                reviewInfo.put("phoneId", phone.getId());
                reviewInfo.put("imageUrl", phone.getImageUrl());
                reviewInfo.put("name", phone.getName());
    
                reviewInfo.put("rating", review.getRating());
                reviewInfo.put("comment", review.getComment());
    
                reviewInfo.put("avgRating", avgRatingMap.getOrDefault(review.getPhoneId(), 0.0));
    
                reviewDetails.add(reviewInfo);
            }
        }
    
        return reviewDetails;
    }
    
    public double calculateAverageRatingByPhoneId(long phoneId) {
        List<Review> reviews = reviewRepository.findByPhoneId(phoneId);
    
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }
    
        return reviews.stream()
                      .mapToInt(Review::getRating)
                      .average()
                      .orElse(0.0);
    }
    

    
    public Map<Integer, Double> calculateRatingPercentageByPhoneId(long phoneId) {
        List<Review> reviews = reviewRepository.findByPhoneId(phoneId);
    
        // Tổng số lượt đánh giá
        int totalReviews = reviews.size();
    
        // Nếu không có đánh giá thì trả về phần trăm 0 cho tất cả
        if (totalReviews == 0) {
            Map<Integer, Double> emptyPercentage = new HashMap<>();
            for (int i = 1; i <= 5; i++) {
                emptyPercentage.put(i, 0.0);
            }
            return emptyPercentage;
        }
    
        // Khởi tạo map lưu số lượt đánh giá từng sao
        Map<Integer, Integer> countMap = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            countMap.put(i, 0);
        }
    
        for (Review review : reviews) {
            int rating = review.getRating();
            countMap.put(rating, countMap.get(rating) + 1);
        }
    
        // Chuyển map sang phần trăm
        Map<Integer, Double> percentMap = new HashMap<>();
        for (int i = 1; i <= 5; i++) {
            double percent = (countMap.get(i) * 100.0) / totalReviews;
            percentMap.put(i, percent);
        }
    
        return percentMap;
    }




}
