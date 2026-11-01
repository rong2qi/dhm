package com.dhm.config;

import com.dhm.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求的URI
        String uri = request.getRequestURI();
        
        // 静态资源和首页、登录、注册页面不拦截
        if (uri.contains("/static/") || uri.equals("/") || uri.startsWith("/login") || uri.startsWith("/register") || uri.contains("/error")) {
            return true;
        }
        
        // 获取Session
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        // 未登录，重定向到登录页
        if (user == null) {
            response.sendRedirect("/login");
            return false;
        }
        
        // 管理员权限验证
        if (uri.startsWith("/admin") && !"admin".equals(user.getRole())) {
            response.sendRedirect("/user/dashboard");
            return false;
        }
        
        return true;
    }
} 