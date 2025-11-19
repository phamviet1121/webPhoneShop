
package com.example.phone_shop.services;

import com.example.phone_shop.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    // Lấy tất cả users
    public List<User> getAllUsers() {
        String sql = "SELECT * FROM users";  // Truy vấn SQL lấy tất cả người dùng
        return jdbcTemplate.query(sql, new UserRowMapper());
    }

   

    // Lưu người dùng vào database
    public String registerUser(User user) {
        // Kiểm tra email có tồn tại không trong cơ sở dữ liệu
        String checkEmailSql = "SELECT COUNT(*) FROM users WHERE gmailUser = ?";
        int count = jdbcTemplate.queryForObject(checkEmailSql, Integer.class, user.getGmailUser());

        if (count > 0) {
            // Nếu email đã tồn tại, trả về thông báo lỗi
            return "Email đã tồn tại! Vui lòng thử lại.";
        }

        // Nếu email chưa tồn tại, thực hiện lưu người dùng vào cơ sở dữ liệu
        String insertSql = "INSERT INTO users (nameUser, gmailUser, passUser,phoneNumber, address, role) VALUES (?, ?, ?, ?, ?, 'user')";
        jdbcTemplate.update(insertSql, user.getNameUser(), user.getGmailUser(), user.getPassUser(), user.getPhoneNumber(), user.getAddress() ); // Thêm số điện thoại

        // Đăng ký thành công
        return "Đăng ký thành công! Bạn có thể đăng nhập ngay.";
    }
   

    // Hàm thêm người dùng mới vào database
    public void addUser(User user) {
        String sql = "INSERT INTO users (nameUser, gmailUser, passUser, phoneNumber, address, role) VALUES (?, ?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, user.getNameUser(), user.getGmailUser(), user.getPassUser(), user.getPhoneNumber(), user.getAddress(), user.getRole());
    }
    //hàm sửa 
    public void updateUser(User user) {
        String sql = "UPDATE users SET nameUser = ?, gmailUser = ?, passUser = ?, phoneNumber = ?, address = ?, role = ? WHERE idUser = ?";
        jdbcTemplate.update(sql, user.getNameUser(), user.getGmailUser(), user.getPassUser(), user.getPhoneNumber(), user.getAddress(), user.getRole(), user.getIdUser());
    }
    //hàm xóa
    public void deleteUserById(Long id) {
        String sql = "DELETE FROM users WHERE idUser = ?";
        jdbcTemplate.update(sql, id);
    }
    


 // Xử lý đăng nhập và điều hướng theo role
    public String loginUser(String gmailUser, String passUser) {
        String sql = "SELECT idUser, nameUser, gmailUser, passUser, phoneNumber, address,role FROM users WHERE gmailUser = ?";

        List<User> users = jdbcTemplate.query(sql, new Object[]{gmailUser}, new UserRowMapper());

        if (users.isEmpty()) {
            return "failed"; // Người dùng không tồn tại hoặc sai thông tin
        }

        User user = users.get(0);
        if (user.getPassUser().equals(passUser)) {
            return user.getRole().equals("admin") ? "index_admin" : "index_user";
        }
        return "failed";
    }
    // Lớp RowMapper để chuyển kết quả truy vấn SQL thành đối tượng User
    private static class UserRowMapper implements RowMapper<User> {
        @Override
         public User mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
            User user = new User();
            user.setIdUser(rs.getLong("idUser")); // Đảm bảo cột 'idUser' trùng với SQL
            user.setNameUser(rs.getString("nameUser"));
            user.setGmailUser(rs.getString("gmailUser"));
            user.setPassUser(rs.getString("passUser"));
            user.setPhoneNumber(rs.getString("phoneNumber")); // ✅ Số điện thoại
            user.setAddress(rs.getString("address"));
            user.setRole(rs.getString("role"));
            return user;
        }
    }


   public User findByUsername(String username) {
    String sql = "SELECT * FROM users WHERE gmailUser = ?";
    List<User> users = jdbcTemplate.query(sql, new Object[]{username}, new UserRowMapper());

    if (users.isEmpty()) {
        return null;  // Không tìm thấy người dùng
    }

    return users.get(0);  // Trả về người dùng đầu tiên (giả sử chỉ có một người dùng với email này)
    }
 // 📌 Lấy user theo ID (dùng trong session)
    public User findById(Long userId) {
        String sql = "SELECT * FROM users WHERE idUser = ?";
        List<User> users = jdbcTemplate.query(sql, new Object[]{userId}, new UserRowMapper());

        return users.isEmpty() ? null : users.get(0);
    }

    public Integer getUserIdByUsername(String username) {
      String sql = "SELECT idUser FROM users WHERE gmailUser = ?";
        try {
            return jdbcTemplate.queryForObject(sql, new Object[]{username}, Integer.class);
        } catch (Exception e) {
            return null; // Trả về null nếu không tìm thấy
        }
    }
  
}
