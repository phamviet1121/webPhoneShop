package com.example.phone_shop.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.phone_shop.models.DiscountVoucher;
import com.example.phone_shop.models.OrdersPhones;
import com.example.phone_shop.models.Phone;
import com.example.phone_shop.models.UserVoucher;

@Service
public class StatisticalService {
    private final PhoneService phoneService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final OrdersPhonesService ordersPhonesService;
    private final PromotionsService promotionsService;
    private final DiscountVoucherService discountVoucherService;
    private final UserVoucherService userVoucherService;
    private final CartService cartService;

    public StatisticalService(
    PhoneService phoneService, 
    UserService userService, 
    ReviewService reviewService,
    OrdersPhonesService ordersPhonesService,
    PromotionsService promotionsService,
    DiscountVoucherService discountVoucherService,
    UserVoucherService userVoucherService,
    CartService cartService) {
            
        this.phoneService = phoneService;
        this.userService = userService;
        this.reviewService = reviewService;
        this.ordersPhonesService = ordersPhonesService;
        this.promotionsService = promotionsService;
        this.discountVoucherService = discountVoucherService;
        this.userVoucherService = userVoucherService;
        this.cartService = cartService;

    }

    // Tổng doanh thu theo ngày, tuần, tháng, năm , toàn bộ đơn hàng 
    public double totalRevenueByDay(LocalDateTime startTime, LocalDateTime endTime) { 
        double totalDay = 0;

        List<OrdersPhones> allOrdersPhones = ordersPhonesService.getAllOrdersPhones();

        for (OrdersPhones order : allOrdersPhones) {
            LocalDateTime createdAt = order.getCreatedAt();
            LocalDateTime updatedAt = order.getUpdatedAt();

            // Chuyển BigDecimal sang double
            double priceOrder = order.getPrice().doubleValue();

            // Điều kiện đơn hàng nằm trong khoảng thời gian
            if (createdAt == null || updatedAt == null) continue;
            boolean isInRange = !createdAt.isBefore(startTime) && !updatedAt.isAfter(endTime);

            // Chỉ tính đơn hàng thành công
            if (isInRange && order.getStatus() == OrdersPhones.OrderStatus.SUCCESS) {
                totalDay += priceOrder;
            }
        }

        return totalDay;
    }
    // tổng số lượng đợn hàng đã đặt thành công 
    public int totalOrdersSuccessByDay(LocalDateTime startTime, LocalDateTime endTime)
    {
        int totalOrder=0;
        List<OrdersPhones> allOrdersPhones = ordersPhonesService.getAllOrdersPhones();

        for (OrdersPhones order : allOrdersPhones) {
            LocalDateTime createdAt = order.getCreatedAt();
            LocalDateTime updatedAt = order.getUpdatedAt();
            // Sô lượng Sản phẩm trong đơn hàng 
            int quantity=order.getQuantity();
            // Điều kiện đơn hàng nằm trong khoảng thời gian
            if (createdAt == null || updatedAt == null) continue;
            boolean isInRange = !createdAt.isBefore(startTime) && !updatedAt.isAfter(endTime);

            if (isInRange && order.getStatus() == OrdersPhones.OrderStatus.SUCCESS) {
                totalOrder +=quantity;
            }
        }

        return totalOrder;
    }

