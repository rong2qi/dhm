package com.dhm.controller;

import com.dhm.entity.User;
import com.dhm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    /**
     * 管理员仪表盘
     */
    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            return "redirect:/login";
        }
        
        return "redirect:/admin/users";
    }

    /**
     * 用户管理页面
     */
    @GetMapping("/users")
    public String userManagement(HttpSession session, Model model, String username, String role, String status) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"admin".equals(user.getRole())) {
            return "redirect:/login";
        }

        List<User> users = userService.searchUsers(username, role, status);
        model.addAttribute("users", users);
        model.addAttribute("user", user);
        model.addAttribute("searchUsername", username);
        model.addAttribute("searchRole", role);
        model.addAttribute("searchStatus", status);

        return "admin/users";
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/user/{userId}")
    public String userDetail(@PathVariable Integer userId, HttpSession session, Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || !"admin".equals(admin.getRole())) {
            return "redirect:/login";
        }

        User user = userService.getUserById(userId);
        model.addAttribute("user", user);

        return "admin/user-detail";
    }

    /**
     * 删除用户
     */
    @GetMapping("/deleteUser/{userId}")
    @ResponseBody
    public boolean deleteUser(@PathVariable Integer userId, HttpSession session) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || !"admin".equals(admin.getRole())) {
            return false;
        }

        return userService.deleteUser(userId);
    }
} 
