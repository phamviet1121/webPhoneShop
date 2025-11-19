package com.example.phone_shop.services;

import com.example.phone_shop.models.Cart;
import com.example.phone_shop.models.Phone;
import com.example.phone_shop.models.User;
import com.example.phone_shop.repositories.CartRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class CartService {

    private final CartRepository repository;
    private final PhoneService phoneService;
    private final UserService userService;

    public CartService(CartRepository repository, PhoneService phoneService, UserService userService) {
        this.repository = repository;
        this.phoneService = phoneService;
        this.userService = userService;
    }

    // ✅ Lấy toàn bộ giỏ hàng
    public List<Cart> getAllCarts() {
        return repository.findAll();
    }

    // ✅ Lưu (thêm / cập nhật) sản phẩm trong giỏ hàng
    public Cart saveCart(Cart cart) {
        return repository.save(cart);
    }

    // ✅ Lấy danh sách sản phẩm trong giỏ hàng của 1 người dùng
    public List<Cart> getCartsByUserId(int userId) {
        return repository.findByUserId(userId);
    }

    // ✅ Lấy 1 sản phẩm cụ thể trong giỏ hàng (theo userId + phoneId)
    public Optional<Cart> getCartByUserIdAndPhoneId(int userId, int phoneId) {
        return repository.findByUserIdAndPhoneId(userId, phoneId);
    }

    // ✅ MỚI: Thêm sản phẩm vào giỏ hoặc cập nhật số lượng
    public Cart addItemToCart(int userId, int phoneId) {
        long phoneid = (long) phoneId;
        Optional<Phone> optionalPhone = phoneService.getPhoneById(phoneid);

        if (!optionalPhone.isPresent()) {
            return null; 
        }
        Phone phone = optionalPhone.get();
        if ("inactive".equals(phone.getStatus())) {
            return null;
        }
        // Tìm kiếm xem sản phẩm đã có trong giỏ hàng của người dùng chưa
        Optional<Cart> existingCartItem = repository.findByUserIdAndPhoneId(userId, phoneId);

        if (existingCartItem.isPresent()) {
            // Nếu đã có: tăng số lượng lên 1
            Cart cart = existingCartItem.get();
            cart.setQuantity(cart.getQuantity() + 1);
            return repository.save(cart);
        } else {
            // Nếu chưa có: tạo mới một đối tượng Cart
            Cart newCartItem = new Cart();
            newCartItem.setUserId(userId);
            newCartItem.setPhoneId(phoneId);
            newCartItem.setQuantity(1); // Số lượng ban đầu là 1
            newCartItem.setAddedAt(LocalDateTime.now()); // Gán thời gian hiện tại
            return repository.save(newCartItem);
        }
    }

    // ✅ MỚI: Xóa sản phẩm khỏi giỏ hàng
    public void removeItemFromCart(int userId, int phoneId) {
        // Kiểm tra xem sản phẩm có tồn tại không để tránh lỗi không cần thiết
        Optional<Cart> existingCartItem = repository.findByUserIdAndPhoneId(userId, phoneId);
        if (existingCartItem.isPresent()) {
            // Nếu tồn tại, gọi phương thức xóa đã định nghĩa trong repository
            repository.deleteByUserIdAndPhoneId(userId, phoneId);
        } else {
            // Có thể throw một exception ở đây nếu cần để thông báo sản phẩm không có trong giỏ
             throw new RuntimeException("Sản phẩm không tìm thấy trong giỏ hàng!");
        }
    }


    // ✅ Lấy danh sách sản phẩm chi tiết trong giỏ hàng (cart + phone detail)
    public List<Map<String, Object>> getCartsByUserId_listphone(int userId) {

        List<Map<String, Object>> cartPhones = new ArrayList<>();

        // Lấy danh sách cart
        List<Cart> carts = getCartsByUserId(userId);

        // Nếu giỏ hàng rỗng → trả về list rỗng
        if (carts == null || carts.isEmpty()) {
            return cartPhones;
        }

        // Lặp từng sản phẩm trong giỏ
        for (Cart c : carts) {

            // Gọi hàm lấy danh sách chi tiết điện thoại
            Map<String, Object> phoneInfo = phoneService.getPhoneWithPromotionById((long) c.getPhoneId());

            // Tạo Map để gom thông tin
            Map<String, Object> combined = new HashMap<>();

            // Dữ liệu từ Cart
            combined.put("CartId", c.getId());
            combined.put("UserId", c.getUserId());
            combined.put("PhoneId", c.getPhoneId());
            combined.put("Quantity", c.getQuantity());
            combined.put("Note", c.getNote());
            combined.put("AddedAt", c.getAddedAt());

            // Dữ liệu chi tiết Phone
            combined.put("PhoneDetail", phoneInfo);

            // Thêm vào danh sách
            cartPhones.add(combined);
        }

        return cartPhones;
    }



    // // ✅ Lấy chi tiết giỏ hàng có thông tin người dùng + điện thoại
    // public List<Map<String, Object>> getCartWithPhoneDetailsByUserId(int userId) {
    //     List<Cart> carts = repository.findByUserId(userId);
    //     User user = userService.findById((long) userId);

    //     List<Map<String, Object>> cartDetails = new ArrayList<>();

    //     for (Cart cart : carts) {
    //         Phone phone = phoneService.getPhoneById((long) cart.getPhoneId())
    //                 .orElseThrow(() -> new RuntimeException("Không tìm thấy điện thoại!"));

    //         Map<String, Object> cartInfo = new HashMap<>();
    //         cartInfo.put("Id", cart.getId());
    //         cartInfo.put("userId", cart.getUserId());
    //         cartInfo.put("username", user.getNameUser());
    //         cartInfo.put("usergmail", user.getGmailUser());
    //         cartInfo.put("userphone", user.getPhoneNumber());
    //         cartInfo.put("useraddress", user.getAddress());
    //         cartInfo.put("phoneId", phone.getId());
    //         cartInfo.put("imageUrl", phone.getImageUrl());
    //         cartInfo.put("name", phone.getName());
    //         cartInfo.put("quantity", cart.getQuantity());
    //         cartInfo.put("addedAt", cart.getAddedAt());
    //         cartInfo.put("note", cart.getNote());

    //         cartDetails.add(cartInfo);
    //     }

    //     return cartDetails;
    // }

    // // ✅ Lấy toàn bộ giỏ hàng của tất cả người dùng kèm chi tiết
    // public List<Map<String, Object>> getAllCartsWithDetails() {
    //     List<Cart> carts = repository.findAll();
    //     List<Map<String, Object>> cartDetails = new ArrayList<>();

    //     for (Cart cart : carts) {
    //         User user = userService.findById((long) cart.getUserId());
    //         Phone phone = phoneService.getPhoneById((long) cart.getPhoneId()).orElse(null);

    //         if (user != null && phone != null) {
    //             Map<String, Object> cartInfo = new HashMap<>();
    //             cartInfo.put("Id", cart.getId());
    //             cartInfo.put("userId", user.getIdUser());
    //             cartInfo.put("username", user.getNameUser());
    //             cartInfo.put("usergmail", user.getGmailUser());
    //             cartInfo.put("userphone", user.getPhoneNumber());
    //             cartInfo.put("useraddress", user.getAddress());
    //             cartInfo.put("phoneId", phone.getId());
    //             cartInfo.put("imageUrl", phone.getImageUrl());
    //             cartInfo.put("name", phone.getName());
    //             cartInfo.put("quantity", cart.getQuantity());
    //             cartInfo.put("addedAt", cart.getAddedAt());
    //             cartInfo.put("note", cart.getNote());

    //             cartDetails.add(cartInfo);
    //         }
    //     }

    //     return cartDetails;
    // }
}