    // tổng số đơn hàng
    public int totalOrders(LocalDateTime startTime, LocalDateTime endTime)
    {
        int totalOrder=0;
        List<OrdersPhones> allOrdersPhones = ordersPhonesService.getAllOrdersPhones();

        for (OrdersPhones order : allOrdersPhones) {
            LocalDateTime createdAt = order.getCreatedAt();
            LocalDateTime updatedAt = order.getUpdatedAt();
            // Sô lượng Sản phẩm trong đơn hàng 
            int quantity=order.getQuantity();
            // Điều kiện đơn hàng nằm trong khoảng thời gian
            if (createdAt == null || updatedAt == null) continue;
            boolean isInRange = !createdAt.isBefore(startTime) && !updatedAt.isAfter(endTime);

            if (isInRange) {
                totalOrder +=quantity;
            }
        }

        return totalOrder;
    }
    //tổng số đơn hàng đã hủy hoặc đã bị từ chối 
    public int totalRejectedAndCanceledOrdersByDay(LocalDateTime startTime, LocalDateTime endTime)
    {
        int totalOrder=0;
        List<OrdersPhones> allOrdersPhones = ordersPhonesService.getAllOrdersPhones();

        for (OrdersPhones order : allOrdersPhones) {
            LocalDateTime createdAt = order.getCreatedAt();
            LocalDateTime updatedAt = order.getUpdatedAt();
            // Sô lượng Sản phẩm trong đơn hàng 
            int quantity=order.getQuantity();
            // Điều kiện đơn hàng nằm trong khoảng thời gian
            if (createdAt == null || updatedAt == null) continue;
            boolean isInRange = !createdAt.isBefore(startTime) && !updatedAt.isAfter(endTime);

            if (isInRange && (order.getStatus() == OrdersPhones.OrderStatus.REJECTED ||order.getStatus() == OrdersPhones.OrderStatus.CANCELED)) {
                totalOrder +=quantity;
            }
        }

        return totalOrder;
    }
    //tổng số đơn hàng đang chờ xác nhận
    public int countOrdersOrderedByDay(LocalDateTime startTime, LocalDateTime endTime)
    {
        int totalOrder=0;
        List<OrdersPhones> allOrdersPhones = ordersPhonesService.getAllOrdersPhones();

        for (OrdersPhones order : allOrdersPhones) {
            LocalDateTime createdAt = order.getCreatedAt();
            LocalDateTime updatedAt = order.getUpdatedAt();
            // Sô lượng Sản phẩm trong đơn hàng 
            int quantity=order.getQuantity();
            // Điều kiện đơn hàng nằm trong khoảng thời gian
            if (createdAt == null || updatedAt == null) continue;
            boolean isInRange = !createdAt.isBefore(startTime) && !updatedAt.isAfter(endTime);

            if (isInRange && order.getStatus() == OrdersPhones.OrderStatus.ORDERED) {
                totalOrder +=quantity;
            }
        }

        return totalOrder;
    }
    // tổng doanh thu của 1 sản phẩn theo ngày , tháng , năm
    public double totalRevenueByDayForProduct(LocalDateTime startTime, LocalDateTime endTime, long idphone) { 
        double totalDay = 0;

        List<OrdersPhones> allOrdersPhones = ordersPhonesService.getAllOrdersPhones();

        for (OrdersPhones order : allOrdersPhones) {
            long phoneid=  order.getPhoneId();
            if(phoneid==idphone)
            {            
                LocalDateTime createdAt = order.getCreatedAt();
                LocalDateTime updatedAt = order.getUpdatedAt();

                // Chuyển BigDecimal sang double
                double priceOrder = order.getPrice().doubleValue();

                // Điều kiện đơn hàng nằm trong khoảng thời gian
                if (createdAt == null || updatedAt == null) continue;
            boolean isInRange = !createdAt.isBefore(startTime) && !updatedAt.isAfter(endTime);

                // Chỉ tính đơn hàng thành công
                if (isInRange && order.getStatus() == OrdersPhones.OrderStatus.SUCCESS) {
                    totalDay += priceOrder;
                }
            }
        }
        return totalDay;
    }

    // tổng số lượng đơn hàng của 1 sản phẩn theo ngày , tháng , năm
    public int  totalOrdersByDayForProduct(LocalDateTime startTime, LocalDateTime endTime, long idphone) { 
        int totalDay = 0;

        List<OrdersPhones> allOrdersPhones = ordersPhonesService.getAllOrdersPhones();

        for (OrdersPhones order : allOrdersPhones) {
            long phoneid=  order.getPhoneId();
            if(phoneid==idphone)
            {            
                LocalDateTime createdAt = order.getCreatedAt();
                LocalDateTime updatedAt = order.getUpdatedAt();
                // Sô lượng Sản phẩm trong đơn hàng 
            int quantity=order.getQuantity();

                // Điều kiện đơn hàng nằm trong khoảng thời gian
                if (createdAt == null || updatedAt == null) continue;
            boolean isInRange = !createdAt.isBefore(startTime) && !updatedAt.isAfter(endTime);

                // Chỉ tính đơn hàng thành công
                if (isInRange && order.getStatus() == OrdersPhones.OrderStatus.SUCCESS) {
                    totalDay +=quantity;
                }
            }
        }
        return totalDay;
    }

