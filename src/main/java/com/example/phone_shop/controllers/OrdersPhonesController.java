package com.example.phone_shop.controllers;

// import com.example.phone_shop.models.Cart;
import com.example.phone_shop.models.OrdersPhones;
import com.example.phone_shop.models.OrdersPhones.OrderStatus;
import com.example.phone_shop.models.Phone;
import com.example.phone_shop.models.Promotions;
import com.example.phone_shop.models.Review;
import com.example.phone_shop.services.CartService;
import com.example.phone_shop.services.OrdersPhonesService;
import com.example.phone_shop.services.PhoneService;
import com.example.phone_shop.services.PromotionsService;
import com.example.phone_shop.services.ReviewService;
import com.example.phone_shop.services.UserService;
import com.example.phone_shop.services.UserVoucherService;
import com.example.phone_shop.services.DiscountVoucherService;

// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;
import com.example.phone_shop.models.User;
import com.example.phone_shop.models.UserVoucher;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.math.BigDecimal;
import java.util.ArrayList;
// import java.util.Arrays;
// import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;



@Controller
@RequestMapping("/orders")
public class OrdersPhonesController {
    private final OrdersPhonesService service;
    private final PhoneService phoneService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final CartService cartService;
    private final PromotionsService promotionsService;
    private final UserVoucherService userVoucherService;
    private final DiscountVoucherService discountVoucherService;

    public OrdersPhonesController(OrdersPhonesService service, PhoneService phoneService, UserService userService,
            ReviewService reviewService, CartService cartService, PromotionsService promotionsService, UserVoucherService userVoucherService,DiscountVoucherService discountVoucherService) {
        this.service = service;
        this.phoneService = phoneService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.cartService = cartService;
        this.promotionsService = promotionsService;
        this.userVoucherService =userVoucherService;
        this.discountVoucherService= discountVoucherService;
    }

    @GetMapping("/orders-phones")
    public String getOrdersPhonesPage(Model model) {
        List<OrdersPhones> ordersPhonesList = service.getAllOrdersPhones();
        model.addAttribute("ordersPhones", ordersPhonesList);
        return "orders/orders_phones";
    }

    // Hiển thị trang mua nhiều điện thoại
    @GetMapping("/buylist")
    public String orderBuyPhones(@RequestParam List<Long> ids, HttpSession session, Model model,
                                RedirectAttributes redirectAttributes) {
        if (ids == null || ids.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không có sản phẩm nào được chọn!");
            return "redirect:/phones/index_User";
        }

        // ✅ Lấy danh sách điện thoại với thông tin khuyến mãi
        List<Map<String, Object>> phonesInOrder = new ArrayList<>();
        for (Long id : ids) {
            Map<String, Object> phoneInfo = phoneService.getPhoneWithPromotionById(id);
            if (phoneInfo == null || phoneInfo.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm với ID: " + id);
                return "redirect:/phones/index_User";
            }

            Integer stock = (Integer) phoneInfo.get("StockPhone");
            if (stock == null || stock <= 0) {
                redirectAttributes.addFlashAttribute("error", "Sản phẩm " + phoneInfo.get("NamePhoneId") + " đã hết hàng!");
                return "redirect:/phones/index_User";
            }

            // ✅ Lấy trạng thái
            String status = (String) phoneInfo.get("StatusPhone");
            if (status != null && status.equalsIgnoreCase("inactive")) {
                redirectAttributes.addFlashAttribute("error", "Sản phẩm " + phoneInfo.get("NamePhoneId") + " đã ngừng kinh doanh!");
                return "redirect:/phones/index_User";
            }

            phonesInOrder.add(phoneInfo);
        }

        model.addAttribute("phonesInOrder", phonesInOrder);

        
        // ✅ Lấy thông tin user
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("user", user);
        Long idUserLong = user.getIdUser(); 
        Integer idUserInt = idUserLong.intValue();
        List<Map<String, Object>> userVouchersinformation =userVoucherService.getgetUserVouchersDiscount(idUserInt);
        model.addAttribute("userVouchersinformation", userVouchersinformation);
        discountVoucherService.deactivateExpiredOrOutOfStockVouchers();
        return "orders/buy_phonelist"; // View hiển thị mua nhiều điện thoại
    }


