package com.dhm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 饮食计划/日志实体类
 */
@Data
@TableName("plan")
public class Plan {
    @TableId(value = "record_id", type = IdType.AUTO)
    private Integer recordId;
    
    @TableField("user_id")
    private Integer userId;
    
    @TableField("record_date")
    private String recordDate;
    
    @TableField("record_type")
    private String recordType;
    
    @TableField("plan_name")
    private String planName;
    
    @TableField("total_calories")
    private Double totalCalories;
    
    @TableField("total_protein")
    private Double totalProtein;
    
    @TableField("total_fat")
    private Double totalFat;
    
    @TableField("total_carbs")
    private Double totalCarbs;
    
    @TableField("weight")
    private Double weight;
    
    @TableField("notes")
    private String notes;
    
    @TableField(exist = false)
    private List<Meal> meals;
} 