    // tổng doanh số đơn hàng của 1 khách hàng  sản phẩn theo ngày , tháng , năm
    public double  totalRevenueByDayForUser(LocalDateTime startTime, LocalDateTime endTime, long iduser) { 
        double totalDay = 0;

        List<OrdersPhones> allOrdersPhones = ordersPhonesService.getAllOrdersPhones();

        for (OrdersPhones order : allOrdersPhones) {
            long userid=  order.getUserId();
            if(userid==iduser)
            {            
                LocalDateTime createdAt = order.getCreatedAt();
                LocalDateTime updatedAt = order.getUpdatedAt();

                double priceOrder = order.getPrice().doubleValue();

                // Điều kiện đơn hàng nằm trong khoảng thời gian
                if (createdAt == null || updatedAt == null) continue;
            boolean isInRange = !createdAt.isBefore(startTime) && !updatedAt.isAfter(endTime);

                // Chỉ tính đơn hàng thành công
                if (isInRange && order.getStatus() == OrdersPhones.OrderStatus.SUCCESS) {
                    totalDay += priceOrder;
                }
            }
        }
        return totalDay;
    }
    // tổng số lượng voucher mà khách hàng đã dùng theo ngày, tháng, năm
    public int totalUsedVouchersForUser(LocalDateTime startTime, LocalDateTime endTime)
    {
        int total=0;
        List<UserVoucher> allUserVouchers = userVoucherService.getAllUserVouchers();
        for (UserVoucher userVouchers : allUserVouchers) {
             if (userVouchers.getUsedAt()!=null)    
             {
                LocalDateTime createdAt = userVouchers.getReceivedAt();
                LocalDateTime updatedAt = userVouchers.getUsedAt();


                // Điều kiện đơn hàng nằm trong khoảng thời gian
                if (createdAt == null || updatedAt == null) continue;
            boolean isInRange =  !updatedAt.isAfter(endTime);

                // Chỉ tính đơn hàng thành công
                if (isInRange) {
                    total ++;
                }
            }      
            
            
        }
        return total; 
    }

    // tổng số lượng của 1 voucher mà khách hàng đã dùng theo ngày, tháng, năm
    public int countVoucherUsageByUser(LocalDateTime startTime, LocalDateTime endTime,long id)
    {
        int total=0;
        List<UserVoucher> allUserVouchers = userVoucherService.getAllUserVouchers();
        for (UserVoucher userVouchers : allUserVouchers) {
            long idvoucher= userVouchers.getVoucherId();
             if (userVouchers.getUsedAt()!=null && idvoucher==id)    
             {
                LocalDateTime createdAt = userVouchers.getReceivedAt();
                LocalDateTime updatedAt = userVouchers.getUsedAt();


                // Điều kiện đơn hàng nằm trong khoảng thời gian
                if (createdAt == null || updatedAt == null) continue;
            boolean isInRange = !updatedAt.isAfter(endTime);
                // Chỉ tính đơn hàng thành công
                if (isInRange) {
                    total ++;
                }
            }      
            
            
        }
        return total; 
    }

    public Map<String, Object> getWeeklyStats() {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekStart = now.minusDays(6);

        List<String> labels = new ArrayList<>();
        List<Double> revenue = new ArrayList<>();
        List<Integer> ordersSuccess = new ArrayList<>();
        List<Integer> ordersTotal = new ArrayList<>();
        List<Integer> ordersRejected = new ArrayList<>();
        List<Integer> vouchersUsed = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            LocalDateTime dayStart = weekStart.plusDays(i).withHour(0).withMinute(0).withSecond(0);
            LocalDateTime dayEnd = weekStart.plusDays(i).withHour(23).withMinute(59).withSecond(59);

            labels.add("Thứ " + (i+2)); // Giả lập labels từ Thứ 2 -> CN
            revenue.add(totalRevenueByDay(dayStart, dayEnd));
            ordersSuccess.add(totalOrdersSuccessByDay(dayStart, dayEnd));
            ordersTotal.add(totalOrders(dayStart, dayEnd));
            ordersRejected.add(totalRejectedAndCanceledOrdersByDay(dayStart, dayEnd));
            vouchersUsed.add(totalUsedVouchersForUser(dayStart, dayEnd));
        }