    // Hiển thị trang mua điện thoại
    @GetMapping("/buy/{id}")
    public String orderBuyPhone(@PathVariable Long id, HttpSession session, Model model,
            RedirectAttributes redirectAttributes) {
                
        Map<String, Object> phoneInfo = phoneService.getPhoneWithPromotionById(id);

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

        model.addAttribute("phoneInfo", phoneInfo);

        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        model.addAttribute("user", user);
        Long idUserLong = user.getIdUser(); 
        Integer idUserInt = idUserLong.intValue();
        List<Map<String, Object>> userVouchersinformation =userVoucherService.getgetUserVouchersDiscount(idUserInt);
        model.addAttribute("userVouchersinformation", userVouchersinformation);
        discountVoucherService.deactivateExpiredOrOutOfStockVouchers();
        return "orders/buy_phone";
    }


    @PostMapping("/confirm")
    public String confirmOrder(
            @RequestParam long phoneId,
            @RequestParam Integer quantity,
            @RequestParam(required = false) Long voucherId, // Thêm tham số voucherId, không bắt buộc
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        // 1. KIỂM TRA ĐĂNG NHẬP VÀ USER (Giữ nguyên)
        Integer idUser = (Integer) session.getAttribute("idUser");
        if (idUser == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để tiếp tục!");
            return "redirect:/auth/login";
        }
        User user = userService.findById(Long.valueOf(idUser));
        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản!");
            return "redirect:/auth/login";
        }

        // 2. LẤY THÔNG TIN SẢN PHẨM VÀ KIỂM TRA TỒN KHO (Giữ nguyên)
        Map<String, Object> phoneInfo = phoneService.getPhoneWithPromotionById(phoneId);
        if (phoneInfo == null || phoneInfo.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm!");
            return "redirect:/phones/index_User";
        }
        Optional<Phone> phoneOptional = phoneService.getPhoneById(phoneId);
        if (phoneOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm!");
            return "redirect:/phones/index_User";
        }
        Phone phone = phoneOptional.get();
        int stock = phone.getStock();
        if (quantity < 1 || quantity > stock) {
            redirectAttributes.addFlashAttribute("error", "Số lượng không hợp lệ!");
            return "redirect:/orders/buy/" + phoneId;
        }
        Long idVoucher=null;
        if(voucherId!=null&&voucherId>0)
        {
           UserVoucher UserVoucherId=userVoucherService.getUserVoucherId(idUser,voucherId);
            if(UserVoucherId!=null)
            {
                idVoucher=UserVoucherId.getVoucherId();
            } 
        }
        if(idVoucher!=null)
        {
            if(discountVoucherService.isVoucherValidForUse(idVoucher)==false)
            {
                redirectAttributes.addFlashAttribute("error", "Voucher không hợp lệ !");
                return "redirect:/orders/buy/" + phoneId;
            }
        }

        // === PHẦN LOGIC ĐƯỢC ĐIỀU CHỈNH ĐỂ KHỚP VỚI JAVASCRIPT ===

        // 3. TÍNH TỔNG TIỀN HÀNG TRƯỚC KHI ÁP VOUCHER (itemBaseTotal)
        String status = (String) phoneInfo.get("StatusDiscountPhone");
        double priceOriginal = ((Number) phoneInfo.get("PricePhone")).doubleValue();
        double priceFinal = phoneInfo.get("FinalPrice") != null ? ((Number) phoneInfo.get("FinalPrice")).doubleValue() : priceOriginal;
        Integer discountQty = phoneInfo.get("QuantityPhone") != null ? ((Number) phoneInfo.get("QuantityPhone")).intValue() : 0; // Mặc định là 0 nếu null

        boolean isPromo = "active".equalsIgnoreCase(status) && priceFinal < priceOriginal;

        double itemBaseTotal;
        int discountUsedCount = 0; // Số lượng sản phẩm được áp dụng giá khuyến mãi

        if (isPromo && discountQty > 0 && quantity > discountQty) {
            // Mua nhiều hơn số lượng KM có hạn -> tính giá riêng cho phần KM và phần thường
            itemBaseTotal = (discountQty * priceFinal) + ((quantity - discountQty) * priceOriginal);
            discountUsedCount = discountQty;
        } else {
            // Mua trong giới hạn KM / KM không giới hạn / hoặc không có KM
            double priceToUse = isPromo ? priceFinal : priceOriginal;
            itemBaseTotal = quantity * priceToUse;
            if (isPromo) {
                discountUsedCount = quantity;
            }
        }

