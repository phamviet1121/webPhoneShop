package com.example.phone_shop.controllers;

import com.example.phone_shop.models.User;
import com.example.phone_shop.services.UserService;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/auth")  // Đường dẫn chung cho các hành động đăng nhập và đăng ký
public class AuthController {
    private final UserService userService;

    //@Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // Hiển thị form đăng nhập
    @GetMapping("/login")
    public String showLoginForm() {
            return "login";  // Chuyển hướng đến trang login.html
        }

        // Xử lý đăng nhập
        @PostMapping("/login")
        public String loginUser(@RequestParam String username, @RequestParam String password, Model model, HttpSession session) {
        // Sử dụng UserService để kiểm tra thông tin đăng nhập
        String loginResult = userService.loginUser(username, password);
        Integer idUser = userService.getUserIdByUsername(username);
        User user = userService.findByUsername(username);

        if (idUser != null) { 
                session.setAttribute("idUser", idUser); // Lưu idUser vào session
                session.setAttribute("user", user);
            }

        if ("index_admin".equals(loginResult)) {
            
            return "redirect:/phones/index_Admin";  // Chuyển hướng đến trang admin nếu đúng
        } else if ("index_user".equals(loginResult)) {
            return "redirect:/phones/index_User";  // Chuyển hướng đến trang user nếu đúng
        } else {
            model.addAttribute("error", "Email hoặc mật khẩu không đúng!");  // Thông báo lỗi nếu sai
            return "login";  // Quay lại trang đăng nhập nếu sai
        }
    }
    // public String loginUser(@RequestParam String username, @RequestParam String password, Model model) {
    //     if ("admin@gmail.com".equals(username) && "123456".equals(password)) {
    //         return "redirect:/phones/index_User";  // Chuyển hướng đến trang chính nếu đúng
    //     } else {
    //         model.addAttribute("error", "Email hoặc mật khẩu không đúng!");
    //         return "login";  // Quay lại trang đăng nhập nếu sai
    //     }
    // }

    // Hiển thị form đăng ký
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "register";  // Chuyển hướng đến trang register.html
    }

    // Xử lý đăng ký
    @PostMapping("/register")
     public String registerUser(@ModelAttribute User user, Model model) {
        String message = userService.registerUser(user);
        
        // Nếu đăng ký thành công, chuyển đến trang login
        if (message.equals("Đăng ký thành công! Bạn có thể đăng nhập ngay.")) {
            model.addAttribute("successMessage", message);
            return "redirect:/auth/login";  // Chuyển hướng đến trang đăng nhập
        }
        
        // Nếu thất bại (email đã tồn tại), hiển thị thông báo và quay lại trang đăng ký
        model.addAttribute("message", message);
        return "register";  // Quay lại trang đăng ký với thông báo
    }
    // Xử lý logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Xóa toàn bộ session
        return "redirect:/phones"; // Chuyển hướng về trang đăng nhập
    }

    // public String registerUser(@ModelAttribute User user, Model model) {
    //     String message = userService.registerUser(user);
    //     model.addAttribute("message", message);
    //     return "register";  // Quay lại trang đăng ký với thông báo
    // }
    // @GetMapping("/List_users")
    // public String listUsers(Model model) {
    //     model.addAttribute("List_users", userService.getAllUsers());
    //     return "List_users";  // Trả về file Thymeleaf: users.html
    // }

    // @GetMapping("/List_users")
    // public String listUsers(Model model) {
    //     List<User> users = userService.getAllUsers();  // Lấy danh sách người dùng từ UserService
    //     model.addAttribute("users", users);  // Truyền dữ liệu vào model
    //     return "users/List_users";  // Trả về tên file Thymeleaf (List_users.html)
    // }


    @GetMapping("/new_user")
    public String showAddUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admins/add_user"; // Trả về trang add_user.html
    }
    
    @PostMapping("/save_user")
    public String saveUser(@ModelAttribute("user") User user) {
        userService.addUser(user);
        return "redirect:/phones/index_Admin"; // Quay lại trang danh sách
    }


    //
    // Hiển thị trang sửa thông tin người dùng
    @GetMapping("/edit_user/{id}")
    public String showEditUserForm(@PathVariable Long id, Model model) {
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "admins/edit_user";  // Chuyển đến trang Thymeleaf edit_user.html
    }

        @PostMapping("/update_user")
    public String updateUser(@ModelAttribute User user) {
        userService.updateUser(user);
        return "redirect:/phones/index_Admin";
    }



    ///
    // Hiển thị trang sửa thông tin người dùng
    @GetMapping("/edit_user_order/{id}")
    public String showEditUserOrderForm(@PathVariable Long id, Model model,HttpSession session) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        if (idUser == null) {
            return "redirect:/auth/login";
        }
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users/edit_user_orders";  // Chuyển đến trang Thymeleaf edit_user.html
    }

        @PostMapping("/update_user_order")
    public String updateUserOrder(@ModelAttribute User user, HttpSession session) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        if (idUser == null) {
            return "redirect:/auth/login";
        }
        userService.updateUser(user);
        
        // Lấy lại thông tin mới từ database
        User updatedUser = userService.findById(user.getIdUser());
        Integer updatedUserId = (updatedUser.getIdUser()).intValue(); // Hoặc lấy ID từ userService nếu có phương thức phù hợp

        // Cập nhật lại session
        session.setAttribute("idUser", updatedUserId);
        session.setAttribute("user", updatedUser);
        
        return "redirect:/orders/orders_list_User/" + updatedUserId;

    }

    ////
    // Hiển thị trang sửa thông tin người dùng
    @GetMapping("/edit_user_index/{id}")
    public String showEditUserIndexForm(@PathVariable Long id, Model model,HttpSession session) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        if (idUser == null) {
            return "redirect:/auth/login";
        }
        User user = userService.findById(id);
        model.addAttribute("user", user);
        return "users/edit_user_index";  // Chuyển đến trang Thymeleaf edit_user.html
    }

        @PostMapping("/update_user_index")
    public String updateUserIndex(@ModelAttribute User user,HttpSession session) {
        Integer idUser = (Integer) session.getAttribute("idUser");
        if (idUser == null) {
            return "redirect:/auth/login";
        }
        userService.updateUser(user);

        User updatedUser = userService.findById(user.getIdUser());
        Integer updatedUserId = (updatedUser.getIdUser()).intValue(); // Hoặc lấy ID từ userService nếu có phương thức phù hợp

        // Cập nhật lại session
        session.setAttribute("idUser", updatedUserId);
        session.setAttribute("user", updatedUser);
        return "redirect:/phones/index_User";
    }
   
    @GetMapping("/delete_user/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteUserById(id);
        return "redirect:/phones/index_Admin";
    }
}