        stats.put("labels", labels);
        stats.put("revenueByDay", revenue);
        stats.put("ordersSuccess", ordersSuccess);
        stats.put("ordersTotal", ordersTotal);
        stats.put("ordersRejected", ordersRejected);
        stats.put("vouchersUsed", vouchersUsed);

        return stats;
    }

    public Map<String, Object> getStatsByWeek(LocalDateTime start, LocalDateTime end) {
        Map<String, Object> stats = new HashMap<>();

        List<String> labels = new ArrayList<>();
        List<Double> revenue = new ArrayList<>();
        List<Integer> ordersSuccess = new ArrayList<>();
        List<Integer> ordersTotal = new ArrayList<>();
        List<Integer> ordersRejected = new ArrayList<>();
        List<Integer> vouchersUsed = new ArrayList<>();

        String[] vietWeekDays = {"Thứ 2","Thứ 3","Thứ 4","Thứ 5","Thứ 6","Thứ 7","Chủ nhật"};

        LocalDateTime current = start;
        while (!current.isAfter(end)) {
            LocalDateTime dayStart = current.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime dayEnd = current.withHour(23).withMinute(59).withSecond(59);

            // Lấy nhãn ngày theo thứ (Tiếng Việt)
            int dayOfWeekValue = dayStart.getDayOfWeek().getValue(); // 1 = Monday, 7 = Sunday
            labels.add(vietWeekDays[dayOfWeekValue - 1]);

            // Tính số liệu
            revenue.add(totalRevenueByDay(dayStart, dayEnd));
            ordersSuccess.add(totalOrdersSuccessByDay(dayStart, dayEnd));
            ordersTotal.add(totalOrders(dayStart, dayEnd));
            ordersRejected.add(totalRejectedAndCanceledOrdersByDay(dayStart, dayEnd));
            vouchersUsed.add(totalUsedVouchersForUser(dayStart, dayEnd));

            current = current.plusDays(1);
        }

        stats.put("labels", labels);
        stats.put("revenueByDay", revenue);
        stats.put("ordersSuccess", ordersSuccess);
        stats.put("ordersTotal", ordersTotal);
        stats.put("ordersRejected", ordersRejected);
        stats.put("vouchersUsed", vouchersUsed);

        return stats;
    }

    /**
     * Thống kê theo từng giờ trong một ngày cụ thể.
     * @param day Ngày cần thống kê.
     * @return Map chứa danh sách các nhãn (giờ) và dữ liệu tương ứng.
     */
    public Map<String, Object> getHourlyStatsForDay(LocalDate day) {
        Map<String, Object> stats = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Double> revenue = new ArrayList<>();
        List<Integer> ordersSuccess = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            LocalDateTime hourStart = day.atTime(hour, 0, 0);
            LocalDateTime hourEnd = day.atTime(hour, 59, 59);

            labels.add(hour + "h");
            revenue.add(totalRevenueByDay(hourStart, hourEnd));
            ordersSuccess.add(totalOrdersSuccessByDay(hourStart, hourEnd));
        }

        stats.put("labels", labels);
        stats.put("revenue", revenue);
        stats.put("ordersSuccess", ordersSuccess);
        return stats;
    }

    public Map<String, Object> getRevenueByDay(LocalDateTime start, LocalDateTime end) {
        Map<String, Object> stats = new HashMap<>();

        List<String> labels = new ArrayList<>();
        List<Double> revenue = new ArrayList<>();
        double totalrevenue=0;

        // Thứ trong tuần viết tắt: Thứ 2 → 2, ... Chủ nhật → CN
        String[] vietWeekDays = {"2","3","4","5","6","7","CN"};

        LocalDateTime current = start.withHour(0).withMinute(0).withSecond(0);
        LocalDateTime finalEnd = end.withHour(23).withMinute(59).withSecond(59);

        while (!current.isAfter(finalEnd)) {
            LocalDateTime dayStart = current.withHour(0).withMinute(0).withSecond(0);
            LocalDateTime dayEnd = current.withHour(23).withMinute(59).withSecond(59);

            int dayOfWeekValue = dayStart.getDayOfWeek().getValue(); // 1 = Monday ... 7 = Sunday
            String label = dayStart.toLocalDate().toString() + " (" + vietWeekDays[dayOfWeekValue - 1] + ")";
            labels.add(label);
            double dailyRevenue = totalRevenueByDay(dayStart, dayEnd);
            revenue.add(dailyRevenue);
            totalrevenue+=dailyRevenue;
            current = current.plusDays(1);
        }

        stats.put("labels", labels);
        stats.put("revenueByDay", revenue);
        stats.put("totalRevenue", totalrevenue);
        return stats;
    }

   public Map<String, Object> getRevenueByDayForProduct(LocalDateTime start, LocalDateTime end) {
    Map<String, Object> stats = new HashMap<>();

    List<String> labels = new ArrayList<>();
    List<Double> revenue = new ArrayList<>();
    List<Double> proportions = new ArrayList<>();
    List<String> ImageUrls = new ArrayList<>();

    LocalDateTime current = start.withHour(0).withMinute(0).withSecond(0);
    LocalDateTime finalEnd = end.withHour(23).withMinute(59).withSecond(59);

    // Tổng doanh thu (tính 1 lần, tránh chia 0)
    double TotalRevenueAll = totalRevenueByDay(current, finalEnd);
    double totalRevenueAll=TotalRevenueAll;
    if (totalRevenueAll == 0.0) {
        totalRevenueAll = 1.0; // tránh chia cho 0
    }

    List<Phone> allPhones = phoneService.getAllPhones();

    // Thu thập dữ liệu (giữ nguyên thứ tự ban đầu)
    for (Phone phone : allPhones) {
        ImageUrls.add(phone.getImageUrl());
        labels.add(phone.getName());

        long phoneid = phone.getId();
        double dailyRevenueForProduct = totalRevenueByDayForProduct(current, finalEnd, phoneid);
        revenue.add(dailyRevenueForProduct);
        
    }

    

    // Tính proportions (làm tròn 2 chữ số)
    for (int i = 0; i < revenue.size(); i++) {
        double prop = (revenue.get(i) / totalRevenueAll) * 100.0;
        prop = Math.round(prop * 100.0) / 100.0; // làm tròn 2 chữ số thập phân
        proportions.add(prop);
    }

    // --- Sắp xếp giảm dần theo revenue, nhưng giữ đồng bộ các list ---
    // Tạo danh sách chỉ số, sắp xếp chỉ số theo revenue
    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i < revenue.size(); i++) indices.add(i);

    indices.sort((a, b) -> Double.compare(revenue.get(b), revenue.get(a))); // giảm dần

    // Tạo các danh sách mới theo thứ tự đã sắp xếp
    List<String> sortedLabels = new ArrayList<>();
    List<Double> sortedRevenue = new ArrayList<>();
    List<Double> sortedProportions = new ArrayList<>();
    List<String> sortedImageUrls = new ArrayList<>();

    for (Integer idx : indices) {
        sortedLabels.add(labels.get(idx));
        sortedRevenue.add(revenue.get(idx));
        sortedProportions.add(proportions.get(idx));
        sortedImageUrls.add(ImageUrls.get(idx));
    }

    // Ghi đè lại các list ban đầu (giữ tên biến, giữ bố cục trả về)
    labels.clear(); labels.addAll(sortedLabels);
    revenue.clear(); revenue.addAll(sortedRevenue);
    proportions.clear(); proportions.addAll(sortedProportions);
    ImageUrls.clear(); ImageUrls.addAll(sortedImageUrls);

    stats.put("ImageUrls", ImageUrls);
    stats.put("labels", labels);
    stats.put("revenueByDay", revenue);
    stats.put("proportions", proportions);
    stats.put("totalRevenue", TotalRevenueAll);


    return stats;
}

