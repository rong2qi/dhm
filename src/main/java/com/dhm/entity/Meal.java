package com.dhm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 餐次实体类
 */
@Data
@TableName("meal")
public class Meal {
    @TableId(value = "meal_id", type = IdType.AUTO)
    private Integer mealId;
    
    @TableField("record_id")
    private Integer recordId;
    
    @TableField("meal_type")
    private String mealType;
    
    @TableField("meal_time")
    private String mealTime;
    
    @TableField("notes")
    private String notes;
    
    @TableField(exist = false)
    private List<MealFood> mealFoods;
} 