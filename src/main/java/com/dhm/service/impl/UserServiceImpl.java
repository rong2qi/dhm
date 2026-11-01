package com.dhm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dhm.entity.User;
import com.dhm.mapper.UserMapper;
import com.dhm.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.xml.crypto.Data;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户对象，登录失败返回null
     */
    @Override
    public User login(String username, String password) {
        // 根据用户名查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = this.getOne(queryWrapper);
        
        // 验证密码
        if (user != null) {
            // 使用MD5验证密码
            String md5Password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));
            if (md5Password.equals(user.getPassword())) {
                return user;
            }
        }
        return null;
    }

    /**
     * 用户注册
     * @param user 用户对象
     * @return 是否注册成功
     */
    @Override
    public boolean register(User user) {
        // 检查用户名是否已存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, user.getUsername());
        if (this.count(queryWrapper) > 0) {
            return false;
        }
        
        // 对密码进行MD5加密
        String md5Password = DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8));
        user.setPassword(md5Password);
        
        // 设置默认角色为普通用户
        user.setRole("user");
        
        // 处理饮食偏好字段，确保是有效的JSON格式
        if (user.getDietaryPrefs() == null || user.getDietaryPrefs().trim().isEmpty()) {
            // 如果为空，则设置为空JSON对象
            user.setDietaryPrefs("{}");
        } else {
            // 检查是否已经是有效的JSON格式
            String dietaryPrefs = user.getDietaryPrefs().trim();
            if (!(dietaryPrefs.startsWith("{") && dietaryPrefs.endsWith("}"))) {
                // 不是有效JSON格式，将其作为字符串值包装在JSON对象中
                user.setDietaryPrefs("{\"preferences\": \"" + dietaryPrefs.replace("\"", "\\\"") + "\"}");
            }
        }

        // 将当前时间转为字符串存醋
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        user.setCreatedAt(dateFormat.format(new Date()));

        return this.save(user);
    }

    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 是否更新成功
     */
    @Override
    public boolean updateUser(User user) {
        // 处理饮食偏好字段，确保是有效的JSON格式
        if (user.getDietaryPrefs() == null || user.getDietaryPrefs().trim().isEmpty()) {
            // 如果为空，则设置为空JSON对象
            user.setDietaryPrefs("{}");
        } else {
            // 检查是否已经是有效的JSON格式
            String dietaryPrefs = user.getDietaryPrefs().trim();
            if (!(dietaryPrefs.startsWith("{") && dietaryPrefs.endsWith("}"))) {
                // 不是有效JSON格式，将其作为字符串值包装在JSON对象中
                user.setDietaryPrefs("{\"preferences\": \"" + dietaryPrefs.replace("\"", "\\\"") + "\"}");
            }
        }
        
        return this.updateById(user);
    }

    /**
     * 根据ID获取用户
     * @param userId 用户ID
     * @return 用户对象
     */
    @Override
    public User getUserById(Integer userId) {
        return this.getById(userId);
    }

    /**
     * 获取所有用户（仅管理员可用）
     * @return 用户列表
     */
    @Override
    public List<User> getAllUsers() {
        return this.list();
    }

    /**
     * 根据ID删除用户
     * @param userId 用户ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteUser(Integer userId) {
        return this.removeById(userId);
    }

    /**
     * 搜索用户
     * @param username 用户名（可选）
     * @param role 角色（可选）
     * @param status 状态（可选）
     * @return 符合条件的用户列表
     */
    @Override
    public List<User> searchUsers(String username, String role, String status) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 如果提供了用户名，进行模糊搜索
        if (StringUtils.hasText(username)) {
            queryWrapper.like(User::getUsername, username);
        }
        
        // 如果提供了角色，进行精确匹配
        if (StringUtils.hasText(role)) {
            queryWrapper.eq(User::getRole, role);
        }
        
        // 状态过滤（虽然数据库中没有实际的status字段，但可以通过返回所有用户来表示"活跃"状态）
        if (StringUtils.hasText(status) && "active".equals(status)) {
            // 所有用户都是活跃的，不做额外筛选
        } else if (StringUtils.hasText(status) && "inactive".equals(status)) {
            // 模拟筛选出不活跃用户，实际上返回空列表
            return Collections.emptyList(); // 返回空列表
        }
        
        return this.list(queryWrapper);
    }
} 