package com.dhm.controller;

import com.dhm.entity.Plan;
import com.dhm.entity.User;
import com.dhm.service.DietPlanService;
import com.dhm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@RequestMapping("/dietPlan")
public class DietPlanController {

    @Autowired
    private DietPlanService dietPlanService;
    
    @Autowired
    private UserService userService;
    
    /**
     * 生成饮食建议页面
     */
    @GetMapping("/generate")
    public String generatePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // 获取最新的用户信息
        user = userService.getUserById(user.getUserId());
        model.addAttribute("user", user);
        
        return "diet/generate";
    }
    
    /**
     * 生成饮食建议
     */
    @PostMapping("/generate")
    public String generateDietPlan(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        // 获取最新的用户信息
        user = userService.getUserById(user.getUserId());
        
        // 生成饮食建议
        Plan plan = dietPlanService.generateDietPlan(user);
        
        return "redirect:/dietPlan/detail/" + plan.getRecordId();
    }
    
    /**
     * 查看饮食计划详情
     */
    @GetMapping("/detail/{planId}")
    public String planDetail(@PathVariable Integer planId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        Plan plan = dietPlanService.getPlanById(planId);
        
        // 检查是否是当前用户的计划
        if (plan == null || !plan.getUserId().equals(user.getUserId())) {
            return "redirect:/user/dashboard";
        }
        
        model.addAttribute("plan", plan);
        model.addAttribute("isAdminView", false); // 普通用户查看
        
        return "diet/detail";
    }
    
    /**
     * 管理员查看饮食计划详情
     */
    @GetMapping("/admin/detail/{planId}")
    public String adminPlanDetail(@PathVariable Integer planId, HttpSession session, Model model) {
        User admin = (User) session.getAttribute("user");
        if (admin == null || !"admin".equals(admin.getRole())) {
            return "redirect:/login";
        }
        
        Plan plan = dietPlanService.getPlanById(planId);
        if (plan == null) {
            return "redirect:/admin/dashboard";
        }
        
        // 获取计划所属用户
        User user = userService.getUserById(plan.getUserId());
        
        model.addAttribute("plan", plan);
        model.addAttribute("user", user);
        model.addAttribute("isAdminView", true); // 管理员查看
        
        return "diet/detail";
    }
    
    /**
     * 查看历史记录列表
     */
    @GetMapping("/history")
    public String historyList(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Plan> historyList = dietPlanService.getUserHistory(user.getUserId());
        model.addAttribute("historyList", historyList);
        
        return "diet/history";
    }
    
    /**
     * 查看所有计划列表（包括计划和日志）
     */
    @GetMapping("/plans")
    public String planList(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        
        List<Plan> planList = dietPlanService.getUserPlans(user.getUserId());
        model.addAttribute("planList", planList);
        
        return "diet/plans";
    }
    
    /**
     * 将计划转换为日志
     */
    @GetMapping("/convertToLog/{planId}")
    @ResponseBody
    public Integer convertToLog(@PathVariable Integer planId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return null;
        }
        
        // 检查是否是当前用户的计划
        Plan plan = dietPlanService.getPlanById(planId);
        if (plan == null || !plan.getUserId().equals(user.getUserId())) {
            return null;
        }
        
        return dietPlanService.convertPlanToLog(planId);
    }
    
    /**
     * 删除计划或日志
     */
    @GetMapping("/delete/{planId}")
    @ResponseBody
    public boolean deletePlan(@PathVariable Integer planId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return false;
        }
        
        // 检查是否是当前用户的计划
        Plan plan = dietPlanService.getPlanById(planId);
        if (plan == null || !plan.getUserId().equals(user.getUserId())) {
            return false;
        }
        
        return dietPlanService.deletePlan(planId);
    }
} 