public Map<String, Object> getTop5ProductsByRevenue(LocalDateTime start, LocalDateTime end) {
    Map<String, Object> stats = new HashMap<>();

    List<String> labels = new ArrayList<>();
    List<Double> revenue = new ArrayList<>();
    List<Double> proportions = new ArrayList<>();
    List<String> ImageUrls = new ArrayList<>();

    LocalDateTime current = start.withHour(0).withMinute(0).withSecond(0);
    LocalDateTime finalEnd = end.withHour(23).withMinute(59).withSecond(59);

    // Tổng doanh thu (tránh chia 0)
    double TotalRevenueAll = totalRevenueByDay(current, finalEnd);
    double totalRevenueAll = TotalRevenueAll == 0 ? 1.0 : TotalRevenueAll;

    List<Phone> allPhones = phoneService.getAllPhones();

    // Thu thập dữ liệu
    for (Phone phone : allPhones) {
        ImageUrls.add(phone.getImageUrl());
        labels.add(phone.getName());

        long phoneid = phone.getId();
        double dailyRevenueForProduct = totalRevenueByDayForProduct(current, finalEnd, phoneid);
        revenue.add(dailyRevenueForProduct);
    }

    // Tính tỉ lệ %
    for (int i = 0; i < revenue.size(); i++) {
        double prop = (revenue.get(i) / totalRevenueAll) * 100.0;
        prop = Math.round(prop * 100.0) / 100.0;
        proportions.add(prop);
    }

    // Sắp xếp theo revenue giảm dần (dùng list index)
    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i < revenue.size(); i++) indices.add(i);

    indices.sort((a, b) -> Double.compare(revenue.get(b), revenue.get(a)));

    // ⚠ CHỈ LẤY TOP 5
    if (indices.size() > 5) {
        indices = indices.subList(0, 5);
    }

    // Tạo list mới theo thứ tự đã sắp top 5
    List<String> sortedLabels = new ArrayList<>();
    List<Double> sortedRevenue = new ArrayList<>();
    List<Double> sortedProportions = new ArrayList<>();
    List<String> sortedImageUrls = new ArrayList<>();

    for (Integer idx : indices) {
        sortedLabels.add(labels.get(idx));
        sortedRevenue.add(revenue.get(idx));
        sortedProportions.add(proportions.get(idx));
        sortedImageUrls.add(ImageUrls.get(idx));
    }

    // Ghi lại kết quả
    stats.put("ImageUrls", sortedImageUrls);
    stats.put("labels", sortedLabels);
    stats.put("revenueByDay", sortedRevenue);
    stats.put("proportions", sortedProportions);
    stats.put("totalRevenue", TotalRevenueAll);

    return stats;
}

