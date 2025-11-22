package com.example.phone_shop.services;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.phone_shop.models.OrdersPhones;
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
            // Điều kiện đơn hàng nằm trong khoảng thời gian
            if (createdAt == null || updatedAt == null) continue;
            boolean isInRange = !createdAt.isBefore(startTime) && !updatedAt.isAfter(endTime);

            if (isInRange && order.getStatus() == OrdersPhones.OrderStatus.SUCCESS) {
                totalOrder ++;
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
            // Điều kiện đơn hàng nằm trong khoảng thời gian
            if (createdAt == null || updatedAt == null) continue;
            boolean isInRange = !createdAt.isBefore(startTime) && !updatedAt.isAfter(endTime);

            if (isInRange) {
                totalOrder ++;
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
            // Điều kiện đơn hàng nằm trong khoảng thời gian
            if (createdAt == null || updatedAt == null) continue;
            boolean isInRange = !createdAt.isBefore(startTime) && !updatedAt.isAfter(endTime);

            if (isInRange && (order.getStatus() == OrdersPhones.OrderStatus.REJECTED ||order.getStatus() == OrdersPhones.OrderStatus.CANCELED)) {
                totalOrder ++;
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
            // Điều kiện đơn hàng nằm trong khoảng thời gian
            if (createdAt == null || updatedAt == null) continue;
            boolean isInRange = !createdAt.isBefore(startTime) && !updatedAt.isAfter(endTime);

            if (isInRange && order.getStatus() == OrdersPhones.OrderStatus.ORDERED) {
                totalOrder ++;
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

                // Điều kiện đơn hàng nằm trong khoảng thời gian
                if (createdAt == null || updatedAt == null) continue;
            boolean isInRange = !createdAt.isBefore(startTime) && !updatedAt.isAfter(endTime);

                // Chỉ tính đơn hàng thành công
                if (isInRange && order.getStatus() == OrdersPhones.OrderStatus.SUCCESS) {
                    totalDay ++;
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
            boolean isInRange = !createdAt.isBefore(startTime) && !updatedAt.isAfter(endTime);

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
            long idvoucher= userVouchers.getId();
             if (userVouchers.getUsedAt()!=null && idvoucher==id)    
             {
                LocalDateTime createdAt = userVouchers.getReceivedAt();
                LocalDateTime updatedAt = userVouchers.getUsedAt();


                // Điều kiện đơn hàng nằm trong khoảng thời gian
                if (createdAt == null || updatedAt == null) continue;
            boolean isInRange = !createdAt.isBefore(startTime) && !updatedAt.isAfter(endTime);
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


    



}