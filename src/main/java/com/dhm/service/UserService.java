package com.dhm.service;

import com.dhm.entity.User;

import java.util.List;

public interface UserService {
    
    /**
     * 用户登录
     * @param username 用户名
     * @param password 密码
     * @return 用户对象，登录失败返回null
     */
    User login(String username, String password);
    
    /**
     * 用户注册
     * @param user 用户对象
     * @return 是否注册成功
     */
    boolean register(User user);
    
    /**
     * 更新用户信息
     * @param user 用户对象
     * @return 是否更新成功
     */
    boolean updateUser(User user);
    
    /**
     * 根据ID获取用户
     * @param userId 用户ID
     * @return 用户对象
     */
    User getUserById(Integer userId);
    
    /**
     * 获取所有用户（仅管理员可用）
     * @return 用户列表
     */
    List<User> getAllUsers();
    
    /**
     * 搜索用户
     * @param username 用户名（可选）
     * @param role 角色（可选）
     * @param status 状态（可选）
     * @return 符合条件的用户列表
     */
    List<User> searchUsers(String username, String role, String status);
    
    /**
     * 根据ID删除用户
     * @param userId 用户ID
     * @return 是否删除成功
     */
    boolean deleteUser(Integer userId);
} 