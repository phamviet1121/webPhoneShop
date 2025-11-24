package com.example.phone_shop.controllers;

import com.example.phone_shop.models.ContactMessage;
// import com.example.phone_shop.models.Cart;
import com.example.phone_shop.models.DiscountVoucher;
import com.example.phone_shop.models.Phone;
import com.example.phone_shop.models.Promotions;
import com.example.phone_shop.models.Review;
import com.example.phone_shop.services.CartService;
import com.example.phone_shop.services.ContactService;
import com.example.phone_shop.services.DiscountVoucherService;
import com.example.phone_shop.services.OrdersPhonesService;
import com.example.phone_shop.services.PhoneService;
import com.example.phone_shop.services.ReviewService;
import com.example.phone_shop.services.StatisticalService;
import com.example.phone_shop.services.PromotionsService;
import com.example.phone_shop.services.UserService;
import com.example.phone_shop.services.UserVoucherService;

//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.phone_shop.models.User;
import com.example.phone_shop.models.UserVoucher;

import jakarta.servlet.http.HttpSession;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
//import com.example.phone_shop.models.User; 
//import com.example.phone_shop.services.UserService;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import java.nio.file.Path;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/phones")
public class PhoneController {
    private final PhoneService phoneService;
    private final UserService userService; // ⚠ Thêm UserService
    private final OrdersPhonesService ordersPhonesService;
    private final ReviewService reviewService;
    private final PromotionsService promotionsService;
    private final DiscountVoucherService voucherService;
    private final UserVoucherService userVoucherService;
    private final CartService cartService;
    private final StatisticalService statisticalService;
    private final ContactService contactService;

    // @Autowired
    public PhoneController(PhoneService phoneService, UserService userService, OrdersPhonesService ordersPhonesService,
            ReviewService reviewService, PromotionsService promotionsService, DiscountVoucherService voucherService,
            UserVoucherService userVoucherService, CartService cartService,StatisticalService statisticalService,ContactService contactService) {
        this.phoneService = phoneService;
        this.userService = userService;
        this.ordersPhonesService = ordersPhonesService;
        this.reviewService = reviewService;
        this.promotionsService = promotionsService;
        this.voucherService = voucherService;
        this.userVoucherService = userVoucherService;
        this.cartService = cartService;
        this.statisticalService = statisticalService;
        this.contactService = contactService;

    }

    // 🛠 Danh sách điện thoại
    @GetMapping
    public String listPhones(Model model) {
        model.addAttribute("phones", phoneService.getAllPhones());
        model.addAttribute("shouldShowLogin", true);
        model.addAttribute("extraItemsFragment", null);
        List<Map<String, Object>> phonesWithPromotions = phoneService.getAllPhones_list_phones();
        model.addAttribute("phonesWithPromotions", phonesWithPromotions);
        promotionsService.updatePromotionStatuses();
        return "phone-list"; // Trả về file Thymeleaf: phone-list.html
    }

    // @GetMapping("/list")
    // public String showPromotionsWithPhones(Model model) {
    //     List<Map<String, Object>> phonesWithPromotions = phoneService.getAllPhones_list_phones();
    //     model.addAttribute("phonesWithPromotions", phonesWithPromotions);
    //     return "promotions_list"; // Tên file HTML (Thymeleaf)
    // }

    // 🛠 Hiển thị form thêm điện thoại
    @GetMapping("/new")
    public String showAddPhoneForm(Model model) {
        model.addAttribute("phone", new Phone());
        return "admins/add_phone"; // Trả về file Thymeleaf: add_phone.html
    }

    // 🛠 Lưu điện thoại mới
    // @PostMapping("/save")
    // public String savePhone(@ModelAttribute Phone phone) {
    // phoneService.savePhone(phone);
    // return "redirect:/phones/index_Admin"; // Chuyển hướng về danh sách
    // }

    @PostMapping("/save")
    public String savePhone(@ModelAttribute Phone phone,
            @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            if (!imageFile.isEmpty()) {
                // 🔹 Thư mục lưu ảnh trên ổ D:
                String uploadDir = "D:/phone_shop/uploads/img/";
                Path uploadPath = Paths.get(uploadDir);

                // 🔹 Kiểm tra thư mục, nếu chưa có thì tạo
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // 🔹 Tạo tên file duy nhất
                String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);

                // 🔹 Lưu file vào thư mục
                Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                // 🔹 Lưu đường dẫn vào DB (đường dẫn tuyệt đối hoặc tương đối tùy theo cách sử
                // dụng)
                phone.setImageUrl("/uploads/img/" + fileName);
            }