        // 4. XỬ LÝ VOUCHER VÀ TÍNH TOÁN GIẢM GIÁ
        double voucherDiscount = 0.0;
        Long validVoucherId = null; // Biến tạm để xác định voucher có hợp lệ và được áp dụng không
        if (voucherId != null && voucherId > 0) {
            List<Map<String, Object>> voucherDetailsList = userVoucherService.getgetUserVouchersDiscountProduct(idUser, voucherId);
            if (voucherDetailsList != null && !voucherDetailsList.isEmpty()) {
                Map<String, Object> voucherInfo = voucherDetailsList.get(0);

                boolean isUsed = (boolean) voucherInfo.get("isUsed");
                String voucherStatus = (String) voucherInfo.get("status");
                double minOrderValue = ((Number) voucherInfo.get("minOrderValue")).doubleValue();

                // Điều kiện áp dụng: voucher hợp lệ VÀ tổng tiền tạm tính >= giá trị đơn tối thiểu
                if (!isUsed && "active".equalsIgnoreCase(voucherStatus) && itemBaseTotal >= minOrderValue) {
                    double discountPercent = ((Number) voucherInfo.get("discountPercent")).doubleValue();
                    double maxDiscount = ((Number) voucherInfo.get("maxDiscount")).doubleValue();
                    String voucherType = (String) voucherInfo.get("voucherType");

                    double calculatedDiscount;
                    
                    if ("product".equalsIgnoreCase(voucherType)) {
                        // Loại 'product': Chỉ tính giảm giá trên giá của MỘT sản phẩm
                        double singleItemPrice = isPromo ? priceFinal : priceOriginal;
                        calculatedDiscount = singleItemPrice * (discountPercent / 100.0);
                    } else { 
                        // Loại 'order' (hoặc loại khác): Tính trên TỔNG giá trị đơn hàng
                        calculatedDiscount = itemBaseTotal * (discountPercent / 100.0);
                    }

                    voucherDiscount = Math.min(calculatedDiscount, maxDiscount);

                    // Nếu có giảm giá thực sự, ghi nhận ID voucher này là hợp lệ
                    if (voucherDiscount > 0) {
                        validVoucherId = voucherId;
                    }
                }
            }
        }

        // 5. TÍNH GIÁ CUỐI CÙNG VÀ LƯU ĐƠN HÀNG
        double finalPrice = itemBaseTotal - voucherDiscount;

        OrdersPhones order = new OrdersPhones();
        order.setUserId(idUser);
        order.setPhoneId((int) phoneId);
        order.setQuantity(quantity);
        order.setPrice(BigDecimal.valueOf(finalPrice));
        order.setStatus(OrderStatus.ORDERED);
        order.setUsedDiscount(discountUsedCount);

        service.saveOrder(order);

        // 6. CẬP NHẬT TỒN KHO SẢN PHẨM VÀ SỐ LƯỢNG KHUYẾN MÃI (Giữ nguyên)
        phone.setStock(stock - quantity);
        phoneService.updatePhone(phone);

        if (isPromo && discountUsedCount > 0) {
            Optional<Promotions> promoOpt = promotionsService.getPromotionsByPhoneId(phoneId);
            if (promoOpt.isPresent()) {
                Promotions promo = promoOpt.get();
                if (promo.getQuantity() != null) {
                    int newQuantity = Math.max(0, promo.getQuantity() - discountUsedCount);
                    promo.setQuantity(newQuantity);
                    if (newQuantity == 0) {
                        promo.setStatus("inactive");
                    }
                    promotionsService.savePromotion(promo);
                }
            }
        }

        // 7. ĐÁNH DẤU VOUCHER LÀ ĐÃ SỬ DỤNG
        // Chỉ đánh dấu đã dùng nếu voucher đã được xác thực là hợp lệ
        if (validVoucherId != null) {
            userVoucherService.markVoucherUsed(idUser, validVoucherId);
        }
        if(idVoucher!=null)
        {
            discountVoucherService.decreaseQuantity(idVoucher);
        }