public Map<String, Object> getTop5BestSellingProducts(LocalDateTime start, LocalDateTime end) {
    Map<String, Object> stats = new HashMap<>();

    List<String> labels = new ArrayList<>();
    List<Double> quantities = new ArrayList<>(); // Số lượng bán ra
    List<Double> proportions = new ArrayList<>();
    List<String> ImageUrls = new ArrayList<>();

    LocalDateTime current = start.withHour(0).withMinute(0).withSecond(0);
    LocalDateTime finalEnd = end.withHour(23).withMinute(59).withSecond(59);

    List<Phone> allPhones = phoneService.getAllPhones();

    double totalQuantityAll = 0; // Tổng số lượng tất cả sản phẩm bán được

    // 1. Thu thập dữ liệu
    for (Phone phone : allPhones) {
        ImageUrls.add(phone.getImageUrl());
        labels.add(phone.getName());

        long phoneid = phone.getId();
        // Gọi hàm đếm số lượng đơn hàng bạn đã viết
        int quantityInt = totalOrdersByDayForProduct(current, finalEnd, phoneid);
        double quantity = (double) quantityInt;

        quantities.add(quantity);
        
        // Cộng dồn vào tổng để tính % sau này
        totalQuantityAll += quantity;
    }

    // Xử lý chia cho 0
    double finalTotalQuantity = totalQuantityAll == 0 ? 1.0 : totalQuantityAll;

    // 2. Tính tỉ lệ %
    for (Double qty : quantities) {
        double prop = (qty / finalTotalQuantity) * 100.0;
        prop = Math.round(prop * 100.0) / 100.0;
        proportions.add(prop);
    }

    // 3. Sắp xếp theo số lượng (quantity) giảm dần
    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i < quantities.size(); i++) indices.add(i);

    indices.sort((a, b) -> Double.compare(quantities.get(b), quantities.get(a)));

    // ⚠ CHỈ LẤY TOP 5
    if (indices.size() > 5) {
        indices = indices.subList(0, 5);
    }

    // 4. Tạo list mới theo thứ tự đã sắp xếp
    List<String> sortedLabels = new ArrayList<>();
    List<Double> sortedQuantities = new ArrayList<>();
    List<Double> sortedProportions = new ArrayList<>();
    List<String> sortedImageUrls = new ArrayList<>();

    for (Integer idx : indices) {
        sortedLabels.add(labels.get(idx));
        sortedQuantities.add(quantities.get(idx));
        sortedProportions.add(proportions.get(idx));
        sortedImageUrls.add(ImageUrls.get(idx));
    }

    // 5. Ghi lại kết quả (Lưu ý key "revenueByDay" ở đây đổi thành "quantityByDay" hoặc giữ nguyên tùy ý bạn map ở FE)
    stats.put("ImageUrls", sortedImageUrls);
    stats.put("labels", sortedLabels);
    stats.put("revenueByDay", sortedQuantities); // Mình giữ nguyên key này để bạn đỡ phải sửa JS ở frontend, nhưng bản chất nó là quantity
    stats.put("proportions", sortedProportions);
    stats.put("totalRevenue", totalQuantityAll); // Trả về tổng số lượng

    return stats;
}   

