package com.dhm.controller;

import com.dhm.entity.User;
import com.dhm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    @Autowired
    private UserService userService;
    
    /**
     * 首页
     */
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    /**
     * 登录页面
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    /**
     * 处理登录请求
     */
    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, 
                        HttpSession session, Model model) {
        User user = userService.login(username, password);
        if (user != null) {
            // 登录成功，将用户信息存入session
            session.setAttribute("user", user);
            
            // 根据角色跳转到不同页面
            if ("admin".equals(user.getRole())) {
                return "redirect:/admin/dashboard";
            } else {
                return "redirect:/user/dashboard";
            }
        } else {
            // 登录失败
            model.addAttribute("error", "用户名或密码错误");
            return "login";
        }
    }
    
    /**
     * 注册页面
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }
    
    /**
     * 处理注册请求
     */
    @PostMapping("/register")
    public String register(User user, Model model) {
        // 注册用户
        boolean success = userService.register(user);
        if (success) {
            // 注册成功，跳转到登录页
            model.addAttribute("msg", "注册成功，请登录");
            return "login";
        } else {
            // 注册失败
            model.addAttribute("error", "用户名已存在");
            return "register";
        }
    }
    
    /**
     * 退出登录
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 清除session
        session.invalidate();
        return "redirect:/login";
    }
    
    /**
     * 用户个人中心页面
     */
    @GetMapping("/user/profile")
    public String userProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // 获取最新的用户信息
        user = userService.getUserById(user.getUserId());
        model.addAttribute("user", user);
        
        return "user/profile";
    }
    
    /**
     * 修改用户信息
     */
    @PostMapping("/user/updateProfile")
    public String updateProfile(User user, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("user");
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        // 设置不能修改的字段
        user.setUserId(currentUser.getUserId());
        user.setUsername(currentUser.getUsername());
        user.setRole(currentUser.getRole());
        
        // 更新用户信息
        boolean success = userService.updateUser(user);
        if (success) {
            // 更新session中的用户信息
            session.setAttribute("user", userService.getUserById(user.getUserId()));
            model.addAttribute("msg", "更新成功");
        } else {
            model.addAttribute("error", "更新失败");
        }
        
        return "user/profile";
    }
    
    /**
     * 检查用户名是否已存在（用于异步验证）
     */
    @GetMapping("/checkUsername")
    @ResponseBody
    public boolean checkUsername(@RequestParam String username) {
        User user = userService.login(username, "");
        return user == null; // 如果为null，表示用户名可用
    }
    
    /**
     * 用户仪表盘页面
     */
    @GetMapping("/user/dashboard")
    public String userDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", user);
        return "user/dashboard";
    }
} 