        redirectAttributes.addFlashAttribute("success", "Đặt hàng thành công!");
        return "redirect:/phones/index_User";
    }
    @Transactional 
    @PostMapping("/confirmMultiple")
    public String confirmMultipleOrders(
        @RequestParam long[] phoneId,       // Thay đổi thành mảng
        @RequestParam Integer[] quantity,   // Thay đổi thành mảng
        @RequestParam(required = false) Long[] voucherId, // Thay đổi thành mảng
        HttpSession session,
        RedirectAttributes redirectAttributes) {

    // 1. KIỂM TRA ĐĂNG NHẬP VÀ USER (Chỉ cần làm một lần)
    Integer idUser = (Integer) session.getAttribute("idUser");
    if (idUser == null) {
        redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập để tiếp tục!");
        return "redirect:/auth/login";
    }
    User user = userService.findById(Long.valueOf(idUser));
    if (user == null) {
        redirectAttributes.addFlashAttribute("error", "Không tìm thấy tài khoản!");
        return "redirect:/auth/login";
    }
    
    // Đảm bảo các mảng có cùng độ dài
    if(voucherId.length!=0)
    {
        if (phoneId.length != quantity.length || (  phoneId.length != voucherId.length  )) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu đơn hàng không đồng bộ!");
            return "redirect:/phones/index_User"; // Hoặc trang giỏ hàng
        }
    }else{
        if (phoneId.length != quantity.length) {
            redirectAttributes.addFlashAttribute("error", "Dữ liệu đơn hàng không đồng bộ!");
            return "redirect:/phones/index_User"; // Hoặc trang giỏ hàng
        }
    }
    

    // Bắt đầu vòng lặp để xử lý từng sản phẩm
    for (int i = 0; i < phoneId.length; i++) {
        long currentPhoneId = phoneId[i];
        int currentQuantity = quantity[i];
        // Nếu voucherId[i] là null (do không chọn voucher), nó sẽ vẫn là null
        Long currentVoucherId=null;
        if(voucherId.length!=0)
        {
            currentVoucherId = (voucherId[i] != null && voucherId[i] > 0) ? voucherId[i] : null;
        }
        Long idVoucher=null;
        if(currentVoucherId!=null)
        {
           UserVoucher UserVoucherId=userVoucherService.getUserVoucherId(idUser,currentVoucherId);
            if(UserVoucherId!=null)
            {
                idVoucher=UserVoucherId.getVoucherId();
            } 
        }
        if(idVoucher!=null)
        {
            if(discountVoucherService.isVoucherValidForUse(idVoucher)==false)
            {
                redirectAttributes.addFlashAttribute("error", "Voucher không hợp lệ !");
                return "redirect:/phones/index_User";
            }
        }
        

        // --- BẮT ĐẦU SAO CHÉP LOGIC CŨ VÀO ĐÂY ---

        // 2. LẤY THÔNG TIN SẢN PHẨM VÀ KIỂM TRA TỒN KHO
        Map<String, Object> phoneInfo = phoneService.getPhoneWithPromotionById(currentPhoneId);
        Optional<Phone> phoneOptional = phoneService.getPhoneById(currentPhoneId);

        if (phoneOptional.isEmpty() || phoneInfo == null || phoneInfo.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy sản phẩm với ID: " + currentPhoneId);
            return "redirect:/orders/checkout"; // Quay lại trang thanh toán
        }
        
        Phone phone = phoneOptional.get();
        int stock = phone.getStock();
        if (currentQuantity < 1 || currentQuantity > stock) {
            redirectAttributes.addFlashAttribute("error", "Số lượng không hợp lệ hoặc hết hàng cho sản phẩm: " + phone.getName());
            return "redirect:/orders/checkout";
        }

        // 3. TÍNH TỔNG TIỀN HÀNG
        String status = (String) phoneInfo.get("StatusDiscountPhone");
        double priceOriginal = ((Number) phoneInfo.get("PricePhone")).doubleValue();
        double priceFinal = phoneInfo.get("FinalPrice") != null ? ((Number) phoneInfo.get("FinalPrice")).doubleValue() : priceOriginal;
        Integer discountQty = phoneInfo.get("QuantityPhone") != null ? ((Number) phoneInfo.get("QuantityPhone")).intValue() : 0;
        boolean isPromo = "active".equalsIgnoreCase(status) && priceFinal < priceOriginal;
        double itemBaseTotal;
        int discountUsedCount = 0;

        if (isPromo && discountQty > 0 && currentQuantity > discountQty) {
            itemBaseTotal = (discountQty * priceFinal) + ((currentQuantity - discountQty) * priceOriginal);
            discountUsedCount = discountQty;
        } else {
            double priceToUse = isPromo ? priceFinal : priceOriginal;
            itemBaseTotal = currentQuantity * priceToUse;
            if (isPromo) {
                discountUsedCount = currentQuantity;
            }
        }

        // 4. XỬ LÝ VOUCHER
        double voucherDiscount = 0.0;
        Long validVoucherId = null;

        if (currentVoucherId != null) {
            List<Map<String, Object>> voucherDetailsList = userVoucherService.getgetUserVouchersDiscountProduct(idUser, currentVoucherId);
            if (voucherDetailsList != null && !voucherDetailsList.isEmpty()) {
                Map<String, Object> voucherInfo = voucherDetailsList.get(0);
                boolean isUsed = (boolean) voucherInfo.get("isUsed");
                String voucherStatus = (String) voucherInfo.get("status");
                double minOrderValue = ((Number) voucherInfo.get("minOrderValue")).doubleValue();

                if (!isUsed && "active".equalsIgnoreCase(voucherStatus) && itemBaseTotal >= minOrderValue) {
                    double discountPercent = ((Number) voucherInfo.get("discountPercent")).doubleValue();
                    double maxDiscount = ((Number) voucherInfo.get("maxDiscount")).doubleValue();
                    String voucherType = (String) voucherInfo.get("voucherType");
                    double calculatedDiscount;
                    
                    if ("product".equalsIgnoreCase(voucherType)) {
                        double singleItemPrice = isPromo ? priceFinal : priceOriginal;
                        calculatedDiscount = singleItemPrice * (discountPercent / 100.0);
                    } else { 
                        calculatedDiscount = itemBaseTotal * (discountPercent / 100.0);
                    }
                    voucherDiscount = Math.min(calculatedDiscount, maxDiscount);
                    if (voucherDiscount > 0) {
                        validVoucherId = currentVoucherId;
                    }
                }
            }
        }

        // 5. TÍNH GIÁ CUỐI CÙNG VÀ LƯU ĐƠN HÀNG
        double finalPrice = itemBaseTotal - voucherDiscount;
        OrdersPhones order = new OrdersPhones();
        order.setUserId(idUser);
        order.setPhoneId((int) currentPhoneId);
        order.setQuantity(currentQuantity);
        order.setPrice(BigDecimal.valueOf(finalPrice));
        order.setStatus(OrderStatus.ORDERED);
        order.setUsedDiscount(discountUsedCount);
        service.saveOrder(order);

        // 6. CẬP NHẬT TỒN KHO SẢN PHẨM VÀ KHUYẾN MÃI
        phone.setStock(stock - currentQuantity);
        phoneService.updatePhone(phone);
        if (isPromo && discountUsedCount > 0) {
            Optional<Promotions> promoOpt = promotionsService.getPromotionsByPhoneId(currentPhoneId);
            if (promoOpt.isPresent()) {
                Promotions promo = promoOpt.get();
                if (promo.getQuantity() != null) {
                    // Giảm số lượng khuyến mãi đúng bằng số lượng đã dùng
                    int newQuantity = Math.max(0, promo.getQuantity() - discountUsedCount);
                    promo.setQuantity(newQuantity);

                    // Nếu hết khuyến mãi, chuyển trạng thái thành inactive
                    if (newQuantity == 0) {
                        promo.setStatus("inactive");
                    }
                    promotionsService.savePromotion(promo);
                }
            }
        }
       

        // 7. ĐÁNH DẤU VOUCHER LÀ ĐÃ SỬ DỤNG
        if (validVoucherId != null) {
            userVoucherService.markVoucherUsed(idUser, validVoucherId);
        }
        if(idVoucher!=null)
        {
            discountVoucherService.decreaseQuantity(idVoucher);
        }
        // 8 - XÓA SẢN PHẨM KHỎI GIỎ HÀNG
        try {
            cartService.removeItemFromCart(idUser, (int) currentPhoneId);
        } catch (Exception e) {
            System.err.println("Lỗi khi xóa sản phẩm ID " + currentPhoneId + " khỏi giỏ hàng: " + e.getMessage());
            throw new RuntimeException("Không thể xóa sản phẩm khỏi giỏ hàng. Giao dịch sẽ được hoàn tác.", e);
        }

    } // Kết thúc vòng lặp for

    // Nếu vòng lặp chạy xong mà không có lỗi, chuyển hướng với thông báo thành công
    redirectAttributes.addFlashAttribute("success", "Đặt hàng thành công!");
    return "redirect:/phones/index_User";
}

    @GetMapping("/orders_list_User/{userId}")
    public String getOrdersByUserId(@PathVariable("userId") int userId, HttpSession session, Model model) {

        User user = userService.findById(Long.valueOf(userId));
        // if(user==null)
        // {
        // return "redirect:/auth/login";
        // }
        model.addAttribute("user", user);

        Integer idUser = (Integer) session.getAttribute("idUser");

        if (idUser == null) {
            return "redirect:/auth/login";
        }

        List<OrdersPhones> orders = service.getOrdersByUserId(userId);
        model.addAttribute("orders", orders);

        List<Map<String, Object>> orderDetails = service.getOrdersWithPhoneDetailsByUserIdIsVisibleToUser(userId);
        model.addAttribute("orderDetails", orderDetails);

        return "orders/orders_list_user"; // Trả về trang Thymeleaf "orders_list_user.html"
    }

    @GetMapping("/carts_list_User_view/{userId}")
    public String getcartsByUserId1(@PathVariable("userId") int userId, HttpSession session, Model model) {
        User user = userService.findById(Long.valueOf(userId));
        if (user == null) {
            return "redirect:/auth/login";
        }


        List<Map<String, Object>> cartData = cartService.getCartsByUserId_listphone(userId);
        model.addAttribute("cartPhones", cartData);
        
        List<UserVoucher> userVouchers=userVoucherService.getUserVouchers(userId);
        model.addAttribute("userVouchers", userVouchers);

        List<Map<String, Object>> userVouchersinformation =userVoucherService.getgetUserVouchersDiscount(userId);
        model.addAttribute("userVouchersinformation", userVouchersinformation);
        discountVoucherService.deactivateExpiredOrOutOfStockVouchers();
        return "orders/carts_list_user"; // Trả về trang Thymeleaf "orders_list_user.html"
    }

    // // ✅ Hủy đơn hàng (Chuyển trạng thái ORDERED -> CANCELED)
    // @PostMapping("/cancel/{id}")
    // public String cancelOrder(@PathVariable int id, RedirectAttributes
    // redirectAttributes) {
    // Optional<OrdersPhones> orderOptional = service.getOrderById(id);
    // if (orderOptional.isPresent() && orderOptional.get().getStatus() ==
    // OrdersPhones.OrderStatus.ORDERED) {
    // OrdersPhones order = orderOptional.get();
    // order.setStatus(OrdersPhones.OrderStatus.CANCELED);
    // service.saveOrder(order);
    // redirectAttributes.addFlashAttribute("success", "Đơn hàng đã bị hủy.");
    // } else {
    // redirectAttributes.addFlashAttribute("error", "Không thể hủy đơn hàng này.");
    // }
    // return "redirect:/orders/orders_list_User/" +
    // orderOptional.get().getUserId();
    // }

    // // ✅ Xóa đơn hàng (Chỉ xóa khi trạng thái là CANCELED)
    // @PostMapping("/delete/{id}")
    // public String deleteOrder(@PathVariable int id, RedirectAttributes
    // redirectAttributes) {
    // Optional<OrdersPhones> orderOptional = service.getOrderById(id);
    // if (orderOptional.isPresent() && orderOptional.get().getStatus() ==
    // OrdersPhones.OrderStatus.CANCELED) {
    // service.deleteOrder(id);
    // redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được xóa.");
    // } else {
    // redirectAttributes.addFlashAttribute("error", "Chỉ có thể xóa đơn hàng đã
    // hủy.");
    // }
    // return "redirect:/orders/orders_list_User/" +
    // orderOptional.get().getUserId();
    // }

    // ✅ Hủy đơn hàng (Chuyển trạng thái ORDERED -> CANCELED)
    @GetMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<OrdersPhones> orderOptional = service.getOrderById(id);
        if (orderOptional.isPresent() && orderOptional.get().getStatus() == OrdersPhones.OrderStatus.ORDERED) {
            OrdersPhones order = orderOptional.get();
            order.setStatus(OrdersPhones.OrderStatus.CANCELED);
            service.saveOrder(order);
            service.Refundquantity(id);
            redirectAttributes.addFlashAttribute("success", "Đơn hàng đã bị hủy.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không thể hủy đơn hàng này.");
        }
        return "redirect:/orders/orders_list_User/" + orderOptional.get().getUserId();
    }

    // ✅ Xóa đơn hàng (Chỉ xóa khi trạng thái là CANCELED hoặc REJECTED)
    @GetMapping("/delete/{id}")
    public String deleteOrder(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<OrdersPhones> orderOptional = service.getOrderById(id);
        if (orderOptional.isPresent()) {
            OrdersPhones order = orderOptional.get();
            if (order.getStatus() == OrdersPhones.OrderStatus.CANCELED
                    || order.getStatus() == OrdersPhones.OrderStatus.REJECTED) {
                service.hideOrderForUser(id);
                redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được xóa.");
            } else {
                if(order.getStatus() == OrdersPhones.OrderStatus.SUCCESS && order.getIsReviewed()==true )
                {
                    service.hideOrderForUser(id);
                }
                else{
                   redirectAttributes.addFlashAttribute("error", "Chỉ có thể xóa đơn hàng đã hủy hoặc bị từ chối."); 
                }
                
            }
            return "redirect:/orders/orders_list_User/" + order.getUserId();
        }
        redirectAttributes.addFlashAttribute("error", "Đơn hàng không tồn tại.");
        return "redirect:/orders/orders_list_User";
    }

    // // ✅ Xác nhận đơn hàng (Chuyển trạng thái ORDERED -> CONFIRMED)
    // @GetMapping("/confirm/{id}")
    // public String confirmOrder(@PathVariable int id, RedirectAttributes
    // redirectAttributes) {
    // Optional<OrdersPhones> orderOptional = service.getOrderById(id);
    // if (orderOptional.isPresent() && orderOptional.get().getStatus() ==
    // OrdersPhones.OrderStatus.ORDERED) {
    // OrdersPhones order = orderOptional.get();
    // order.setStatus(OrdersPhones.OrderStatus.CONFIRMED);
    // service.saveOrder(order);
    // redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được xác
    // nhận.");
    // } else {
    // redirectAttributes.addFlashAttribute("error", "Chỉ có thể xác nhận đơn hàng ở
    // trạng thái ORDERED.");
    // }
    // return "redirect:/phones/index_Admin" + orderOptional.get().getUserId();
    // }
    // ✅ Từ chối đơn hàng (Chuyển trạng thái ORDERED -> REJECTED)
    // @GetMapping("/reject/{id}")
    // public String rejectOrder(@PathVariable int id, RedirectAttributes
    // redirectAttributes) {
    // Optional<OrdersPhones> orderOptional = service.getOrderById(id);
    // if (orderOptional.isPresent() && orderOptional.get().getStatus() ==
    // OrdersPhones.OrderStatus.ORDERED) {
    // OrdersPhones order = orderOptional.get();
    // order.setStatus(OrdersPhones.OrderStatus.REJECTED);
    // service.saveOrder(order);
    // redirectAttributes.addFlashAttribute("success", "Đơn hàng đã bị từ chối.");
    // } else {
    // redirectAttributes.addFlashAttribute("error", "Chỉ có thể từ chối đơn hàng ở
    // trạng thái ORDERED.");
    // }
    // return "redirect:/phones/index_Admin" + orderOptional.get().getUserId();
    // }

    // @GetMapping("/confirm/{id}")
    // public String confirmOrder(@PathVariable int id, RedirectAttributes
    // redirectAttributes) {
    // Optional<OrdersPhones> orderOptional = service.getOrderById(id);
    // if (orderOptional.isPresent() && orderOptional.get().getStatus() ==
    // OrdersPhones.OrderStatus.ORDERED) {
    // OrdersPhones order = orderOptional.get();
    // order.setStatus(OrdersPhones.OrderStatus.CONFIRMED);
    // service.saveOrder(order);
    // redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được xác
    // nhận.");
    // } else {
    // redirectAttributes.addFlashAttribute("error", "Chỉ có thể xác nhận đơn hàng ở
    // trạng thái ORDERED.");
    // }
    // return "redirect:/phones/index_Admin"; // Đảm bảo đường dẫn đúng
    // }

    @GetMapping("/confirm/{id}")
    public String confirmOrder(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<OrdersPhones> orderOptional = service.getOrderById(id);

        if (orderOptional.isPresent()) {
            OrdersPhones order = orderOptional.get();

            // Kiểm tra nếu trạng thái là ORDERED hoặc REJECTED
            if (order.getStatus() == OrdersPhones.OrderStatus.ORDERED ||
                    order.getStatus() == OrdersPhones.OrderStatus.REJECTED) {

                order.setStatus(OrdersPhones.OrderStatus.CONFIRMED);
                service.saveOrder(order);
                redirectAttributes.addFlashAttribute("success", "Đơn hàng đã được xác nhận.");
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "Chỉ có thể xác nhận đơn hàng ở trạng thái ORDERED hoặc REJECTED.");
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy đơn hàng.");
        }

        return "redirect:/phones/index_Admin"; // Đảm bảo đường dẫn đúng
    }

    @GetMapping("/reject/{id}")
    public String rejectOrder(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<OrdersPhones> orderOptional = service.getOrderById(id);

        if (orderOptional.isPresent()) {
            OrdersPhones order = orderOptional.get();

            // Kiểm tra nếu trạng thái là ORDERED hoặc CONFIRMED
            if (order.getStatus() == OrdersPhones.OrderStatus.ORDERED ||
                    order.getStatus() == OrdersPhones.OrderStatus.CONFIRMED) {

                order.setStatus(OrdersPhones.OrderStatus.REJECTED);
                service.saveOrder(order);
                service.Refundquantity(id);
                redirectAttributes.addFlashAttribute("success", "Đơn hàng đã bị từ chối.");
            } else {
                redirectAttributes.addFlashAttribute("error",
                        "Chỉ có thể từ chối đơn hàng ở trạng thái ORDERED hoặc CONFIRMED.");
            }
            return "redirect:/phones/index_Admin"; // Điều hướng đúng
        }

        redirectAttributes.addFlashAttribute("error", "Đơn hàng không tồn tại.");
        return "redirect:/phones/index_Admin";
    }

    @GetMapping("/success/{id}")
    public String successOrder(@PathVariable int id, RedirectAttributes redirectAttributes) {
        Optional<OrdersPhones> orderOptional = service.getOrderById(id);

        if (orderOptional.isPresent()) {
            OrdersPhones order = orderOptional.get();

            // Kiểm tra nếu trạng thái là ORDERED hoặc CONFIRMED
            if (order.getStatus() == OrdersPhones.OrderStatus.CONFIRMED) {

                order.setStatus(OrdersPhones.OrderStatus.SUCCESS);
                service.saveOrder(order);
                redirectAttributes.addFlashAttribute("success", "Đơn hàng đã thành công");
            } else {
                redirectAttributes.addFlashAttribute("error", "Chỉ có thể từ chối đơn hàng ở trạng thái  CONFIRMED.");
            }
            return "redirect:/phones/index_Admin"; // Điều hướng đúng
        }

        redirectAttributes.addFlashAttribute("error", "Đơn hàng không tồn tại.");
        return "redirect:/phones/index_Admin";
    }

    @GetMapping("/revieworder/{id}")
    public String showReviewForm(@PathVariable("id") int orderId, HttpSession session, Model model) {
        OrdersPhones order = service.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ Chỉ cho phép đánh giá khi đơn hàng đã hoàn thành và chưa đánh giá
        if (order.getStatus() != OrdersPhones.OrderStatus.SUCCESS || Boolean.TRUE.equals(order.getIsReviewed())) {
            // Có thể redirect về trang danh sách đơn hàng hoặc trả về thông báo lỗi
            return "redirect:/orders/orders_list_User/" + order.getUserId(); // hoặc hiển thị thông báo lỗi tùy bạn
        }

        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user); // Truyền user vào Model
        } else {
            return "redirect:/auth/login";
        }

        Optional<Phone> phoneOptional = phoneService.getPhoneById(Long.valueOf(order.getPhoneId()));
        Phone phone = phoneOptional.orElseThrow(() -> new RuntimeException("Không tìm thấy điện thoại"));
        model.addAttribute("phone", phone); // Truyền user vào Model

        Review review = new Review();
        review.setUserId(order.getUserId());
        review.setPhoneId(order.getPhoneId());

        model.addAttribute("review", review);
        model.addAttribute("orderId", orderId); // để biết cần cập nhật lại đơn
        model.addAttribute("userId", order.getUserId());

        return "review/review_form"; // Tên file HTML form đánh giá
    }

    @PostMapping("/submitreview")
    public String submitReview(@ModelAttribute Review review, @RequestParam("orderId") int orderId,
            HttpSession session) {
        // Lưu đánh giá
        reviewService.save(review);

        // Cập nhật đơn hàng đã được đánh giá
        OrdersPhones order = service.getOrderById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setIsReviewed(true);
        service.saveOrder(order);
        Integer idUser = (Integer) session.getAttribute("idUser");

        return "redirect:/orders/orders_list_User/" + idUser; // quay lại danh sách đơn
    }

}