public Map<String, Object> getCountVoucherUsageByUser(LocalDateTime start, LocalDateTime end) {
    Map<String, Object> stats = new HashMap<>();

    List<String> labels = new ArrayList<>();
    List<Integer> quantities = new ArrayList<>();

    LocalDateTime current = start.withHour(0).withMinute(0).withSecond(0);
    LocalDateTime finalEnd = end.withHour(23).withMinute(59).withSecond(59);

    List<DiscountVoucher> allVoucherUsers = discountVoucherService.getAll();
    int totalQuantityAll = totalUsedVouchersForUser(current, finalEnd);
    //int totalQuantityAll = 0; // Tổng số lượng tất cả voucher

    for (DiscountVoucher discountVoucher : allVoucherUsers) {
        long voucherId = discountVoucher.getId();
        labels.add(discountVoucher.getCode());
        // Gọi hàm đếm số lượng 1 voucher
        int quantityInt = countVoucherUsageByUser(current, finalEnd, voucherId);
        int quantity = quantityInt;

        quantities.add(quantity);
        
        // Cộng dồn vào tổng để tính %
        //totalQuantityAll += quantity;
    }

    // 3. Sắp xếp theo số lượng (quantity) giảm dần
    List<Integer> indices = new ArrayList<>();
    for (int i = 0; i < quantities.size(); i++) indices.add(i);

    indices.sort((a, b) -> Double.compare(quantities.get(b), quantities.get(a)));

    // ⚠ CHỈ LẤY TOP 5
    if (indices.size() > 5) {
        indices = indices.subList(0, 5);
    }

    // 4. Tạo list mới theo thứ tự đã sắp xếp
    List<String> sortedLabels = new ArrayList<>();
    List<Integer> sortedQuantities = new ArrayList<>();

    for (Integer idx : indices) {
        sortedLabels.add(labels.get(idx));
        sortedQuantities.add(quantities.get(idx));
    }

    // Gán vào map với tên key rõ nghĩa
    stats.put("labels", sortedLabels);               // Tên voucher
    stats.put("quantities", sortedQuantities);      // Số lượng đã dùng
    stats.put("totalQuantity", totalQuantityAll);   // Tổng số lượng tất cả

    return stats;
}

}