            // 🔹 Lưu điện thoại vào DB
            phoneService.savePhone(phone);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/phones/index_Admin";
    }

    // 🛠 Hiển thị form cập nhật điện thoại
    @GetMapping("/edit/{id}")
    public String showEditPhoneForm(@PathVariable Long id, Model model) {
        Optional<Phone> phone = phoneService.getPhoneById(id);
        if (phone.isPresent()) {
            model.addAttribute("phone", phone.get());
            return "admins/edit_phone"; // Dùng chung form
        } else {
            return "redirect:/phones/index_Admin";
        }
    }

    @PostMapping("/update")
    public String updatePhone(@ModelAttribute Phone phone,
            @RequestParam("imageFile") MultipartFile imageFile) {
        if (!imageFile.isEmpty()) {
            // Đường dẫn lưu file
            String uploadDir = "uploads/";
            String fileName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
            Path uploadPath = Paths.get(uploadDir);

            try {
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                Files.copy(imageFile.getInputStream(), uploadPath.resolve(fileName),
                        StandardCopyOption.REPLACE_EXISTING);
                phone.setImageUrl("/" + uploadDir + fileName); // Lưu đường dẫn vào DB
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        phoneService.updatePhone(phone);
        return "redirect:/phones/index_Admin";
    }
    // public String updatePhone(@ModelAttribute Phone phone) {
    // phoneService.savePhone(phone); // Lưu lại điện thoại sau khi chỉnh sửa
    // return "redirect:/phones/index_Admin"; // Chuyển hướng về danh sách

    // 🛠 Xóa điện thoại
    @GetMapping("/delete/{id}")
    public String deletePhone(@PathVariable Long id) {
        phoneService.deletePhone(id);
        return "redirect:/phones/index_Admin";
    }
    // @GetMapping("/login")
    // public String showLoginForm() {
    // return "login"; // Chuyển hướng đến trang login.html
    // }
    // Xử lý đăng nhập
    // @PostMapping("/login")
    // public String loginUser(@RequestParam String username, @RequestParam String
    // password, Model model) {
    // if ("admin@gmail.com".equals(username) && "123456".equals(password)) {
    // return "redirect:/phones/index_User"; // Chuyển hướng đến trang chính nếu
    // đúng
    // } else {
    // model.addAttribute("error", "Email hoặc mật khẩu không đúng!");
    // return "login"; // Quay lại trang đăng nhập nếu sai
    // }
    // }

    // @GetMapping("/register")
    // public String showRegisterForm(Model model) {
    // model.addAttribute("user", new User());
    // return "register"; // Chuyển hướng đến trang register.html
    // }

    // Xử lý đăng ký
    // @PostMapping("/register")
    // public String registerUser(@ModelAttribute User user, Model model) {
    // String message = userService.registerUser(user);
    // model.addAttribute("message", message);
    // return "register"; // Quay lại trang đăng ký với thông báo
    // }

    // Hiển thị trang sau khi đăng nhập thành công
    @GetMapping("/index_User")
    public String showIndexUser(HttpSession session, Model model) {

        Integer idUser = (Integer) session.getAttribute("idUser");
        if (idUser != null) {
            // Gọi hàm findById để lấy thông tin user từ database
            // User user = userService.findById(Long.valueOf(idUser));
            User user = (User) session.getAttribute("user");
            if (user != null) {
                model.addAttribute("user", user); // Truyền user vào Model
            }
        } else {
            return "redirect:/auth/login";
        }
        phoneService.updateStatus();
        List<Map<String, Object>> phonesWithPromotions = phoneService.getAllPhones_list_phones();
        model.addAttribute("phonesWithPromotions", phonesWithPromotions);

        // danh sách Voucher
        List<DiscountVoucher> list = voucherService.getAll();
        model.addAttribute("vouchers", list);

        model.addAttribute("idUser", idUser);
        promotionsService.updatePromotionStatuses();
        voucherService.deactivateExpiredOrOutOfStockVouchers();
        return "users/index_User"; // Chuyển đến trang index_User.html
    }

    @GetMapping("/index_Admin")
    public String showIndexAdmin(HttpSession session, Model model) {
        phoneService.updateStatus();

        List<Phone> phones = phoneService.getAllPhones(); // Lấy danh sách điện thoại từ database
        model.addAttribute("phones", phones); // Truyền vào Model

        List<User> users = userService.getAllUsers(); // Lấy danh sách người dùng từ UserService
        model.addAttribute("users", users); // Truyền dữ liệu vào model

        List<Map<String, Object>> orderDetails = ordersPhonesService.getAllOrdersWithPhoneDetails();
        model.addAttribute("orderDetails", orderDetails);

        // List<Map<String, Object>> DiscountPhones = phoneService.getAllPhones_discount();
        // model.addAttribute("discountPhones", DiscountPhones);

        List<Map<String, Object>> promotions = promotionsService.getAllPromotionsWithPhones();
        model.addAttribute("promotions", promotions);

        // danh sách Voucher
        List<DiscountVoucher> list = voucherService.getAll();
        model.addAttribute("vouchers", list);


        List<UserVoucher> listUserVoucher = userVoucherService.getAllUserVouchers();
        model.addAttribute("userVouchers", listUserVoucher);

        // danh sách đánh giá
        List<Review> reviews = reviewService.getAllReviews();
        model.addAttribute("reviews", reviews);

        List<Map<String, Object>> reviewDetails = reviewService.getAllReviewsWithUserAndPhoneDetails();
        model.addAttribute("reviewDetails", reviewDetails);

        List<ContactMessage> listcontact = contactService.getAllActiveContacts();
        // Đẩy vào model để HTML dùng
        model.addAttribute("contacts", listcontact);

        Integer idUser = (Integer) session.getAttribute("idUser");
        User user = (User) session.getAttribute("user");
        if (idUser != null && user != null && "admin".equals(user.getRole())) {
            
            model.addAttribute("user", user); 
            
        } else {
            // Nếu không đăng nhập HOẶC đã đăng nhập nhưng không phải admin
            return "redirect:/auth/login";
        }

        model.addAttribute("idUser", idUser);
        promotionsService.updatePromotionStatuses();
        voucherService.deactivateExpiredOrOutOfStockVouchers();
        return "admins/index_Admin"; // Chuyển đến trang index_User.html
    }

    @GetMapping("/reviewsdelete/{id}")
    public String deleteReview(@PathVariable int id) {
        reviewService.deleteById(id);
        return "redirect:/phones/index_Admin"; // hoặc trang hiện tại
    }

    // @GetMapping("/List_users")
    // public String listUsers(Model model) {
    // model.addAttribute("List_users", userService.getAllUsers());
    // return "List_users"; // Trả về file Thymeleaf: users.html
    // }
    // @GetMapping("/search")
    // public String searchPhones(@RequestParam("keyword") String keyword, Model
    // model) {
    // // List<Map<String, Object>> phonesWithPromotions_search =
    // phoneService.searchAndGetPhonesWithPromotions(keyword);
    // // model.addAttribute("phonesWithPromotion_searchs",
    // phonesWithPromotions_search);
    // List<Map<String, Object>> searchResults =
    // phoneService.searchAndGetPhonesWithPromotions(keyword);

    // // Đổi tên biến truyền ra cho khớp với file debug
    // model.addAttribute("phonesData", searchResults);
    // model.addAttribute("searchKeyword", keyword);
    // model.addAttribute("resultCount", searchResults.size());

    // return "search/search-results"; // Trả về trang kết quả tìm kiếm
    // }
    @GetMapping("/search")
    public String searchPhones(@RequestParam(value = "keyword", required = false) String keyword, Model model) {
        // 1️⃣ Nếu keyword null hoặc rỗng thì không tìm kiếm
        if (keyword == null || keyword.trim().isEmpty()) {
            model.addAttribute("phonesData", new ArrayList<>()); // Danh sách rỗng
            model.addAttribute("searchKeyword", "");
            model.addAttribute("resultCount", 0);
            model.addAttribute("message", "Vui lòng nhập từ khóa để tìm kiếm.");
            return "search/search-results";
        }

        // 2️⃣ Gọi service để tìm kiếm điện thoại theo từ khóa
        List<Map<String, Object>> searchResults = phoneService.searchAndGetPhonesWithPromotions(keyword.trim());

        // 3️⃣ Gán dữ liệu ra model (dù có kết quả hay không vẫn trả về)
        model.addAttribute("phonesData", searchResults);
        model.addAttribute("searchKeyword", keyword);
        model.addAttribute("resultCount", searchResults.size());

        // 4️⃣ Nếu không có kết quả → thêm thông báo
        if (searchResults.isEmpty()) {
            model.addAttribute("message", "Không tìm thấy sản phẩm nào phù hợp với từ khóa \"" + keyword + "\"");
        }

        // 5️⃣ Trả về view kết quả
        return "search/search-results";
    }

    @GetMapping("/search_user")
    public String searchPhonesUser(@RequestParam("keyword") String keyword, Model model) {
        // 1️⃣ Nếu keyword null hoặc rỗng thì không tìm kiếm
        if (keyword == null || keyword.trim().isEmpty()) {
            model.addAttribute("phonesData", new ArrayList<>()); // Danh sách rỗng
            model.addAttribute("searchKeyword", "");
            model.addAttribute("resultCount", 0);
            model.addAttribute("message", "Vui lòng nhập từ khóa để tìm kiếm.");
            return "search/search-results_user";
        }

        // 2️⃣ Gọi service để tìm kiếm điện thoại theo từ khóa
        List<Map<String, Object>> searchResults = phoneService.searchAndGetPhonesWithPromotions(keyword.trim());

        // 3️⃣ Gán dữ liệu ra model (dù có kết quả hay không vẫn trả về)
        model.addAttribute("phonesData", searchResults);
        model.addAttribute("searchKeyword", keyword);
        model.addAttribute("resultCount", searchResults.size());

        // 4️⃣ Nếu không có kết quả → thêm thông báo
        if (searchResults.isEmpty()) {
            model.addAttribute("message", "Không tìm thấy sản phẩm nào phù hợp với từ khóa \"" + keyword + "\"");
        }

        return "search/search-results_user"; // Trả về trang kết quả tìm kiếm
    }

    // @PostMapping("/phones/buy/{id}")
    // public String buyPhone(@PathVariable Long id, Principal principal, Model
    // model) {
    // if (principal == null) {
    // model.addAttribute("error", "Khách hàng chưa đăng nhập. Để có thể mua hàng,
    // yêu cầu bạn phải đăng nhập!");
    // return "redirect:/index_User"; // Quay về trang danh sách sản phẩm
    // }

    // Optional<Phone> phoneOptional = phoneRepository.findById(id);
    // if (phoneOptional.isEmpty()) {
    // model.addAttribute("error", "Sản phẩm không tồn tại!");
    // return "redirect:/index_User";
    // }

    // Phone phone = phoneOptional.get();

    // // Kiểm tra số lượng hàng
    // if (phone.getStock() <= 0) {
    // model.addAttribute("error", "Sản phẩm đã hết hàng!");
    // return "redirect:/index_User";
    // }

    // // Lấy user từ database
    // User user = userRepository.findByUsername(principal.getName());

    // // Trừ hàng trong kho
    // phone.setStock(phone.getStock() - 1);
    // phoneRepository.save(phone);

    // // Lưu đơn hàng vào database
    // Order order = new Order(user, phone.getPrice());
    // orderRepository.save(order);

    // model.addAttribute("message", "Mua hàng thành công!");
    // return "redirect:/index_User";
    // }

    @GetMapping("/detail/{id}")
    public String productDetail(@PathVariable long id, Model model) {
        Optional<Phone> phoneOptional = phoneService.getPhoneById(id);

        Map<String, Object> phoneInfo = phoneService.getPhoneWithPromotionById(id);
        model.addAttribute("phoneInfo", phoneInfo);

        List<Map<String, Object>> filteredReviews = reviewService.getReviewsByPhoneId(id);
        model.addAttribute("reviewPhoneDetails", filteredReviews);

        double avgRating = reviewService.calculateAverageRatingByPhoneId(id);
        model.addAttribute("avgRating", avgRating);

        Map<Integer, Double> ratingPercent = reviewService.calculateRatingPercentageByPhoneId(id);
        model.addAttribute("ratingPercent", ratingPercent);

        if (phoneOptional.isPresent()) {
            model.addAttribute("phone", phoneOptional.get());
            return "details/product_detail"; // Tên file Thymeleaf
        } else {
            return "redirect:/phones"; // Quay lại nếu không tìm thấy
        }
    }

    @GetMapping("/detail_user/{id}")
    public String productDetailUser(@PathVariable long id, Model model, HttpSession session) {
        Optional<Phone> phoneOptional = phoneService.getPhoneById(id);

        Map<String, Object> phoneInfo = phoneService.getPhoneWithPromotionById(id);
        model.addAttribute("phoneInfo", phoneInfo);

        List<Map<String, Object>> filteredReviews = reviewService.getReviewsByPhoneId(id);
        model.addAttribute("reviewPhoneDetails", filteredReviews);

        double avgRating = reviewService.calculateAverageRatingByPhoneId(id);
        model.addAttribute("avgRating", avgRating);

        Map<Integer, Double> ratingPercent = reviewService.calculateRatingPercentageByPhoneId(id);
        model.addAttribute("ratingPercent", ratingPercent);

        Integer idUser = (Integer) session.getAttribute("idUser");

        if (idUser != null) {
            // Gọi hàm findById để lấy thông tin user từ database
            // User user = userService.findById(Long.valueOf(idUser));
            User user = (User) session.getAttribute("user");
            if (user != null) {
                model.addAttribute("user", user); // Truyền user vào Model
            }
        } else {
            return "redirect:/auth/login";
        }

        model.addAttribute("idUser", idUser);

        if (phoneOptional.isPresent()) {
            model.addAttribute("phone", phoneOptional.get());
            return "details/product_detail_user"; // Tên file Thymeleaf
        } else {
            return "redirect:/phones"; // Quay lại nếu không tìm thấy
        }
    }

    // Hiển thị form thêm giảm giá
    @GetMapping("/discount/new")
    public String showAddDiscountForm(Model model) {
        List<Phone> phones = phoneService.getAllPhones(); // Lấy danh sách điện thoại từ database
        model.addAttribute("phones", phones);

        // Lấy toàn bộ danh sách khuyến mãi
        List<Promotions> promotions = promotionsService.getAllPromotions();
        model.addAttribute("promotions", promotions);

        // Truyền một promotion rỗng để binding form
        model.addAttribute("promotion", new Promotions());
        return "admins/add_discount";
    }

    // lưu điện thoại giảm giá
    @PostMapping("/discount/save")
    public String savePromotion(
            @RequestParam("phoneId") Long phoneId,
            @RequestParam("discountPercent") BigDecimal discountPercent,
            @RequestParam("quantity") int quantity,
            @RequestParam(value = "startTime", required = false) String startTime,
            @RequestParam(value = "endTime", required = false) String endTime,
            @RequestParam(value = "repeatIntervalDays", required = false) Integer repeatIntervalDays,
            @RequestParam(value = "status", defaultValue = "ACTIVE") String status,
            Model model) {
        Phone phone = phoneService.getPhoneById(phoneId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy điện thoại với ID: " + phoneId));

        // 🔹 Kiểm tra số lượng khuyến mãi không vượt quá số lượng điện thoại hiện có
        if (quantity > phone.getStock()) {
            model.addAttribute("errorMessage",
                    "Số lượng áp dụng khuyến mãi (" + quantity +
                            ") vượt quá số lượng điện thoại hiện có (" + phone.getStock() + ")");
            model.addAttribute("phones", phoneService.getAllPhones());
            return "add_promotion";
        }

        // 🔹 Kiểm tra xem điện thoại đã có khuyến mãi chưa
        Optional<Promotions> existingPromotion = promotionsService.findByPhoneId(phoneId);

        Promotions promotion;
        if (existingPromotion.isPresent()) {
            // ✅ Cập nhật khuyến mãi cũ
            promotion = existingPromotion.get();
        } else {
            // ✅ Tạo mới
            promotion = new Promotions();
            promotion.setPhone(phone);
        }

        promotion.setDiscountPercent(discountPercent);
        promotion.setQuantity(quantity);

        if (startTime != null && !startTime.isEmpty()) {
            promotion.setStartTime(LocalDateTime.parse(startTime));
        }
        if (endTime != null && !endTime.isEmpty()) {
            promotion.setEndTime(LocalDateTime.parse(endTime));
        }

        promotion.setRepeatIntervalDays(repeatIntervalDays);
        promotion.setStatus(status);

        promotionsService.savePromotion(promotion);

        return "redirect:/phones/index_Admin";
    }

    @GetMapping("/discount/delete/{id}")
    public String deletePromotion(@PathVariable Long id) {
        promotionsService.deletePromotion(id);
        return "redirect:/phones/index_Admin";
    }

    @GetMapping("/voucher/add")
    public String addVoucherForm(Model model) {
        model.addAttribute("voucher", new DiscountVoucher());
        return "admins/add_voucher"; // dùng folder "admins" giống các trang admin khác
    }

    @PostMapping("/voucher/save")
    public String saveVoucher(@ModelAttribute("voucher") DiscountVoucher voucher) {
        // Nếu bạn dùng field expiredAt (LocalDateTime) từ form datetime-local, đảm bảo
        // form bind đúng kiểu hoặc xử lý chuyển chuỗi -> LocalDateTime ở đây nếu cần.
        voucherService.save(voucher);
        // Sau khi lưu, chuyển về trang admin chính (nơi bạn list vouchers trong
        // index_Admin)
        return "redirect:/phones/index_Admin";
    }

    // ✅ Hiển thị form sửa voucher
    @GetMapping("/voucher/edit/{id}")
    public String editVoucherForm(@PathVariable Long id, Model model) {
        DiscountVoucher voucher = voucherService.getById(id);
        if (voucher == null) {
            return "redirect:/phones/index_Admin";
        }
        model.addAttribute("voucher", voucher);
        return "admins/edit_voucher"; // file HTML
    }

    // ✅ Lưu thay đổi của voucher
    @PostMapping("/voucher/update")
    public String updateVoucher(@ModelAttribute("voucher") DiscountVoucher voucher) {
        voucherService.save(voucher); // save() = update nếu có ID
        return "redirect:/phones/index_Admin";
    }

    // ✅ Xóa voucher
    @GetMapping("/voucher/delete/{id}")
    public String deleteVoucher(@PathVariable Long id) {
        voucherService.delete(id);
        return "redirect:/phones/index_Admin";
    }
    
    // thu thập voucher
    @GetMapping("/voucher/collect/{id}")
    public String collectVoucher(
            @PathVariable("id") Long voucherId,
            HttpSession session,
            RedirectAttributes ra
    ) {
        Integer userId = (Integer) session.getAttribute("idUser");

        // ❌ Chưa đăng nhập
        if (userId == null) {
            ra.addFlashAttribute("error", "Bạn phải đăng nhập để thu thập voucher.");
            return "redirect:/auth/login";
        }
        if(voucherId==null)
        {
            ra.addFlashAttribute("error", "Thu thập voucher thất bại.");
            return "redirect:/phones/index_User";
        }

        // ❌ Voucher không tồn tại
        DiscountVoucher voucher = voucherService.getById(voucherId);
        if (voucher == null) {
            ra.addFlashAttribute("error", "Voucher không tồn tại.");
            return "redirect:/phones/index_User";
        }

        if(voucherService.isVoucherValidForUse(voucherId)==false)
        {
            ra.addFlashAttribute("error", "Voucher đã dừng lại .");
            return "redirect:/phones/index_User";
        }

        // ❌ Người dùng đã thu thập rồi
        if (userVoucherService.hasCollected(userId, voucherId)) {
            ra.addFlashAttribute("error", "Bạn đã thu thập voucher này rồi!");
            return "redirect:/phones/index_User";
        }

        // ✅ Lưu vào bảng user_voucher
        userVoucherService.collectVoucher(userId, voucherId);

        ra.addFlashAttribute("success", "Thu thập voucher thành công!");

        return "redirect:/phones/index_User";
        
    }

     // ✅ Thêm vật phẩm vào trong giỏ hàng 
    @PostMapping("/addCarIdUser/{id_phone}")
    public String addCarIdUser(@PathVariable long id_phone, Model model, HttpSession session,RedirectAttributes redirectAttributes) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        if (idUser == null) {
            return "redirect:/auth/login";
        } 
        Map<String, Object> phoneInfo = phoneService.getPhoneWithPromotionById(id_phone);

        if (phoneInfo == null || phoneInfo.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm!");
            return "redirect:/phones/index_User";
        }

        Integer stock = (Integer) phoneInfo.get("StockPhone");
        if (stock == null || stock <= 0) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm hết hàng!");
            return "redirect:/phones/index_User";
        }

        // ✅ Lấy trạng thái
        String status = (String) phoneInfo.get("StatusPhone");
        if (status != null && status.equalsIgnoreCase("inactive")) {
            redirectAttributes.addFlashAttribute("error", "Sản phẩm đã ngừng kinh doanh!");
            return "redirect:/phones/index_User";
        }
        int idPhone = (int)id_phone;
        cartService.addItemToCart(idUser,idPhone);
        redirectAttributes.addFlashAttribute("success", "Thêm sản phẩm vào giỏ hàng thành công!");
        return "redirect:/phones/index_User";
    }

    @PostMapping("/deleteCarIdUser/{id_phone}") 
    public String deleteCarIdUser(@PathVariable("id_phone") Long id_phone, 
                                HttpSession session,
                                RedirectAttributes redirectAttributes) { 
        Integer idUser = (Integer) session.getAttribute("idUser");
        if (idUser == null) {
            return "redirect:/auth/login";
        } 

        try {
            int idPhone = id_phone.intValue(); // Cách chuyển đổi an toàn hơn
            cartService.removeItemFromCart(idUser, idPhone);
            
            // Gửi thông báo thành công về trang được chuyển hướng
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng.");

        } catch (RuntimeException e) {
            // Bắt lỗi từ service (ví dụ: sản phẩm không có trong giỏ) và thông báo
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());

        } catch (Exception e) {
            // Bắt các lỗi chung khác
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa sản phẩm.");
        }
        
        // ✅ THAY ĐỔI: Chuyển hướng về trang giỏ hàng sẽ hợp lý hơn
        return "redirect:/orders/carts_list_User_view/" + idUser;
    }
    // @GetMapping("/api/statistics/weekly")
    // public Map<String, Object> getWeeklyStatistics() {
    //     return statisticalService.getWeeklyStats();
    // }
    // @GetMapping("/api/statistics/weekly")
    // @ResponseBody
    // public Map<String, Object> getWeeklyStatistics(
    //         @RequestParam(required = false) String startDate,
    //         @RequestParam(required = false) String endDate) {

    //     LocalDateTime start, end;

    //     if (startDate != null && endDate != null) {
    //         start = LocalDateTime.parse(startDate + "T00:00:00");
    //         end = LocalDateTime.parse(endDate + "T23:59:59");
    //     } else {
    //         // Nếu không truyền, lấy tuần hiện tại
    //         LocalDateTime now = LocalDateTime.now();
    //         start = now.minusDays(6).withHour(0).withMinute(0).withSecond(0);
    //         end = now.withHour(23).withMinute(59).withSecond(59);
    //     }

    //     return statisticalService.getStatsByWeek(start, end);
    // }

    @GetMapping("/api/statistics/Revenue")
    @ResponseBody
    public Map<String, Object> getWeeklyRevenue(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDateTime start, end;

        if (startDate != null && endDate != null) {
            start = LocalDateTime.parse(startDate + "T00:00:00");
            end = LocalDateTime.parse(endDate + "T23:59:59");
        } else {
            // Nếu không truyền, lấy 7 ngày gần nhất
            LocalDateTime now = LocalDateTime.now();
            start = now.minusDays(6).withHour(0).withMinute(0).withSecond(0);
            end = now.withHour(23).withMinute(59).withSecond(59);
        }

        // Gọi service rút gọn chỉ lấy labels và doanh thu
        return statisticalService.getRevenueByDay(start, end);
    }
    @GetMapping("/statistical")
    public String showStatisticalPage(HttpSession session) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        User user = (User) session.getAttribute("user");
        if (idUser != null && user != null && "admin".equals(user.getRole())) {
        } else {
            // Nếu không đăng nhập HOẶC đã đăng nhập nhưng không phải admin
            return "redirect:/auth/login";
        }
        return "/admins/statistical"; // sẽ map tới templates/statistical.html
    }
    // @GetMapping("/salesChart")
    // public String salesChart() {
    //     return "admins/salesChart"; // Thymeleaf tự tìm admins/salesChart.html
    // }

    @GetMapping("/salesByProduct")
    public String salesByProduct(HttpSession session) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        User user = (User) session.getAttribute("user");
        if (idUser != null && user != null && "admin".equals(user.getRole())) {
        } else {
            // Nếu không đăng nhập HOẶC đã đăng nhập nhưng không phải admin
            return "redirect:/auth/login";
        }
        return "admins/salesByProduct"; // Thymeleaf tự tìm admins/salesByProduct.html
    }
    @GetMapping("/api/statistics/RevenueForProduct")
    @ResponseBody
    public Map<String, Object> getWeeklyRevenueForProduct(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDateTime start, end;

        if (startDate != null && endDate != null) {
            start = LocalDateTime.parse(startDate + "T00:00:00");
            end = LocalDateTime.parse(endDate + "T23:59:59");
        } else {
            // Nếu không truyền, lấy 7 ngày gần nhất
            LocalDateTime now = LocalDateTime.now();
            start = now.minusDays(6).withHour(0).withMinute(0).withSecond(0);
            end = now.withHour(23).withMinute(59).withSecond(59);
        }

        // Gọi service rút gọn chỉ lấy labels và doanh thu
        return statisticalService.getRevenueByDayForProduct(start, end);
    }
    @GetMapping("/salesChartProduct")
    public String salesChartProduct(HttpSession session) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        User user = (User) session.getAttribute("user");
        if (idUser != null && user != null && "admin".equals(user.getRole())) {
        } else {
            // Nếu không đăng nhập HOẶC đã đăng nhập nhưng không phải admin
            return "redirect:/auth/login";
        }
        return "admins/salesChartProduct"; // Thymeleaf tự tìm admins/salesByProduct.html
    }

    @GetMapping("/api/statistics/Top5RevenueForProduct")
    @ResponseBody
    public Map<String, Object> getWeeklyTop5RevenueForProduct(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDateTime start, end;

        if (startDate != null && endDate != null) {
            start = LocalDateTime.parse(startDate + "T00:00:00");
            end = LocalDateTime.parse(endDate + "T23:59:59");
        } else {
            // Nếu không truyền, lấy 7 ngày gần nhất
            LocalDateTime now = LocalDateTime.now();
            start = now.minusDays(6).withHour(0).withMinute(0).withSecond(0);
            end = now.withHour(23).withMinute(59).withSecond(59);
        }

        // Gọi service rút gọn chỉ lấy labels và doanh thu
        return statisticalService.getTop5ProductsByRevenue(start, end);
    }
    @GetMapping("/salesTop5ChartProduct")
    public String salesTop5ChartProduct(HttpSession session) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        User user = (User) session.getAttribute("user");
        if (idUser != null && user != null && "admin".equals(user.getRole())) {
        } else {
            // Nếu không đăng nhập HOẶC đã đăng nhập nhưng không phải admin
            return "redirect:/auth/login";
        }
        return "admins/salesTop5ChartProduct";
    }

    @GetMapping("/api/statistics/Top5BestSellingForProduct")
    @ResponseBody
    public Map<String, Object> getWeeklyTop5BestSellingForProduct(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDateTime start, end;

        if (startDate != null && endDate != null) {
            start = LocalDateTime.parse(startDate + "T00:00:00");
            end = LocalDateTime.parse(endDate + "T23:59:59");
        } else {
            // Nếu không truyền, lấy 7 ngày gần nhất
            LocalDateTime now = LocalDateTime.now();
            start = now.minusDays(6).withHour(0).withMinute(0).withSecond(0);
            end = now.withHour(23).withMinute(59).withSecond(59);
        }

        // Gọi service rút gọn chỉ lấy labels và doanh thu
        return statisticalService.getTop5BestSellingProducts(start, end);
    }
    @GetMapping("/salesTop5BestSellingProduct")
    public String salesTop5BestSellingProduct(HttpSession session) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        User user = (User) session.getAttribute("user");
        if (idUser != null && user != null && "admin".equals(user.getRole())) {
        } else {
            // Nếu không đăng nhập HOẶC đã đăng nhập nhưng không phải admin
            return "redirect:/auth/login";
        }
        return "admins/salesTop5BestSellingProduct";
    }

    @GetMapping("/api/statistics/Top5BestSellingVoucher")
    @ResponseBody
    public Map<String, Object> getWeeklyTop5BestSellingVoucher(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDateTime start, end;

        if (startDate != null && endDate != null) {
            start = LocalDateTime.parse(startDate + "T00:00:00");
            end = LocalDateTime.parse(endDate + "T23:59:59");
        } else {
            // Nếu không truyền, lấy 7 ngày gần nhất
            LocalDateTime now = LocalDateTime.now();
            start = now.minusDays(6).withHour(0).withMinute(0).withSecond(0);
            end = now.withHour(23).withMinute(59).withSecond(59);
        }

        return statisticalService.getCountVoucherUsageByUser(start, end);
    }
    @GetMapping("/salesTop5BestSellingVoucher")
    public String salesTop5BestSellingvoucher(HttpSession session) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        User user = (User) session.getAttribute("user");
        if (idUser != null && user != null && "admin".equals(user.getRole())) {
        } else {
            // Nếu không đăng nhập HOẶC đã đăng nhập nhưng không phải admin
            return "redirect:/auth/login";
        }
        return "admins/salesTop5BestSellingVoucher";
    }

    @GetMapping("/api/statistics/totalStatistics")
    @ResponseBody
    public Map<String, Object> getWeeklyTotalStatistics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {

        LocalDateTime start, end;

        if (startDate != null && endDate != null) {
            start = LocalDateTime.parse(startDate + "T00:00:00");
            end = LocalDateTime.parse(endDate + "T23:59:59");
        } else {
            // Nếu không truyền, lấy 7 ngày gần nhất
            LocalDateTime now = LocalDateTime.now();
            start = now.minusDays(6).withHour(0).withMinute(0).withSecond(0);
            end = now.withHour(23).withMinute(59).withSecond(59);
        }

        return statisticalService.getTotalStatistics(start, end);
    }
    @GetMapping("/totalStatistics")
    public String TotalStatistics(HttpSession session) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        User user = (User) session.getAttribute("user");
        if (idUser != null && user != null && "admin".equals(user.getRole())) {
        } else {
            // Nếu không đăng nhập HOẶC đã đăng nhập nhưng không phải admin
            return "redirect:/auth/login";
        }
        return "admins/salesChart";
    }
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("contactMessage", new ContactMessage());
        return "fragments/contact";
    }

    @PostMapping("/send")
    public String sendContactMessage(@ModelAttribute("contactMessage") ContactMessage contactMessage,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Lưu vào DB
            contactService.save(contactMessage);
            
            // Thông báo thành công (hiển thị sau khi redirect)
            redirectAttributes.addFlashAttribute("successMessage", "Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi sớm nhất.");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra, vui lòng thử lại sau!");
        }

        // Redirect lại trang contact để tránh việc user F5 lại bị gửi lặp lại (Post-Redirect-Get pattern)
        return "redirect:/phones/contact";
    }

    @GetMapping("/deleteContact/{id}")
    public String deleteContact(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            // Gọi hàm xóa mềm bên Service
            contactService.softDeleteContact(id);
            
            // Thông báo thành công (nếu muốn hiển thị bên view)
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa tin nhắn thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi xóa.");
        }
        
        // Quay lại trang danh sách
        return "redirect:/phones/index_Admin";
    }

    @GetMapping("/updateContactStatus/{id}")
    public String updateContactStatus(@PathVariable("id") Integer id, 
                                    @RequestParam("status") Integer newStatus, 
                                    RedirectAttributes redirectAttributes) {
        try {
            // Gọi hàm updateStatus bên Service
            contactService.updateStatus(id, newStatus);
            
            // Thông báo tùy theo trạng thái
            String msg = "";
            if(newStatus == 1) msg = "Đã chuyển sang trạng thái: Đã xem";
            else if(newStatus == 2) msg = "Đã chuyển sang trạng thái: Đã xử lý";
            else msg = "Đã cập nhật trạng thái";

            redirectAttributes.addFlashAttribute("successMessage", msg);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật trạng thái.");
        }
        
        // Quay lại trang danh sách
        return "redirect:/phones/index_Admin";
    }


    @GetMapping("/chatbot")
    public String trangChat() {
        return "chat"; 
    }

}
