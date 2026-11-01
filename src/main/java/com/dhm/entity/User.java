package com.dhm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户实体类
 */
@Data
@TableName("user")
public class User {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Integer userId;
    
    @TableField("username")
    private String username;
    
    @TableField("password")
    private String password;
    
    @TableField("birth_date")
    private String birthDate;
    
    @TableField("gender")
    private String gender;
    
    @TableField("height")
    private Double height;
    
    @TableField("activity_level")
    private String activityLevel;
    
    @TableField("health_goal")
    private String healthGoal;
    
    @TableField("role")
    private String role;
    
    @TableField("dietary_prefs")
    private String dietaryPrefs;
    
    @TableField("created_at")
    private String createdAt;
} 